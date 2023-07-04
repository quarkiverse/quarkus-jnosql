package org.eclipse.jnosql.databases.cassandra.communication;

import java.util.concurrent.Executor;

import org.eclipse.jnosql.communication.column.ColumnManagerFactory;

import com.datastax.oss.driver.api.core.CqlSession;

public class QuarkusCassandraColumnManagerFactory implements ColumnManagerFactory {

    private final CqlSession cqlSession;
    private final Executor executor;

    public QuarkusCassandraColumnManagerFactory(
            CqlSession cqlSession,
            Executor executor) {
        this.cqlSession = cqlSession;
        this.executor = executor;
    }

    @Override
    public void close() {

    }

    @Override
    public CassandraColumnManager apply(String keyspace) {
        return new DefaultCassandraColumnManager(
                cqlSession,
                executor,
                keyspace);
    }
}
