package io.quarkiverse.jnosql.vector.weaviate.deployment;

import org.jboss.jandex.ClassInfo;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageAllowIncompleteClasspathBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedPackageBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-weaviate";

    private static final String CLIENT_PACKAGE = "io.weaviate.client6.";

    private static final String SHADED_GRPC = "io.grpc.netty.shaded.io.";

    private static final String[] RUNTIME_INITIALIZED = {
            SHADED_GRPC + "grpc.netty.Utils",
            SHADED_GRPC + "grpc.netty.NettyChannelBuilder",
            SHADED_GRPC + "grpc.netty.Utils$ByteBufAllocatorPreferDirectHolder",
            SHADED_GRPC + "grpc.netty.Utils$ByteBufAllocatorPreferHeapHolder",
            SHADED_GRPC + "netty.handler.ssl.JdkNpnApplicationProtocolNegotiator",
            "com.nimbusds.oauth2.sdk.http.HTTPRequest"
    };

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    // The Weaviate client uses grpc-netty-shaded, which references optional classes (Conscrypt, Jetty NPN,
    // Zstd) that are not on the classpath.
    @BuildStep
    NativeImageAllowIncompleteClasspathBuildItem allowIncompleteClasspath() {
        return new NativeImageAllowIncompleteClasspathBuildItem(FEATURE);
    }

    @BuildStep
    void grpcRuntimeInitialization(BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitialized) {
        for (String className : RUNTIME_INITIALIZED) {
            runtimeInitialized.produce(new RuntimeInitializedClassBuildItem(className));
        }
    }

    @BuildStep
    IndexDependencyBuildItem indexClient() {
        return new IndexDependencyBuildItem("io.weaviate", "client6");
    }

    // The client deserializes its REST payloads (including records) with Gson, which relies on reflection.
    @BuildStep
    void clientReflection(CombinedIndexBuildItem index, BuildProducer<ReflectiveClassBuildItem> reflective) {
        String[] classes = index.getIndex().getKnownClasses().stream()
                .map(ClassInfo::name)
                .map(Object::toString)
                .filter(name -> name.startsWith(CLIENT_PACKAGE))
                .toArray(String[]::new);
        reflective.produce(ReflectiveClassBuildItem.builder(classes).constructors().methods().fields().build());
    }

    // Epoll and TLS availability must be detected at run time, not frozen into the image.
    @BuildStep
    void nettyNativeTransports(BuildProducer<RuntimeInitializedPackageBuildItem> runtimeInitialized) {
        for (String pkg : new String[] { "channel.epoll", "internal.tcnative" }) {
            runtimeInitialized.produce(new RuntimeInitializedPackageBuildItem(SHADED_GRPC + "netty." + pkg));
        }
    }
}
