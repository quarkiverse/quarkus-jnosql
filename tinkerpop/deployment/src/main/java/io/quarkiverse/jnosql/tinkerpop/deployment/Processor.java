package io.quarkiverse.jnosql.tinkerpop.deployment;

import jakarta.inject.Singleton;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.eclipse.jnosql.databases.tinkerpop.mapping.TinkerPopRepository;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import io.quarkiverse.jnosql.tinkerpop.runtime.TinkerpopRepositoryCreator;
import io.quarkus.arc.deployment.BeanDiscoveryFinishedBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-tinkerpop";

    private static final DotName REPOSITORY = DotName.createSimple(TinkerPopRepository.class.getName());

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

    /**
     * Arc reports an unsatisfied {@code Graph} injection point with a generic message during validation,
     * so the missing producer is checked first to give an actionable one.
     */
    @BuildStep
    UnremovableBeanBuildItem keepMappingBeansAndRequireGraph(BeanDiscoveryFinishedBuildItem discovery) {
        boolean present = discovery.beanStream().withBeanType(GRAPH).collect().stream()
                .anyMatch(BeanInfo::hasDefaultQualifiers);
        if (!present) {
            throw new IllegalStateException(MISSING_GRAPH_MESSAGE);
        }
        return UnremovableBeanBuildItem.beanClassNames(
                "org.eclipse.jnosql.databases.tinkerpop.mapping.DefaultTinkerpopTemplate",
                "org.eclipse.jnosql.databases.tinkerpop.mapping.GraphTemplateProducer");
    }

    /**
     * Registers the {@code TinkerPopRepository} interfaces of the application, which the driver would
     * otherwise discover from a portable CDI extension that Arc does not execute.
     */
    @BuildStep
    void registerRepositories(CombinedIndexBuildItem index,
            BuildProducer<SyntheticBeanBuildItem> beans,
            BuildProducer<NativeImageProxyDefinitionBuildItem> proxies,
            BuildProducer<ReflectiveClassBuildItem> reflection) {
        for (ClassInfo repository : index.getIndex().getAllKnownSubinterfaces(REPOSITORY)) {
            String name = repository.name().toString();
            beans.produce(SyntheticBeanBuildItem.configure(repository.name())
                    .scope(Singleton.class)
                    .unremovable()
                    .creator(TinkerpopRepositoryCreator.class)
                    .param(TinkerpopRepositoryCreator.REPOSITORY_TYPE, repository)
                    .done());
            proxies.produce(new NativeImageProxyDefinitionBuildItem(name));
            reflection.produce(ReflectiveClassBuildItem.builder(name).methods().build());
        }
    }
}
