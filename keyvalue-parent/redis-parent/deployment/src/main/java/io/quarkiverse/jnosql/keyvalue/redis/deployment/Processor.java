package io.quarkiverse.jnosql.keyvalue.redis.deployment;

import org.eclipse.jnosql.databases.redis.communication.QuarkusRedisKeyValueConfiguration;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-keyvalue-redis";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusRedisKeyValueConfiguration.class));
    }

    @BuildStep
    void markRuntimeInitializedClasses(BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitializedClassesProducer) {

        runtimeInitializedClassesProducer.produce(
                new RuntimeInitializedClassBuildItem(
                        org.eclipse.jnosql.databases.redis.communication.RedisConfiguration.class.getName()));
    }

}
