package org.eclipse.jnosql.databases.cassandra.communication;

import com.datastax.oss.driver.api.core.CqlSession;

import java.util.concurrent.Executors;

public class QuarkusCassandraColumnManager extends DefaultCassandraColumnManager {

    public QuarkusCassandraColumnManager(CqlSession cqlSession, String keyspace) {
        super(cqlSession, Executors.newCachedThreadPool(), keyspace);
    }

    @Override
    public void close() {
        // don't close the cqlSession because it's managed by another component
    }
}
