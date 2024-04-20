package org.eclipse.jnosql.databases.cassandra.communication;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;
import org.eclipse.jnosql.communication.semistructured.DatabaseManagerFactory;

import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;

@Singleton
public class QuarkusCassandraConfiguration implements DatabaseConfiguration {

    @Inject
    protected QuarkusCqlSession quarkusCqlSession;

    @Override
    public DatabaseManagerFactory apply(Settings settings) throws NullPointerException {
        return new QuarkusCassandraColumnManagerFactory(
                quarkusCqlSession);
    }

}
