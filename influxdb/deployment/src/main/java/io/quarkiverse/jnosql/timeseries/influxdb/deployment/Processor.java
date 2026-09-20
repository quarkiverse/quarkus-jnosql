package io.quarkiverse.jnosql.timeseries.influxdb.deployment;

import org.eclipse.jnosql.databases.influxdb.communication.InfluxDBTimeSeriesConfiguration;
import org.eclipse.jnosql.databases.influxdb.communication.QuarkusInfluxDBDatabaseManagerProducer;

import io.grpc.LoadBalancerProvider;
import io.grpc.NameResolverProvider;
import io.quarkiverse.jnosql.core.deployment.ServiceProviderRegister;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-influxdb";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(InfluxDBTimeSeriesConfiguration.class));
        additionalBeanProducer.produce(
                AdditionalBeanBuildItem.unremovableOf(QuarkusInfluxDBDatabaseManagerProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem(
                        "org.eclipse.jnosql.mapping.timeseries.configuration.TimeSeriesManagerSupplier"));
    }

    @BuildStep
    void configureNativeImage(BuildProducer<ServiceProviderBuildItem> services,
            BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitializedClasses) {
        ServiceProviderRegister.registerService(services,
                LoadBalancerProvider.class,
                NameResolverProvider.class);
        runtimeInitializedClasses.produce(
                new RuntimeInitializedClassBuildItem("io.grpc.netty.Utils$ByteBufAllocatorPreferHeapHolder"));
        runtimeInitializedClasses.produce(
                new RuntimeInitializedClassBuildItem("io.grpc.netty.Utils$ByteBufAllocatorPreferDirectHolder"));
        runtimeInitializedClasses.produce(
                new RuntimeInitializedClassBuildItem("org.apache.arrow.memory.netty.NettyAllocationManager"));
    }
}
