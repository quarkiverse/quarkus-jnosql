package io.quarkiverse.jnosql.timeseries.influxdb.deployment;

import org.eclipse.jnosql.databases.influxdb.communication.InfluxDBTimeSeriesConfiguration;
import org.eclipse.jnosql.databases.influxdb.communication.QuarkusInfluxDBDatabaseManagerProducer;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-influxdb";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(InfluxDBTimeSeriesConfiguration.class));
        additionalBeanProducer.produce(
                AdditionalBeanBuildItem.unremovableOf(QuarkusInfluxDBDatabaseManagerProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem(
                        "org.eclipse.jnosql.mapping.timeseries.configuration.TimeSeriesManagerSupplier"));
    }
}
