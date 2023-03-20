package io.quarkiverse.jnosql.keyvalue.runtime;

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
import org.eclipse.jnosql.communication.keyvalue.BucketManager;
import org.eclipse.jnosql.communication.keyvalue.BucketManagerFactory;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;

import io.quarkiverse.jnosql.core.runtime.MicroProfileSettings;

public class BucketManagerProducer implements Supplier<BucketManager> {

    private static final Logger LOGGER = Logger.getLogger(BucketManagerProducer.class.getName());

    private final Settings settings = new MicroProfileSettings();

    @Inject
    private KeyValueConfiguration documentConfiguration;

    @Override
    @Produces
    @Alternative
    @Priority(1)
    @ApplicationScoped
    public BucketManager get() {
        BucketManagerFactory managerFactory = documentConfiguration.apply(settings);

        Optional<String> database = settings.get(KEY_VALUE_DATABASE, String.class);
        String db = database.orElseThrow(() -> new MappingException("Please, inform the database filling up the property "
                + KEY_VALUE_DATABASE));
        BucketManager manager = managerFactory.apply(db);

        LOGGER.log(Level.FINEST, "Starting  a BucketManager instance using Eclipse MicroProfile Config, database name: {0}",
                db);
        return manager;
    }

}
