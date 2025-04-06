package io.quarkiverse.jnosql.elasticsearch.it;

import jakarta.data.repository.Repository;

import org.eclipse.jnosql.mapping.NoSQLRepository;

@Repository
public interface People extends NoSQLRepository<Person, String> {
}
