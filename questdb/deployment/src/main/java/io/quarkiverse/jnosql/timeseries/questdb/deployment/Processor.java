package io.quarkiverse.jnosql.timeseries.questdb.deployment;

import org.eclipse.jnosql.databases.questdb.communication.QuarkusQuestDBDatabaseManagerProducer;
import org.eclipse.jnosql.databases.questdb.communication.QuestDBTimeSeriesConfiguration;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.JPMSExportBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveFieldBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedPackageBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-questdb";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuestDBTimeSeriesConfiguration.class));
        additionalBeanProducer.produce(
                AdditionalBeanBuildItem.unremovableOf(QuarkusQuestDBDatabaseManagerProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem(
                        "org.eclipse.jnosql.mapping.timeseries.configuration.TimeSeriesManagerSupplier"));
    }

    @BuildStep
    void configureNativeImage(BuildProducer<RuntimeInitializedPackageBuildItem> runtimeInitializedPackages,
            BuildProducer<NativeImageResourceBuildItem> nativeImageResources,
            BuildProducer<JPMSExportBuildItem> jpmsExports,
            BuildProducer<ReflectiveFieldBuildItem> reflectiveFields) {
        runtimeInitializedPackages.produce(new RuntimeInitializedPackageBuildItem("io.questdb.client"));
        jpmsExports.produce(new JPMSExportBuildItem("java.base", "jdk.internal.math"));
        reflectiveFields.produce(new ReflectiveFieldBuildItem(
                "QuestDB probes the JVM object layout at runtime",
                "io.questdb.client.std.Unsafe$1Probe",
                "intField"));
        nativeImageResources.produce(new NativeImageResourceBuildItem(
                "io/questdb/client/bin/linux-aarch64/libquestdb.so",
                "io/questdb/client/bin/linux-x86-64/libquestdb.so",
                "io/questdb/client/bin/darwin-aarch64/libquestdb.dylib",
                "io/questdb/client/bin/windows-x86-64/libquestdb.dll"));
    }
}
