package io.quarkiverse.jnosql.document.deployment;

import io.quarkiverse.jnosql.document.runtime.EntityMetadataExtensionProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class JNoSQLProcessor {

    private static final String FEATURE = "jnosql";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {

        AdditionalBeanBuildItem beanBuilItem = AdditionalBeanBuildItem.unremovableOf(EntityMetadataExtensionProducer.class);
        additionalBeanProducer.produce(beanBuilItem);

    }

}
