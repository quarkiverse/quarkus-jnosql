package io.quarkiverse.jnosql.dynamodb.it;

import java.util.List;
import java.util.UUID;

public record PersonRecord(String id,
        String name,
        List<String> phones) {

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
