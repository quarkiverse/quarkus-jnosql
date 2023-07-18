package io.quarkiverse.jnosql.column.runtime;

import java.util.function.Supplier;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;

import org.eclipse.jnosql.communication.column.ColumnConfiguration;
import org.eclipse.jnosql.communication.column.ColumnManager;
import org.eclipse.jnosql.communication.column.ColumnManagerFactory;
import org.eclipse.jnosql.mapping.config.MappingConfigurations;

import io.quarkiverse.jnosql.core.runtime.AbstractManagerProducer;

public class ColumnManagerProducer extends AbstractManagerProducer<ColumnManager, ColumnManagerFactory, ColumnConfiguration>
        implements Supplier<ColumnManager> {

    @Override
    @Produces
    @Alternative
    @Priority(1)
    @ApplicationScoped
    public ColumnManager get() {
        return apply(MappingConfigurations.COLUMN_DATABASE);
    }

}
