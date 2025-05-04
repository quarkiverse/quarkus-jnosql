package io.quarkiverse.jnosql.dynamodb.deployment;

import org.eclipse.jnosql.databases.dynamodb.communication.QuarkusDynamoDBBucketManagerProducer;
import org.eclipse.jnosql.databases.dynamodb.communication.QuarkusDynamoDBKeyValueConfiguration;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-dynamodb";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusDynamoDBKeyValueConfiguration.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusDynamoDBBucketManagerProducer.class));
    }

}
