package io.quarkiverse.jnosql.arangodb.it;

import java.util.List;
import java.util.UUID;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public record PersonRecord(@Id String id,
        @Column String name,
        @Column List<String> phones) {

    public static PersonRecord randomPerson() {
        var person = new PersonRecord(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                List.of(
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString()));
        return person;
    }
}
