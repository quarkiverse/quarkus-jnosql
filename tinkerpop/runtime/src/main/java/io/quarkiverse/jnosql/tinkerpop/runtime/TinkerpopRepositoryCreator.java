package io.quarkiverse.jnosql.tinkerpop.runtime;

import org.eclipse.jnosql.databases.tinkerpop.mapping.query.TinkerpopRepositoryBean;

import io.quarkus.arc.BeanCreator;
import io.quarkus.arc.SyntheticCreationalContext;

/**
 * Creates the {@code TinkerPopRepository} proxy through the driver's own {@link TinkerpopRepositoryBean}.
 * The driver registers these beans from a portable CDI extension, which Quarkus Arc does not run.
 */
public class TinkerpopRepositoryCreator implements BeanCreator<Object> {

    public static final String REPOSITORY_TYPE = "repositoryType";

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object create(SyntheticCreationalContext<Object> context) {
        Class<?> repositoryType = (Class<?>) context.getParams().get(REPOSITORY_TYPE);
        return new TinkerpopRepositoryBean(repositoryType).create(null);
    }
}
