package org.eclipse.jnosql.databases.mongodb.communication;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;

import io.quarkiverse.jnosql.core.runtime.AbstractDatabaseManagerProducer;

@Singleton
public class QuarkusMongoDBDatabaseManagerProducer extends
        AbstractDatabaseManagerProducer<MongoDBDocumentManager, MongoDBDocumentManagerFactory, QuarkusMongoDBDocumentConfiguration> {

    private static final Logger LOGGER = Logger.getLogger(QuarkusMongoDBDatabaseManagerProducer.class.getName());

    @Produces
    @Priority(1)
    @Alternative
    @Default
    @Database(DatabaseType.DOCUMENT)
    public MongoDBDocumentManager get() {
        LOGGER.log(Level.FINEST, "creating a MongoDBDocumentManager");
        return get(MappingConfigurations.DOCUMENT_DATABASE);
    }

}
