package io.quarkiverse.jnosql.core.deployment;

import io.quarkiverse.jnosql.core.runtime.EntityMetadataExtensionProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;

class Processor {

    private static final String FEATURE = "jnosql";

    private static final String JNOSQL_COMMUNICATION_GROUP_ID = "org.eclipse.jnosql.communication";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(CurateOutcomeBuildItem curateOutcomeBuildItem,
            BuildProducer<IndexDependencyBuildItem> indexDependencyBuildItemProducer) {
        curateOutcomeBuildItem.getApplicationModel().getDependencies().stream()
                .filter(dependency -> JNOSQL_COMMUNICATION_GROUP_ID.equals(dependency.getGroupId()))
                .forEach(dependency -> indexDependencyBuildItemProducer
                        .produce(new IndexDependencyBuildItem(dependency.getGroupId(), dependency.getArtifactId())));
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(EntityMetadataExtensionProducer.class));
    }

}
