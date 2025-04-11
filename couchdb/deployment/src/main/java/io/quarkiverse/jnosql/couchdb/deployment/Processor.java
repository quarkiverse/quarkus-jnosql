package io.quarkiverse.jnosql.couchdb.deployment;

import org.eclipse.jnosql.databases.couchdb.communication.QuarkusCouchDBDatabaseManagerProducer;
import org.eclipse.jnosql.databases.couchdb.communication.QuarkusCouchDBDocumentConfiguration;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-couchdb";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusCouchDBDocumentConfiguration.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusCouchDBDatabaseManagerProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.document.configuration.DocumentManagerSupplier"));
    }

}
