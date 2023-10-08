package io.quarkiverse.jnosql.core.runtime;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.mapping.spi.EntityMetadataExtension;

@Singleton
public class QuarkusEntityMetadataExtension extends EntityMetadataExtension {

    public void onStart(@Observes Startup ev) {
        this.afterBeanDiscovery(null);
    }

}
