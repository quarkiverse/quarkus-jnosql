package org.eclipse.jnosql.databases.scylladb.communication;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;

import io.quarkiverse.jnosql.core.runtime.AbstractDatabaseManagerProducer;

@Singleton
public class QuarkusScyllaDBDatabaseManagerProducer extends
        AbstractDatabaseManagerProducer<ScyllaDBColumnManager, ScyllaDBColumnManagerFactory, QuarkusScyllaDBConfiguration> {

    @Produces
    @Singleton
    @Priority(1)
    @Alternative
    @Default
    @Database(DatabaseType.COLUMN)
    @Override
    public ScyllaDBColumnManager get() {
        return get(MappingConfigurations.COLUMN_DATABASE);
    }

    public void close(@Disposes @Database(DatabaseType.COLUMN) ScyllaDBColumnManager manager) {
        manager.close();
    }
}
