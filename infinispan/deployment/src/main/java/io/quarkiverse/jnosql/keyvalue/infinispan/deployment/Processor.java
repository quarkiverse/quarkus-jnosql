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
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBundleBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourcePatternsBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
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
    NativeImageResourcePatternsBuildItem registerInfinispanResources() {
        // The XML parser loads default stacks even for local caches; marshallers load bundled schemas.
        return NativeImageResourcePatternsBuildItem.builder()
                .includePattern("org/infinispan/configuration/default-jgroups-.*\\.xml")
                .includePattern("org/infinispan/.*\\.proto")
                .build();
    }

    @BuildStep
    NativeImageResourceBundleBuildItem registerJGroupsMessages() {
        return new NativeImageResourceBundleBuildItem("jg-messages");
    }

    @BuildStep
    ReflectiveClassBuildItem registerDefaultKeyPartitioner() {
        return ReflectiveClassBuildItem.builder("org.infinispan.distribution.ch.impl.HashFunctionPartitioner")
                .constructors()
                .reason("Infinispan copies the default key partitioner while validating cache configuration")
                .build();
    }

    @BuildStep
    ReflectiveClassBuildItem registerRuntimeLoggers() {
        return ReflectiveClassBuildItem.builder(
                "org.infinispan.util.logging.Log_$logger",
                "org.infinispan.commons.logging.Log_$logger")
                .constructors()
                .reason("Runtime-initialized Infinispan components obtain generated JBoss loggers reflectively")
                .build();
    }

    @BuildStep
    ReflectiveClassBuildItem registerMarshallerSubtypes() {
        return ReflectiveClassBuildItem.builder(
                "java.time.ZoneRegion",
                "java.util.Arrays$ArrayList",
                "java.util.ArrayList$SubList",
                "java.util.AbstractList$RandomAccessSubList",
                "java.util.Collections$CopiesList",
                "java.util.Collections$EmptyList",
                "java.util.Collections$SingletonList",
                "java.util.Collections$SynchronizedRandomAccessList",
                "java.util.Collections$UnmodifiableRandomAccessList",
                "java.util.ImmutableCollections$ListN",
                "java.util.ImmutableCollections$List12",
                "java.util.HashMap",
                "java.util.Collections$EmptyMap",
                "java.util.Collections$SingletonMap",
                "java.util.ImmutableCollections$Map1",
                "java.util.ImmutableCollections$MapN",
                "java.util.Collections$EmptySet",
                "java.util.ImmutableCollections$SetN",
                "java.util.ImmutableCollections$Set12",
                "java.util.Collections$SingletonSet",
                "java.util.Collections$SynchronizedSet",
                "java.util.Collections$UnmodifiableSet")
                .constructors(false)
                .reason("ProtoStream resolves subtype names declared by the bundled ZoneId, List, Map and Set adapters")
                .build();
    }

    @BuildStep
    void initializeOptionalComponentsAtRuntime(BuildProducer<RuntimeInitializedClassBuildItem> classes) {
        // These classes reference optional libraries which the driver checks for at runtime.
        classes.produce(new RuntimeInitializedClassBuildItem("org.infinispan.remoting.transport.jgroups.JGroupsRaftManager"));
        classes.produce(new RuntimeInitializedClassBuildItem("org.infinispan.metrics.impl.PrometheusRegistry"));
        classes.produce(new RuntimeInitializedClassBuildItem("org.infinispan.metrics.impl.PrometheusSimpleClientRegistry"));
        // JGroups caches the build host's network interfaces and local address otherwise.
        classes.produce(new RuntimeInitializedClassBuildItem("org.jgroups.util.Util"));
    }

    @BuildStep
    void initializeOffHeapMemoryAtRuntime(BuildProducer<RuntimeInitializedClassBuildItem> classes) {
        // Defer both the allocator and every static MEMORY holder to avoid capturing native handles in the image heap.
        classes.produce(new RuntimeInitializedClassBuildItem(
                "org.infinispan.commons.jdkspecific.UnsafeMemoryAddressOffHeapMemory"));
        classes.produce(new RuntimeInitializedClassBuildItem("org.infinispan.container.offheap.MemoryAddressHash"));
        classes.produce(new RuntimeInitializedClassBuildItem("org.infinispan.container.offheap.OffHeapEntryFactoryImpl"));
        classes.produce(new RuntimeInitializedClassBuildItem("org.infinispan.container.offheap.OffHeapLruNode"));
        classes.produce(
                new RuntimeInitializedClassBuildItem("org.infinispan.container.offheap.UnpooledOffHeapMemoryAllocator"));
    }

}
