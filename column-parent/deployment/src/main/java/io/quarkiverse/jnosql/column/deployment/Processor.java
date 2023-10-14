package io.quarkiverse.jnosql.column.deployment;

import io.quarkiverse.jnosql.column.runtime.ColumnManagerProducer;
import io.quarkiverse.jnosql.core.deployment.ServiceProviderRegister;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import org.eclipse.jnosql.communication.column.ColumnConfiguration;

import java.io.IOException;

class Processor {

    private static final String FEATURE = "jnosql-column";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(ColumnManagerProducer.class));
    }

    @BuildStep
    void registerNativeImageResources(BuildProducer<ServiceProviderBuildItem> services) throws IOException {
        ServiceProviderRegister.registerService(services, ColumnConfiguration.class);
    }

}
