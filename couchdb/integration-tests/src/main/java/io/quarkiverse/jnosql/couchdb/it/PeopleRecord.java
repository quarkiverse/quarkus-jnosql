package io.quarkiverse.jnosql.couchdb.it;

import jakarta.data.repository.Repository;

import org.eclipse.jnosql.mapping.NoSQLRepository;

@Repository
public interface PeopleRecord extends NoSQLRepository<PersonRecord, String> {
}
