package io.quarkiverse.jnosql.document.runtime;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.mapping.reflection.EntityMetadataExtension;

public class EntityMetadataExtensionProducer {

    @Produces
    @Singleton
    public EntityMetadataExtension jobScheduler() {
        System.out.println("ALLORA2 ????");
        return new EntityMetadataExtension();
    }

}
