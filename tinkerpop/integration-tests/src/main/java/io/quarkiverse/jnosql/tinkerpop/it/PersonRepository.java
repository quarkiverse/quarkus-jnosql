package io.quarkiverse.jnosql.tinkerpop.it;

import java.util.List;

import org.eclipse.jnosql.databases.tinkerpop.mapping.TinkerPopRepository;

public interface PersonRepository extends TinkerPopRepository<Person, String> {

    List<Person> findByName(String name);
}
