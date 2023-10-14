package io.quarkiverse.jnosql.keyvalue.deployment;

import java.io.IOException;

import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;

import io.quarkiverse.jnosql.core.deployment.ServiceProviderRegister;
import io.quarkiverse.jnosql.keyvalue.runtime.BucketManagerProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import io.quarkus.deployment.util.ServiceUtil;

class Processor {

    private static final String FEATURE = "jnosql-keyvalue";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(BucketManagerProducer.class));
    }

    @BuildStep
    void registerNativeImageResources(BuildProducer<ServiceProviderBuildItem> services) throws IOException {
        ServiceProviderRegister.registerService(services, KeyValueConfiguration.class);
    }

    private static void registerService(BuildProducer<ServiceProviderBuildItem> services, String serviceInterface) {
        try {
            var service = "META-INF/services/" + serviceInterface;
            // find out all the implementation classes listed in the service files
            var implementations = ServiceUtil.classNamesNamedIn(Thread.currentThread().getContextClassLoader(),
                    service);
            // register every listed implementation class so they can be instantiated
            // in native-image at run-time
            services.produce(
                    new ServiceProviderBuildItem(serviceInterface,
                            implementations.toArray(new String[0])));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
