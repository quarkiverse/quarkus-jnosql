package io.quarkiverse.jnosql.keyvalue.infinispan.deployment;

import org.eclipse.jnosql.databases.infinispan.communication.InfinispanConfigurations;
import org.eclipse.jnosql.databases.infinispan.communication.QuarkusInfinispanBucketManagerFactoryProducer;
import org.eclipse.jnosql.databases.infinispan.communication.QuarkusInfinispanBucketManagerProducer;
import org.eclipse.jnosql.databases.infinispan.communication.QuarkusInfinispanKeyValueConfiguration;
import org.eclipse.microprofile.config.ConfigProvider;
import org.infinispan.configuration.parsing.ConfigurationParser;
import org.infinispan.factories.impl.ModuleMetadataBuilder;
import org.infinispan.protostream.SerializationContextInitializer;

import io.quarkiverse.jnosql.core.deployment.ServiceProviderRegister;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-infinispan";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusInfinispanKeyValueConfiguration.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusInfinispanBucketManagerProducer.class));
        additionalBeanProducer
                .produce(AdditionalBeanBuildItem.unremovableOf(QuarkusInfinispanBucketManagerFactoryProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {

        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.keyvalue.configuration.BucketManagerSupplier"));
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.keyvalue.configuration.BucketManagerFactorySupplier"));
    }

    @BuildStep
    void registerNativeServices(BuildProducer<ServiceProviderBuildItem> services) {
        ServiceProviderRegister.registerService(services,
                ConfigurationParser.class, ModuleMetadataBuilder.class, SerializationContextInitializer.class);
    }

    @BuildStep
    void registerConfigurationResource(BuildProducer<NativeImageResourceBuildItem> resources) {
        ConfigProvider.getConfig().getOptionalValue(InfinispanConfigurations.CONFIG.get(), String.class)
                .filter(path -> Thread.currentThread().getContextClassLoader().getResource(path) != null)
                .ifPresent(path -> resources.produce(new NativeImageResourceBuildItem(path)));
    }

    @BuildStep
    void initializeOptionalComponentsAtRuntime(BuildProducer<RuntimeInitializedClassBuildItem> classes) {
        // These classes reference optional libraries which the driver checks for at runtime.
        classes.produce(new RuntimeInitializedClassBuildItem("org.infinispan.remoting.transport.jgroups.JGroupsRaftManager"));
        classes.produce(new RuntimeInitializedClassBuildItem("org.infinispan.metrics.impl.PrometheusRegistry"));
        classes.produce(new RuntimeInitializedClassBuildItem("org.infinispan.metrics.impl.PrometheusSimpleClientRegistry"));
        // The Java 22+ implementation creates process-specific foreign-memory handles.
        classes.produce(new RuntimeInitializedClassBuildItem(
                "org.infinispan.commons.jdkspecific.UnsafeMemoryAddressOffHeapMemory"));
    }

}
