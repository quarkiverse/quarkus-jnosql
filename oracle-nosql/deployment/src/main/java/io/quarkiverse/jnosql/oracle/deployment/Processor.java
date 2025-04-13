package io.quarkiverse.jnosql.oracle.deployment;

import java.util.stream.Stream;

import org.eclipse.jnosql.databases.oracle.communication.QuarkusOracleNoSQLBucketManagerProducer;
import org.eclipse.jnosql.databases.oracle.communication.QuarkusOracleNoSQLDocumentConfiguration;
import org.eclipse.jnosql.databases.oracle.communication.QuarkusOracleNoSQLDocumentManagerProducer;
import org.eclipse.jnosql.databases.oracle.communication.QuarkusOracleNoSQLKeyValueConfiguration;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-oracle-nosql";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusOracleNoSQLDocumentConfiguration.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusOracleNoSQLKeyValueConfiguration.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusOracleNoSQLDocumentManagerProducer.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusOracleNoSQLBucketManagerProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.document.configuration.DocumentManagerSupplier"));
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.databases.oracle.mapping.DocumentManagerSupplier"));
    }

    @BuildStep
    void markRuntimeInitializedClasses(BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitializedClassesProducer) {
        Stream.of("DefaultSessionKeySupplier",
                "FileKeyPairSupplier",
                "FixedKeyPairSupplier",
                "JDKKeyPairSupplier")
                .map(c -> "oracle.nosql.driver.iam.SessionKeyPairSupplier$" + c)
                .map(RuntimeInitializedClassBuildItem::new)
                .forEach(runtimeInitializedClassesProducer::produce);
    }
}
