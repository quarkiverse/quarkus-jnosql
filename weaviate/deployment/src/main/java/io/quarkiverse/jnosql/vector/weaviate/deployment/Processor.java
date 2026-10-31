package io.quarkiverse.jnosql.vector.weaviate.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageAllowIncompleteClasspathBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-weaviate";

    private static final String SHADED_GRPC = "io.grpc.netty.shaded.io.";

    private static final String[] RUNTIME_INITIALIZED = {
            SHADED_GRPC + "grpc.netty.Utils$ByteBufAllocatorPreferDirectHolder",
            SHADED_GRPC + "grpc.netty.Utils$ByteBufAllocatorPreferHeapHolder",
            SHADED_GRPC + "netty.handler.ssl.JdkNpnApplicationProtocolNegotiator"
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
}
