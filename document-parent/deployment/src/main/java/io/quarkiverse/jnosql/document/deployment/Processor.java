package io.quarkiverse.jnosql.document.deployment;

import java.io.IOException;

import org.eclipse.jnosql.communication.document.DocumentConfiguration;

import io.quarkiverse.jnosql.core.deployment.ServiceProviderRegister;
import io.quarkiverse.jnosql.document.runtime.DocumentManagerProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-document";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(DocumentManagerProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.document.configuration.DocumentManagerSupplier"));
    }

    @BuildStep
    void registerNativeImageResources(BuildProducer<ServiceProviderBuildItem> services)
            throws IOException {
        ServiceProviderRegister.registerService(services, DocumentConfiguration.class);
    }

}
