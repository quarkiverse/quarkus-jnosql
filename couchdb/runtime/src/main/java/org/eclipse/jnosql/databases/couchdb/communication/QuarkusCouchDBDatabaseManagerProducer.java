package org.eclipse.jnosql.databases.couchdb.communication;

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
public class QuarkusCouchDBDatabaseManagerProducer extends
        AbstractDatabaseManagerProducer<CouchDBDocumentManager, CouchDBDocumentManagerFactory, QuarkusCouchDBDocumentConfiguration> {

    @Produces
    @Priority(1)
    @Alternative
    @Default
    @Database(DatabaseType.DOCUMENT)
    public CouchDBDocumentManager get() {
        return get(MappingConfigurations.DOCUMENT_DATABASE);
    }

}
