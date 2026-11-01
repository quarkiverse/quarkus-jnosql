package io.quarkiverse.jnosql.vector.deployment;

import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;

import io.quarkiverse.jnosql.core.deployment.ServiceProviderRegister;
import io.quarkiverse.jnosql.vector.runtime.QuarkusVectorManagerProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-vector";

    private static final String[] GRPC_SERVICES = {
            "io.grpc.ManagedChannelProvider",
            "io.grpc.NameResolverProvider",
            "io.grpc.LoadBalancerProvider"
    };

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusVectorManagerProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.vector.configuration.VectorManagerSupplier"));
    }

    @BuildStep
    void registerServices(BuildProducer<ServiceProviderBuildItem> services) {
        ServiceProviderRegister.registerService(services, DatabaseConfiguration.class);
        // gRPC-based drivers discover channel, resolver and balancer providers via ServiceLoader.
        for (String grpcService : GRPC_SERVICES) {
            services.produce(ServiceProviderBuildItem.allProvidersFromClassPath(grpcService));
        }
    }

    @BuildStep
    void grpcRuntimeInitialization(BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitialized) {
        runtimeInitialized
                .produce(new RuntimeInitializedClassBuildItem("io.grpc.netty.Utils$ByteBufAllocatorPreferDirectHolder"));
        runtimeInitialized
                .produce(new RuntimeInitializedClassBuildItem("io.grpc.netty.Utils$ByteBufAllocatorPreferHeapHolder"));
    }
}
