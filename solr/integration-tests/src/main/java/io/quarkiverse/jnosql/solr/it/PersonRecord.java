package io.quarkiverse.jnosql.solr.it;

import java.util.UUID;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public record PersonRecord(@Id String id, @Column String name) {

    public static PersonRecord randomPerson() {
        var person = new PersonRecord(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        return person;
    }
}
