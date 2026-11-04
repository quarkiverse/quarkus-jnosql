package io.quarkiverse.jnosql.tinkerpop.it;

import java.util.List;

import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Repository;

@Repository
public interface PersonDataRepository extends BasicRepository<Person, String> {

    List<Person> findByName(String name);
}
