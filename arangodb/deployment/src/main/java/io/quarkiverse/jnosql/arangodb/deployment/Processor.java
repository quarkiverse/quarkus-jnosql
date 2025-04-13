package io.quarkiverse.jnosql.arangodb.deployment;

import org.eclipse.jnosql.databases.arangodb.communication.QuarkusArangoDBBucketManagerProducer;
import org.eclipse.jnosql.databases.arangodb.communication.QuarkusArangoDBDocumentConfiguration;
import org.eclipse.jnosql.databases.arangodb.communication.QuarkusArangoDBDocumentManagerProducer;
import org.eclipse.jnosql.databases.arangodb.communication.QuarkusArangoDBKeyValueConfiguration;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-arangodb";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusArangoDBDocumentConfiguration.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusArangoDBKeyValueConfiguration.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusArangoDBDocumentManagerProducer.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusArangoDBBucketManagerProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.document.configuration.DocumentManagerSupplier"));
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.databases.arangodb.mapping.DocumentManagerSupplier"));
    }
}
