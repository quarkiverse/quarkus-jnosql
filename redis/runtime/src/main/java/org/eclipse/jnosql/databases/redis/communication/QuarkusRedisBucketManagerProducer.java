package org.eclipse.jnosql.databases.redis.communication;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;

import io.quarkiverse.jnosql.core.runtime.AbstractBucketManagerProducer;

@Singleton
public class QuarkusRedisBucketManagerProducer extends
        AbstractBucketManagerProducer<RedisBucketManager, RedisBucketManagerFactory, QuarkusRedisKeyValueConfiguration> {
    @Override
    @Produces
    @Alternative
    @Priority(1)
    @Default
    public RedisBucketManager get() {
        return get(MappingConfigurations.KEY_VALUE_DATABASE);
    }
}
