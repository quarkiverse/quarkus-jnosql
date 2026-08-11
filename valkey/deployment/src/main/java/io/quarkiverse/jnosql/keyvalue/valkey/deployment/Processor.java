package io.quarkiverse.jnosql.keyvalue.valkey.deployment;

import org.eclipse.jnosql.databases.valkey.communication.QuarkusValkeyBucketManagerFactoryProducer;
import org.eclipse.jnosql.databases.valkey.communication.QuarkusValkeyBucketManagerProducer;
import org.eclipse.jnosql.databases.valkey.communication.QuarkusValkeyKeyValueConfiguration;
import org.eclipse.jnosql.databases.valkey.communication.ValkeyConfiguration;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-valkey";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusValkeyKeyValueConfiguration.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusValkeyBucketManagerProducer.class));
        additionalBeanProducer
                .produce(AdditionalBeanBuildItem.unremovableOf(QuarkusValkeyBucketManagerFactoryProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {

        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.keyvalue.configuration.BucketManagerSupplier"));
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.keyvalue.configuration.BucketManagerFactorySupplier"));
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.databases.valkey.mapping.BucketManagerFactorySupplier"));
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.databases.valkey.mapping.CollectionSupplier"));
    }

    @BuildStep
    void markRuntimeInitializedClasses(BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitializedClassesProducer) {
        runtimeInitializedClassesProducer
                .produce(new RuntimeInitializedClassBuildItem(ValkeyConfiguration.class.getName()));
    }

}
