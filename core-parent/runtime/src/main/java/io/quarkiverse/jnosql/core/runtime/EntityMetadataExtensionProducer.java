package io.quarkiverse.jnosql.core.runtime;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.mapping.reflection.EntityMetadataExtension;

public class EntityMetadataExtensionProducer {

    @Produces
    @Singleton
    public EntityMetadataExtension jobScheduler() {
        return new EntityMetadataExtension();
    }

}
