package io.quarkiverse.jnosql.document.elasticsearch.deployment;

import io.quarkiverse.jnosql.core.deployment.ServiceProviderRegister;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import jakarta.json.bind.spi.JsonbProvider;
import jakarta.json.spi.JsonProvider;
import org.eclipse.jnosql.databases.elasticsearch.communication.QuarkusElasticseachDocumentConfiguration;

class Processor {

    private static final String FEATURE = "jnosql-document-elasticsearch";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusElasticseachDocumentConfiguration.class));
    }

    @BuildStep
    void registerNativeImageResources(BuildProducer<ServiceProviderBuildItem> services) {
        ServiceProviderRegister.registerService(services,
                JsonProvider.class, JsonbProvider.class);
    }

}
