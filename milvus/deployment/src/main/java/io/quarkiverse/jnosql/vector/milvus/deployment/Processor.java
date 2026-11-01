package io.quarkiverse.jnosql.vector.milvus.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageAllowIncompleteClasspathBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedPackageBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-milvus";

    private static final String SHADED_GRPC = "io.milvus.shaded.io.grpc.";

    private static final String NETTY = "netty.shaded.io.netty.";

    // The bundled native-image.properties of the SDK refer to the original package names, not the relocated ones.
    private static final String[] RUNTIME_INITIALIZED = {
            "netty.shaded.io.grpc.netty.GrpcSslContexts",
            "netty.shaded.io.grpc.netty.Utils$ByteBufAllocatorPreferDirectHolder",
            "netty.shaded.io.grpc.netty.Utils$ByteBufAllocatorPreferHeapHolder",
            "internal.RetriableStream",
            NETTY + "buffer.PooledByteBufAllocator",
            NETTY + "buffer.ByteBufAllocator",
            NETTY + "buffer.ByteBufUtil",
            NETTY + "buffer.AbstractReferenceCountedByteBuf",
            NETTY + "buffer.UnpooledByteBufAllocator",
            NETTY + "buffer.Unpooled",
            NETTY + "util.AbstractReferenceCounted",
            NETTY + "util.concurrent.GlobalEventExecutor",
            NETTY + "util.concurrent.ImmediateEventExecutor",
            NETTY + "util.concurrent.ScheduledFutureTask",
            NETTY + "util.internal.ThreadLocalRandom",
            NETTY + "handler.ssl.util.ThreadLocalInsecureRandom",
            NETTY + "channel.unix.Limits",
            NETTY + "channel.unix.IovArray",
            NETTY + "channel.unix.Errors",
            NETTY + "handler.codec.http2.Http2CodecUtil",
            NETTY + "handler.codec.http2.Http2ClientUpgradeCodec",
            NETTY + "handler.codec.http2.Http2ConnectionHandler",
            NETTY + "handler.codec.http2.DefaultHttp2FrameWriter",
            NETTY + "handler.codec.http.HttpObjectEncoder",
            NETTY + "handler.codec.http.websocketx.WebSocket00FrameEncoder",
            NETTY + "handler.codec.http.websocketx.extensions.compression.DeflateDecoder",
            NETTY + "handler.codec.compression.BrotliDecoder",
            NETTY + "handler.ssl.JdkNpnApplicationProtocolNegotiator"
    };

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    // The Milvus SDK bundles a relocated gRPC/Netty whose TLS classes load native libraries at class initialization.
    @BuildStep
    void shadedNettyRuntimeInitialization(BuildProducer<RuntimeInitializedPackageBuildItem> runtimeInitialized,
            BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitializedClasses) {
        for (String className : RUNTIME_INITIALIZED) {
            runtimeInitializedClasses.produce(new RuntimeInitializedClassBuildItem(SHADED_GRPC + className));
        }
        runtimeInitialized.produce(new RuntimeInitializedPackageBuildItem(
                SHADED_GRPC + "netty.shaded.io.netty.internal.tcnative"));
        for (String pkg : new String[] { "channel.epoll", "handler.codec.http", "handler.proxy", "handler.ssl" }) {
            runtimeInitialized.produce(new RuntimeInitializedPackageBuildItem(SHADED_GRPC + "netty.shaded.io.netty." + pkg));
        }
    }

    // The bundled Netty references optional TLS providers (Conscrypt, Jetty NPN, BouncyCastle).
    @BuildStep
    NativeImageAllowIncompleteClasspathBuildItem allowIncompleteClasspath() {
        return new NativeImageAllowIncompleteClasspathBuildItem(FEATURE);
    }

    @BuildStep
    void grpcServices(BuildProducer<ServiceProviderBuildItem> services) {
        for (String service : new String[] { "ManagedChannelProvider", "NameResolverProvider",
                "LoadBalancerProvider" }) {
            services.produce(ServiceProviderBuildItem.allProvidersFromClassPath("io.milvus.shaded.io.grpc." + service));
        }
    }
}
