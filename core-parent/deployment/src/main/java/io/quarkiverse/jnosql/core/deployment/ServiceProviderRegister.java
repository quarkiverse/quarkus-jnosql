package io.quarkiverse.jnosql.core.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import io.quarkus.deployment.util.ServiceUtil;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Utility class to register service providers
 */
public interface ServiceProviderRegister {


    /**
      * Register a service provider for a given service interface.
      *
      * @param services the build producer
      * @param serviceInterface the service interface
      */
     static void registerService(BuildProducer<ServiceProviderBuildItem> services, Class<?>... serviceInterface) {
         for (Class<?> service : serviceInterface) {
             registerService(services, service);
         }
     }

    /**
     * Register a service provider for a given service interface.
     *
     * @param services the build producer
     * @param serviceInterface the service interface
     */
    static void registerService(BuildProducer<ServiceProviderBuildItem> services, Class<?> serviceInterface) {
        try {
            var serviceInterfaceName = serviceInterface.getName();
            var service = "META-INF/services/" + serviceInterfaceName;
            // find out all the implementation classes listed in the service files
            var implementations = ServiceUtil.classNamesNamedIn(Thread.currentThread().getContextClassLoader(),
                    service);
            // register every listed implementation class so they can be instantiated
            // in native-image at run-time
            services.produce(
                    new ServiceProviderBuildItem(serviceInterfaceName,
                            implementations.toArray(new String[0])));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Consumer<? super Class<?>> registerInto(BuildProducer<ServiceProviderBuildItem> services) {
        return clazz -> registerService(services, clazz);
    }
}
