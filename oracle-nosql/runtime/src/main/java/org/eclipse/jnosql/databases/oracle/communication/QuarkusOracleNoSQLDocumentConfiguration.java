package org.eclipse.jnosql.databases.oracle.communication;

import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;
import org.eclipse.jnosql.communication.semistructured.DatabaseManagerFactory;

@Singleton
public class QuarkusOracleNoSQLDocumentConfiguration implements DatabaseConfiguration {

    @Override
    public DatabaseManagerFactory apply(Settings settings) {
        return new OracleDocumentConfiguration().apply(settings);
    }
}
