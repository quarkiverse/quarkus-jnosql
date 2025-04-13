package org.eclipse.jnosql.databases.oracle.communication;

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
public class QuarkusOracleNoSQLDocumentManagerProducer extends
        AbstractDatabaseManagerProducer<OracleNoSQLDocumentManager, OracleDocumentManagerFactory, QuarkusOracleNoSQLDocumentConfiguration> {

    @Produces
    @Priority(1)
    @Alternative
    @Default
    @Database(DatabaseType.DOCUMENT)
    public OracleNoSQLDocumentManager get() {
        return get(MappingConfigurations.DOCUMENT_DATABASE);
    }

}
