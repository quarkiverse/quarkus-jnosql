package io.quarkiverse.jnosql.document.it;

import java.util.List;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public class Person {

    @Id
    private long id;

    @Column
    private String name;

    @Column
    private List<String> phones;

}
