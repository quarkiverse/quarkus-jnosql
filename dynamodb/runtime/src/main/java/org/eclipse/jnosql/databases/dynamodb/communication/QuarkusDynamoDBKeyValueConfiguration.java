package org.eclipse.jnosql.databases.dynamodb.communication;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Singleton
public class QuarkusDynamoDBKeyValueConfiguration implements KeyValueConfiguration {

    @Inject
    protected DynamoDbClient client;

    @Override
    public DynamoDBBucketManagerFactory apply(Settings settings) throws NullPointerException {
        return new DynamoDBBucketManagerFactory(
                client);
    }

}
