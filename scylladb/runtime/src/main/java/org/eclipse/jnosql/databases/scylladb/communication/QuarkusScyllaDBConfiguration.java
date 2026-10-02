package org.eclipse.jnosql.databases.scylladb.communication;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;

import io.quarkiverse.jnosql.core.runtime.MicroProfileSettings;

@Singleton
public class QuarkusScyllaDBConfiguration implements DatabaseConfiguration {

    private final ScyllaDBConfiguration configuration = new ScyllaDBConfiguration();

    private ScyllaDBColumnManagerFactory factory;

    @Override
    public synchronized ScyllaDBColumnManagerFactory apply(Settings settings) {
        if (factory == null) {
            factory = configuration.apply(settings);
        }
        return factory;
    }

    @Produces
    @Singleton
    public ScyllaDBColumnManagerFactory factory() {
        return apply(new MicroProfileSettings());
    }
}
