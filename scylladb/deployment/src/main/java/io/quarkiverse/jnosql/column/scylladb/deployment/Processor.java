package io.quarkiverse.jnosql.column.scylladb.deployment;

import org.eclipse.jnosql.databases.scylladb.communication.QuarkusScyllaDBConfiguration;
import org.eclipse.jnosql.databases.scylladb.communication.QuarkusScyllaDBDatabaseManagerProducer;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-scylladb";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    RuntimeInitializedClassBuildItem runtimeInitializedMetadata() {
        // The ScyllaDB client resolves its static default contact point during class initialization.
        return new RuntimeInitializedClassBuildItem("com.datastax.oss.driver.internal.core.metadata.MetadataManager");
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusScyllaDBDatabaseManagerProducer.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusScyllaDBConfiguration.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.column.configuration.ColumnManagerSupplier"));
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.databases.scylladb.mapping.ColumnManagerSupplier"));
    }

}
