package org.eclipse.jnosql.databases.questdb.communication;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;

import io.quarkiverse.jnosql.core.runtime.AbstractDatabaseManagerProducer;

@Singleton
public class QuarkusQuestDBDatabaseManagerProducer extends
        AbstractDatabaseManagerProducer<QuestDBTimeSeriesManager, QuestDBTimeSeriesManagerFactory, QuestDBTimeSeriesConfiguration> {

    private QuestDBTimeSeriesManagerFactory factory;

    @Produces
    @Priority(1)
    @Alternative
    @Default
    @ApplicationScoped
    @Database(DatabaseType.TIME_SERIES)
    public synchronized QuestDBTimeSeriesManager get() {
        factory = createFactory();
        return get(factory, MappingConfigurations.TIME_SERIES_DATABASE);
    }

    @PreDestroy
    void close() {
        if (factory != null) {
            factory.close();
        }
    }
}
