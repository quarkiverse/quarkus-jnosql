package org.eclipse.jnosql.communication.mongodb.document;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.document.DocumentConfiguration;

import com.mongodb.client.MongoClient;

@Singleton
public class QuarkusMongoDBDocumentConfiguration implements DocumentConfiguration {

    @Inject
    protected MongoClient mongoClient;

    @Override
    public MongoDBDocumentManagerFactory apply(Settings settings) throws NullPointerException {
        return new MongoDBDocumentManagerFactory(this.mongoClient);
    }

}
