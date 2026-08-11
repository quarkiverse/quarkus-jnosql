package io.quarkiverse.jnosql.keyvalue.redis.deployment;

import org.eclipse.jnosql.databases.redis.communication.QuarkusRedisBucketManagerFactoryProducer;
import org.eclipse.jnosql.databases.redis.communication.QuarkusRedisBucketManagerProducer;
import org.eclipse.jnosql.databases.redis.communication.QuarkusRedisKeyValueConfiguration;
import org.eclipse.jnosql.databases.redis.communication.RedisConfiguration;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-redis";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusRedisKeyValueConfiguration.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusRedisBucketManagerProducer.class));
        additionalBeanProducer
                .produce(AdditionalBeanBuildItem.unremovableOf(QuarkusRedisBucketManagerFactoryProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {

        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.keyvalue.configuration.BucketManagerSupplier"));
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.keyvalue.configuration.BucketManagerFactorySupplier"));
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.databases.redis.mapping.BucketManagerFactorySupplier"));
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.databases.redis.mapping.CollectionSupplier"));
    }

    @BuildStep
    void markRuntimeInitializedClasses(BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitializedClassesProducer) {
        runtimeInitializedClassesProducer
                .produce(new RuntimeInitializedClassBuildItem(RedisConfiguration.class.getName()));
    }

}
