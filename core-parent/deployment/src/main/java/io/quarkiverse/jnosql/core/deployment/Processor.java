package io.quarkiverse.jnosql.core.deployment;

import static org.jboss.jandex.AnnotationTarget.Kind.CLASS;

import jakarta.nosql.Entity;

import org.jboss.jandex.Type;

import io.quarkiverse.jnosql.core.runtime.QuarkusEntityMetadataExtension;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyBuildItem;

class Processor {

    private static final String FEATURE = "jnosql";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(CombinedIndexBuildItem index, BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchyBuildItemProducer) {
        index.getIndex().getAnnotations(Entity.class).stream().filter(
                annotationIntance -> CLASS.equals(annotationIntance.target().kind())).forEach(
                        annotationIntance -> reflectiveHierarchyBuildItemProducer.produce(
                                new ReflectiveHierarchyBuildItem.Builder().type(Type.create(
                                        annotationIntance.target().asClass().name(),
                                        Type.Kind.CLASS)).build()));
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusEntityMetadataExtension.class));
    }

}
