package org.eclipse.jnosql.databases.cassandra.communication;

import org.eclipse.jnosql.communication.column.ColumnManagerFactory;

import com.datastax.oss.driver.api.core.CqlSession;

public class QuarkusCassandraColumnManagerFactory implements ColumnManagerFactory {

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
