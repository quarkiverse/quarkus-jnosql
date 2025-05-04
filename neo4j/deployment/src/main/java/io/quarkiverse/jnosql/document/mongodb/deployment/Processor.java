package io.quarkiverse.jnosql.document.mongodb.deployment;

import org.eclipse.jnosql.databases.neo4j.communication.QuarkusNeo4jDatabaseManagerProducer;
import org.eclipse.jnosql.databases.neo4j.communication.QuarkusNeo4jDocumentConfiguration;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-neo4j";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusNeo4jDocumentConfiguration.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusNeo4jDatabaseManagerProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.document.configuration.DocumentManagerSupplier"));
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.databases.neo4j.mapping.GraphManagerSupplier"));
    }

}
