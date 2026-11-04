package io.quarkiverse.jnosql.infinispan.it;

import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Repository;

@Repository
public interface PersonBasicRepository extends BasicRepository<Person, String> {
}
