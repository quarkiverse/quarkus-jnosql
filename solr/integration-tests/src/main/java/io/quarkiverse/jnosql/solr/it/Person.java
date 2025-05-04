package io.quarkiverse.jnosql.solr.it;

import java.util.UUID;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public class Person {

    public static Person randomPerson() {
        var person = new Person();
        person.setId(UUID.randomUUID().toString());
        person.setName(UUID.randomUUID().toString());
        return person;
    }

    @Id
    private String id;

    @Column
    private String name;

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

}
