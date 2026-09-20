package io.quarkiverse.jnosql.timeseries.iotdb.deployment;

import org.eclipse.jnosql.databases.iotdb.communication.IoTDBTimeSeriesConfiguration;
import org.eclipse.jnosql.databases.iotdb.communication.QuarkusIoTDBDatabaseManagerProducer;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-iotdb";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(IoTDBTimeSeriesConfiguration.class));
        additionalBeanProducer.produce(
                AdditionalBeanBuildItem.unremovableOf(QuarkusIoTDBDatabaseManagerProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem(
                        "org.eclipse.jnosql.mapping.timeseries.configuration.TimeSeriesManagerSupplier"));
    }
}
