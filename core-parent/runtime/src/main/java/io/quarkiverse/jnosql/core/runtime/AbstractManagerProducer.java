package io.quarkiverse.jnosql.core.runtime;

import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.data.exceptions.MappingException;
import jakarta.inject.Inject;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.mapping.config.MappingConfigurations;

public abstract class AbstractManagerProducer<M extends AutoCloseable, F extends Function<String, M>, C extends Function<Settings, F>>
        implements Function<MappingConfigurations, M> {

    private static final Logger LOGGER = Logger.getLogger(AbstractManagerProducer.class.getName());

    private final Settings settings = new MicroProfileSettings();

    @Inject
    protected C configuration;

    @Override
    public M apply(MappingConfigurations databaseSetting) {
        F managerFactory = configuration.apply(settings);

        Optional<String> database = settings.get(databaseSetting, String.class);
        String db = database.orElseThrow(() -> new MappingException("Please, inform the database filling up the property "
                + databaseSetting));
        M manager = managerFactory.apply(db);

        LOGGER.log(
                Level.FINEST,
                String.format(
                        "Starting a %s instance using Eclipse MicroProfile Config, database name: %s",
                        manager.getClass().getName(),
                        db));
        return manager;
    }

}
