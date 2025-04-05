package org.eclipse.jnosql.databases.mongodb.communication;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;

import com.mongodb.client.MongoClient;

@Singleton
public class QuarkusMongoDBDocumentConfiguration implements DatabaseConfiguration {

    @Inject
    protected MongoClient client;

    @Override
    public MongoDBDocumentManagerFactory apply(Settings settings) {
        return new MongoDBDocumentManagerFactory(this.client);
    }

}
