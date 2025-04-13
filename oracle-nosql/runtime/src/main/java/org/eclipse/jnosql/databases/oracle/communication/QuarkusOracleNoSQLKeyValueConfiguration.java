package org.eclipse.jnosql.databases.oracle.communication;

import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.BucketManagerFactory;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;

@Singleton
public class QuarkusOracleNoSQLKeyValueConfiguration implements KeyValueConfiguration {

    @Override
    public BucketManagerFactory apply(Settings settings) throws NullPointerException {
        return new OracleNoSQLKeyValueConfiguration().apply(settings);
    }

}
