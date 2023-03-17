package io.quarkiverse.jnosql.document.deployment;

import org.eclipse.jnosql.communication.document.DocumentConfiguration;

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
    ServiceProviderBuildItem serviceProvider() {
        return ServiceProviderBuildItem.allProvidersFromClassPath(DocumentConfiguration.class.getCanonicalName());
    }

}
