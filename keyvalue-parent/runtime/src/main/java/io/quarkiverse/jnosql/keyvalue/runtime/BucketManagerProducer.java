package io.quarkiverse.jnosql.keyvalue.runtime;

import java.util.function.Supplier;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;

import org.eclipse.jnosql.communication.keyvalue.BucketManager;
import org.eclipse.jnosql.communication.keyvalue.BucketManagerFactory;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;
import org.eclipse.jnosql.mapping.config.MappingConfigurations;

import io.quarkiverse.jnosql.core.runtime.AbstractManagerProducer;

public class BucketManagerProducer extends AbstractManagerProducer<BucketManager, BucketManagerFactory, KeyValueConfiguration>
        implements Supplier<BucketManager> {

    @Override
    @Produces
    @Alternative
    @Priority(1)
    @ApplicationScoped
    public BucketManager get() {
        return apply(MappingConfigurations.KEY_VALUE_DATABASE);
    }

}
