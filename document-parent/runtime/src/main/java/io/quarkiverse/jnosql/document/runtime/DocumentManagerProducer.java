package io.quarkiverse.jnosql.document.runtime;

import java.util.function.Supplier;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;

import org.eclipse.jnosql.communication.document.DocumentConfiguration;
import org.eclipse.jnosql.communication.document.DocumentManager;
import org.eclipse.jnosql.communication.document.DocumentManagerFactory;
import org.eclipse.jnosql.mapping.config.MappingConfigurations;

import io.quarkiverse.jnosql.core.runtime.AbstractManagerProducer;

public class DocumentManagerProducer
        extends AbstractManagerProducer<DocumentManager, DocumentManagerFactory, DocumentConfiguration>
        implements Supplier<DocumentManager> {

    @Override
    @Produces
    @Alternative
    @Priority(1)
    @ApplicationScoped
    public DocumentManager get() {
        return apply(MappingConfigurations.DOCUMENT_DATABASE);
    }

}
