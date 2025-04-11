package io.quarkiverse.jnosql.core.runtime;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.nosql.MappingException;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.BucketManager;
import org.eclipse.jnosql.communication.keyvalue.BucketManagerFactory;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;
import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;

public abstract class AbstractBucketManagerProducer<M extends BucketManager, F extends BucketManagerFactory, C extends KeyValueConfiguration>
        implements Supplier<M> {

    protected final Settings settings = new MicroProfileSettings();

    @Inject
    protected C configuration;

    public M get(MappingConfigurations databaseSetting) {

        var factory = configuration.apply(settings);

        var database = settings.get(databaseSetting, String.class)
                .orElseThrow(() -> new MappingException("Please, inform the database filling up the property "
                        + databaseSetting));

        var manager = factory.apply(database);

        Logger.getLogger(getClass().getName()).log(
                Level.FINEST,
                String.format(
                        "Starting a %s instance using Eclipse MicroProfile Config, database name: %s",
                        manager.getClass().getName(),
                        database));

        return (M) manager;
    }

}
