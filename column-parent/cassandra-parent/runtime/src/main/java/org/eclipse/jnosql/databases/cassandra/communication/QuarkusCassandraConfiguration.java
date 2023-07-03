package org.eclipse.jnosql.databases.cassandra.communication;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.column.ColumnConfiguration;
import org.eclipse.jnosql.communication.column.ColumnManagerFactory;

import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;

@Singleton
public class QuarkusCassandraConfiguration implements ColumnConfiguration {

    @Inject
    protected QuarkusCqlSession quarkusCqlSession;

    @Override
    public ColumnManagerFactory apply(Settings settings) throws NullPointerException {
        return new QuarkusCassandraColumnManagerFactory(quarkusCqlSession);
    }

}
