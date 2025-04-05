package io.quarkiverse.jnosql.column.cassandra.deployment;

import org.eclipse.jnosql.databases.cassandra.communication.QuarkusCassandraConfiguration;
import org.eclipse.jnosql.databases.cassandra.communication.QuarkusCassandraDatabaseManagerProducer;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-cassandra";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusCassandraDatabaseManagerProducer.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusCassandraConfiguration.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.column.configuration.ColumnManagerSupplier"));
    }

}
