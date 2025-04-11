package org.eclipse.jnosql.databases.dynamodb.communication;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.keyvalue.BucketManagerFactory;
import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;

import io.quarkiverse.jnosql.core.runtime.AbstractBucketManagerProducer;

@Singleton
public class QuarkusDynamoDBBucketManagerProducer extends
        AbstractBucketManagerProducer<DynamoDBBucketManager, BucketManagerFactory, QuarkusDynamoDBKeyValueConfiguration> {
    @Override
    @Produces
    @Priority(1)
    @Alternative
    @Default
    public DynamoDBBucketManager get() {
        return get(MappingConfigurations.KEY_VALUE_DATABASE);
    }
}
