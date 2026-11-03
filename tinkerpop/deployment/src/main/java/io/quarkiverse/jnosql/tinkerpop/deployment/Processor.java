package io.quarkiverse.jnosql.tinkerpop.deployment;

import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.jboss.jandex.DotName;

import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.arc.deployment.ValidationPhaseBuildItem;
import io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem;
import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-tinkerpop";

    private static final DotName GRAPH = DotName.createSimple(Graph.class.getName());

    static final String MISSING_GRAPH_MESSAGE = "No CDI bean of type org.apache.tinkerpop.gremlin.structure.Graph was found. "
            + "The quarkus-jnosql-tinkerpop extension does not create a Graph: the application must add the dependency "
            + "of its TinkerPop provider and define a CDI producer for org.apache.tinkerpop.gremlin.structure.Graph, "
            + "for example a @Produces @ApplicationScoped method with a matching @Disposes method.";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    /**
     * The driver's own {@code GraphSupplier} produces a second {@link Graph} from MicroProfile settings.
     * The Graph is owned by the application, so the driver producer is excluded.
     */
    @BuildStep
    void excludeDriverGraphSupplier(BuildProducer<ExcludedTypeBuildItem> excludedTypes) {
        excludedTypes.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.databases.tinkerpop.mapping.configuration.GraphSupplier"));
    }

    @BuildStep
    UnremovableBeanBuildItem keepMappingBeans() {
        return UnremovableBeanBuildItem.beanClassNames(
                "org.eclipse.jnosql.databases.tinkerpop.mapping.DefaultTinkerpopTemplate",
                "org.eclipse.jnosql.databases.tinkerpop.mapping.GraphTemplateProducer");
    }

    @BuildStep
    void validateGraphBean(ValidationPhaseBuildItem validation, BuildProducer<ValidationErrorBuildItem> errors) {
        List<BeanInfo> graphs = validation.getContext().beans().withBeanType(GRAPH).collect();
        if (graphs.stream().noneMatch(BeanInfo::hasDefaultQualifiers)) {
            errors.produce(new ValidationErrorBuildItem(new IllegalStateException(MISSING_GRAPH_MESSAGE)));
        }
    }
}
