package io.quarkiverse.jnosql.document.runtime;

import java.util.function.Supplier;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;

import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;
import org.eclipse.jnosql.communication.semistructured.DatabaseManager;
import org.eclipse.jnosql.communication.semistructured.DatabaseManagerFactory;
import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;

import io.quarkiverse.jnosql.core.runtime.AbstractManagerProducer;

public class DocumentManagerProducer
        extends AbstractManagerProducer<DatabaseManager, DatabaseManagerFactory, DatabaseConfiguration>
        implements Supplier<DatabaseManager> {

    @Override
    @Produces
    @Alternative
    @Priority(1)
    @ApplicationScoped
    public DatabaseManager get() {
        return apply(MappingConfigurations.DOCUMENT_DATABASE);
    }

}
