package io.quarkiverse.jnosql.vector.runtime;

import static org.eclipse.jnosql.mapping.core.config.MappingConfigurations.VECTOR_DATABASE;
import static org.eclipse.jnosql.mapping.core.config.MappingConfigurations.VECTOR_PROVIDER;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.nosql.MappingException;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;
import org.eclipse.jnosql.communication.semistructured.DatabaseManagerFactory;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.eclipse.jnosql.mapping.vector.VectorManager;

import io.quarkiverse.jnosql.core.runtime.MicroProfileSettings;

/**
 * Produces the {@link VectorManager} of the application from the JNoSQL vector configuration
 * ({@code jnosql.vector.database} and, optionally, {@code jnosql.vector.provider}).
 * <p>
 * It replaces the JNoSQL {@code VectorManagerSupplier} to keep a reference to the
 * {@link DatabaseManagerFactory}, which owns the driver connection, and close it on shutdown.
 */
@Singleton
public class QuarkusVectorManagerProducer {

    private static final Logger LOGGER = Logger.getLogger(QuarkusVectorManagerProducer.class.getName());

    private final Settings settings = new MicroProfileSettings();

    private DatabaseManagerFactory factory;

    @Produces
    @Priority(1)
    @Alternative
    @Default
    @ApplicationScoped
    @Database(DatabaseType.VECTOR)
    public synchronized VectorManager get() {
        var database = settings.get(VECTOR_DATABASE, String.class)
                .filter(name -> !name.isBlank())
                .orElseThrow(() -> new MappingException("Please, configure a non-blank database name using "
                        + VECTOR_DATABASE.get()));

        factory = configuration().apply(settings);
        var manager = factory.apply(database);
        if (!(manager instanceof VectorManager vectorManager)) {
            closeQuietly(manager);
            closeFactory();
            throw new MappingException("The vector database provider must return a VectorManager");
        }
        LOGGER.log(Level.FINEST, "Starting a VectorManager instance using Eclipse MicroProfile Config, database name: "
                + database);
        return vectorManager;
    }

    synchronized void close(@Disposes @Default @Database(DatabaseType.VECTOR) VectorManager manager) {
        LOGGER.log(Level.FINEST, "Closing the VectorManager and its factory");
        try {
            manager.close();
        } finally {
            closeFactory();
        }
    }

    private void closeFactory() {
        if (factory != null) {
            factory.close();
            factory = null;
        }
    }

    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Error closing the vector database manager", e);
            }
        }
    }

    private DatabaseConfiguration configuration() {
        Class<?> type = settings.get(VECTOR_PROVIDER, Class.class).orElse(null);
        if (type == null) {
            return DatabaseConfiguration.getConfiguration();
        }
        if (!DatabaseConfiguration.class.isAssignableFrom(type)) {
            throw new MappingException("The provider configured by " + VECTOR_PROVIDER.get()
                    + " must implement " + DatabaseConfiguration.class.getName() + ": " + type.getName());
        }
        try {
            return (DatabaseConfiguration) type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new MappingException("Unable to instantiate the vector database provider configured by "
                    + VECTOR_PROVIDER.get() + ": " + type.getName(), e);
        }
    }
}
