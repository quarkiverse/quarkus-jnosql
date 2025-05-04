package io.quarkiverse.jnosql.core.deployment;

import static org.jboss.jandex.AnnotationTarget.Kind.CLASS;

import jakarta.nosql.Embeddable;
import jakarta.nosql.Entity;

import org.eclipse.jnosql.communication.TypeReferenceReader;
import org.eclipse.jnosql.communication.ValueReader;
import org.eclipse.jnosql.communication.ValueWriter;
import org.eclipse.jnosql.mapping.metadata.ClassConverter;
import org.eclipse.jnosql.mapping.metadata.ClassScanner;
import org.eclipse.jnosql.mapping.metadata.CollectionSupplier;
import org.eclipse.jnosql.mapping.metadata.ConstructorBuilderSupplier;
import org.jboss.jandex.Type;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;

class Processor {

    private static final String FEATURE = "jnosql";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void buildReflectiveHierarchy(CombinedIndexBuildItem index,
            BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchyBuildItemProducer) {
        index.getIndex().getAnnotations(Entity.class).stream().filter(
                annotationInstance -> CLASS.equals(annotationInstance.target().kind())).forEach(
                        annotationInstance -> reflectiveHierarchyBuildItemProducer.produce(
                                ReflectiveHierarchyBuildItem.builder(Type.create(
                                        annotationInstance.target().asClass().name(),
                                        Type.Kind.CLASS)).build()));
        index.getIndex().getAnnotations(Embeddable.class).stream().filter(
                annotationInstance -> CLASS.equals(annotationInstance.target().kind())).forEach(
                        annotationInstance -> reflectiveHierarchyBuildItemProducer.produce(
                                ReflectiveHierarchyBuildItem.builder(Type.create(
                                        annotationInstance.target().asClass().name(),
                                        Type.Kind.CLASS)).build()));
    }

    @BuildStep
    void registerNativeImageResources(BuildProducer<ServiceProviderBuildItem> services) {
        ServiceProviderRegister.registerService(services,
                ClassScanner.class,
                CollectionSupplier.class,
                ClassConverter.class,
                ConstructorBuilderSupplier.class,
                ValueReader.class,
                ValueWriter.class,
                TypeReferenceReader.class);
    }

}
