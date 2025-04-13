package io.quarkiverse.jnosql.elasticsearch.deployment;

import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets = RangeQuery.Builder.class)
public class ReflectionConfig {
    // This class is used to register the RangeQuery$Builder class for reflection
}
