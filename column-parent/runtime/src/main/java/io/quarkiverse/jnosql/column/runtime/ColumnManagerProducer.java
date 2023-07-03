package io.quarkiverse.jnosql.column.runtime;

import static org.eclipse.jnosql.mapping.config.MappingConfigurations.COLUMN_DATABASE;
import static org.eclipse.jnosql.mapping.config.MappingConfigurations.KEY_VALUE_DATABASE;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.Priority;
import jakarta.data.exceptions.MappingException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.column.ColumnConfiguration;
import org.eclipse.jnosql.communication.column.ColumnManager;
import org.eclipse.jnosql.communication.column.ColumnManagerFactory;

import io.quarkiverse.jnosql.core.runtime.MicroProfileSettings;

public class ColumnManagerProducer implements Supplier<ColumnManager> {

    private static final Logger LOGGER = Logger.getLogger(ColumnManagerProducer.class.getName());

    private final Settings settings = new MicroProfileSettings();

    @Inject
    private ColumnConfiguration columnConfiguration;

    @Override
    @Produces
    @Alternative
    @Priority(1)
    @ApplicationScoped
    public ColumnManager get() {
        ColumnManagerFactory managerFactory = columnConfiguration.apply(settings);

        Optional<String> database = settings.get(COLUMN_DATABASE, String.class);
        String db = database.orElseThrow(() -> new MappingException("Please, inform the database filling up the property "
                + KEY_VALUE_DATABASE));
        ColumnManager manager = managerFactory.apply(db);

        LOGGER.log(Level.FINEST, "Starting  a BucketManager instance using Eclipse MicroProfile Config, database name: {0}",
                db);
        return manager;
    }

}
