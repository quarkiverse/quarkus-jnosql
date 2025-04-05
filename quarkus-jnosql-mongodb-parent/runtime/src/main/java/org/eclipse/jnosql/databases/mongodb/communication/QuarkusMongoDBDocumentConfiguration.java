package org.eclipse.jnosql.databases.mongodb.communication;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;

import com.mongodb.client.MongoClient;

@Singleton
public class QuarkusMongoDBDocumentConfiguration implements DatabaseConfiguration {

    private static final Logger LOGGER = Logger.getLogger(QuarkusMongoDBDocumentConfiguration.class.getName());

    @Inject
    protected MongoClient client;

    @Override
    public MongoDBDocumentManagerFactory apply(Settings settings) {
        LOGGER.log(Level.FINEST, "initializing from QuarkusMongoDBDocumentConfiguration");
        return new MongoDBDocumentManagerFactory(this.client);
    }

}
