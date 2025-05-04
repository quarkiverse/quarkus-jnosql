package io.quarkiverse.jnosql.elasticsearch.deployment;

import org.eclipse.jnosql.databases.elasticsearch.communication.QuarkusElasticsearchDatabaseManagerProducer;
import org.eclipse.jnosql.databases.elasticsearch.communication.QuarkusElasticsearchDocumentConfiguration;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-elasticsearch";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusElasticsearchDocumentConfiguration.class));
        additionalBeanProducer
                .produce(AdditionalBeanBuildItem.unremovableOf(QuarkusElasticsearchDatabaseManagerProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.document.configuration.DocumentManagerSupplier"));
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.databases.elasticsearch.mapping.DocumentManagerSupplier"));
    }

}
