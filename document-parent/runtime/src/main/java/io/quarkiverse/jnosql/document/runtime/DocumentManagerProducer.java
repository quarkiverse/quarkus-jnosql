package io.quarkiverse.jnosql.document.runtime;

import static org.eclipse.jnosql.mapping.config.MappingConfigurations.DOCUMENT_DATABASE;

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
import org.eclipse.jnosql.communication.document.DocumentConfiguration;
import org.eclipse.jnosql.communication.document.DocumentManager;
import org.eclipse.jnosql.communication.document.DocumentManagerFactory;

import io.quarkiverse.jnosql.core.runtime.MicroProfileSettings;

public class DocumentManagerProducer implements Supplier<DocumentManager> {

    private static final Logger LOGGER = Logger.getLogger(DocumentManagerProducer.class.getName());

    private final Settings settings = new MicroProfileSettings();

    @Inject
    private DocumentConfiguration documentConfiguration;

    @Override
    @Produces
    @Alternative
    @Priority(1)
    @ApplicationScoped
    public DocumentManager get() {
        DocumentManagerFactory managerFactory = documentConfiguration.apply(settings);

        Optional<String> database = settings.get(DOCUMENT_DATABASE, String.class);
        String db = database.orElseThrow(() -> new MappingException("Please, inform the database filling up the property "
                + DOCUMENT_DATABASE));
        DocumentManager manager = managerFactory.apply(db);

        LOGGER.log(Level.FINEST, "Starting  a DocumentManager instance using Eclipse MicroProfile Config, database name: {0}",
                db);
        return manager;
    }

}
