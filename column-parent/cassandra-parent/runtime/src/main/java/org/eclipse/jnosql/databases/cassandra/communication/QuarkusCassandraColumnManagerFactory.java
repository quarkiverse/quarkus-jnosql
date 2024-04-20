package org.eclipse.jnosql.databases.cassandra.communication;

import org.eclipse.jnosql.communication.semistructured.DatabaseManagerFactory;

import com.datastax.oss.driver.api.core.CqlSession;

public class QuarkusCassandraColumnManagerFactory implements DatabaseManagerFactory {

    private final CqlSession cqlSession;

    public QuarkusCassandraColumnManagerFactory(
            CqlSession cqlSession) {
        this.cqlSession = cqlSession;
    }

    @Override
    public void close() {

    }

    @Override
    public CassandraColumnManager apply(String keyspace) {
        return new DefaultCassandraColumnManager(
                cqlSession,
                keyspace);
    }
}
