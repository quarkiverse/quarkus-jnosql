package io.quarkiverse.jnosql.hazelcast.it;

import java.util.List;
import java.util.UUID;

public class Person {

    private String id;

    private String name;

    private List<String> phones;

    public static Person randomPerson() {
        var person = new Person();
        person.setId(UUID.randomUUID().toString());
        person.setName(UUID.randomUUID().toString());
        person.setPhones(List.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()));
        return person;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }
}
