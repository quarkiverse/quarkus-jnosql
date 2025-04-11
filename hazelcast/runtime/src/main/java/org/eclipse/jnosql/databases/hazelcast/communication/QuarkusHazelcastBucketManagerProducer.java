package org.eclipse.jnosql.databases.hazelcast.communication;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;

import io.quarkiverse.jnosql.core.runtime.AbstractBucketManagerProducer;

@Singleton
public class QuarkusHazelcastBucketManagerProducer extends
        AbstractBucketManagerProducer<HazelcastBucketManager, HazelcastBucketManagerFactory, QuarkusHazelcastKeyValueConfiguration> {
    @Override
    @Produces
    @Alternative
    @Priority(1)
    @Default
    public HazelcastBucketManager get() {
        return get(MappingConfigurations.KEY_VALUE_DATABASE);
    }
}
