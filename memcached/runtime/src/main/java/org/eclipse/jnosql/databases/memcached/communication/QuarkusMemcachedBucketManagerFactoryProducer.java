package org.eclipse.jnosql.databases.memcached.communication;

import java.util.function.Supplier;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.keyvalue.BucketManagerFactory;

import io.quarkiverse.jnosql.core.runtime.MicroProfileSettings;

@Singleton
public class QuarkusMemcachedBucketManagerFactoryProducer implements Supplier<BucketManagerFactory> {

    @Inject
    protected QuarkusMemcachedKeyValueConfiguration configuration;

    @Override
    @Produces
    @Alternative
    @Priority(1)
    @Default
    public MemcachedBucketManagerFactory get() {
        return configuration.apply(new MicroProfileSettings());
    }

}
