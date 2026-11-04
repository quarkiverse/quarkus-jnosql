package io.quarkiverse.jnosql.keyvalue.infinispan.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.PageRequest;
import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.quarkiverse.jnosql.infinispan.it.Person;
import io.quarkiverse.jnosql.infinispan.it.PersonBasicRepository;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class PersonBasicRepositoryTest {

    private static final String PREFIX = "basic-repository-infinispan-";

    @Inject
    PersonBasicRepository repository;

    @AfterEach
    void cleanUp() {
        for (String id : List.of(PREFIX + "1", PREFIX + "2", PREFIX + "3")) {
            repository.deleteById(id);
        }
    }

    @Test
    void shouldSaveAndFindById() {
        Person saved = repository.save(person(PREFIX + "1", "Ada", "111", "222"));

        assertEquals(PREFIX + "1", saved.getId());
        Optional<Person> found = repository.findById(PREFIX + "1");
        assertTrue(found.isPresent());
        assertEquals(PREFIX + "1", found.get().getId());
        assertEquals("Ada", found.get().getName());
        assertEquals(List.of("111", "222"), found.get().getPhones());
    }

    @Test
    void shouldUpdateWhenSavingExistingId() {
        repository.save(person(PREFIX + "1", "Ada", "111"));
        repository.save(person(PREFIX + "1", "Ada Lovelace", "999"));

        Person found = repository.findById(PREFIX + "1").orElseThrow();
        assertEquals("Ada Lovelace", found.getName());
        assertEquals(List.of("999"), found.getPhones());
    }

    @Test
    void shouldSaveAll() {
        List<Person> saved = repository.saveAll(List.of(
                person(PREFIX + "1", "Ada", "111"),
                person(PREFIX + "2", "Grace", "222")));

        assertEquals(2, saved.size());
        assertEquals("Ada", repository.findById(PREFIX + "1").orElseThrow().getName());
        assertEquals("Grace", repository.findById(PREFIX + "2").orElseThrow().getName());
    }

    @Test
    void shouldReturnEmptyForUnknownId() {
        assertTrue(repository.findById(PREFIX + "missing").isEmpty());
    }

    @Test
    void shouldCheckExistenceThroughFindById() {
        repository.save(person(PREFIX + "1", "Ada", "111"));

        assertTrue(repository.findById(PREFIX + "1").isPresent());
        assertFalse(repository.findById(PREFIX + "missing").isPresent());
    }

    @Test
    void shouldDeleteById() {
        repository.save(person(PREFIX + "1", "Ada", "111"));

        repository.deleteById(PREFIX + "1");

        assertTrue(repository.findById(PREFIX + "1").isEmpty());
    }

    @Test
    void shouldDeleteEntity() {
        Person ada = repository.save(person(PREFIX + "1", "Ada", "111"));
        repository.save(person(PREFIX + "2", "Grace", "222"));

        repository.delete(ada);

        assertTrue(repository.findById(PREFIX + "1").isEmpty());
        assertTrue(repository.findById(PREFIX + "2").isPresent());
    }

    @Test
    void shouldDeleteAllGivenEntities() {
        List<Person> saved = repository.saveAll(List.of(
                person(PREFIX + "1", "Ada", "111"),
                person(PREFIX + "2", "Grace", "222"),
                person(PREFIX + "3", "Linus", "333")));

        repository.deleteAll(saved.subList(0, 2));

        assertTrue(repository.findById(PREFIX + "1").isEmpty());
        assertTrue(repository.findById(PREFIX + "2").isEmpty());
        assertEquals("Linus", repository.findById(PREFIX + "3").orElseThrow().getName());
    }

    @Test
    void shouldRejectFindAll() {
        assertThrows(UnsupportedOperationException.class, () -> repository.findAll());
    }

    @Test
    void shouldRejectPaginatedFindAll() {
        assertThrows(UnsupportedOperationException.class,
                () -> repository.findAll(PageRequest.ofSize(10), Order.by(Sort.asc("name"))));
    }

    private static Person person(String id, String name, String... phones) {
        Person person = new Person();
        person.setId(id);
        person.setName(name);
        person.setPhones(List.of(phones));
        return person;
    }
}
