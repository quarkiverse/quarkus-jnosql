package io.quarkiverse.jnosql.oracle.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.nosql.Template;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;

import org.eclipse.jnosql.communication.keyvalue.BucketManager;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;

@Path("/jnosql")
@ApplicationScoped
public class JNoSQLResource {

    @Inject
    @Database(DatabaseType.DOCUMENT)
    protected People people;

    @Inject
    @Database(DatabaseType.DOCUMENT)
    protected Template template;

    @Inject
    @Database(DatabaseType.DOCUMENT)
    protected PeopleRecord peopleRecord;

    @Inject
    protected BucketManager bucketManager;

    @GET
    @Path("/document/using-jakarta-data")
    public Person fromRepositoryWithPOJO() {
        Person person = Person.randomPerson();
        Person insert = people.insert(person);
        return people.findById(insert.getId()).orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("/document/using-jakarta-nosql")
    public Person fromTemplateWithPOJO() {
        Person person = Person.randomPerson();
        Person insert = template.insert(person);
        return template.find(Person.class, insert.getId()).orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("/document/using-jakarta-data-record")
    public PersonRecord fromRepositoryWithRecord() {
        PersonRecord person = PersonRecord.randomPerson();
        PersonRecord insert = peopleRecord.save(person);
        return peopleRecord.findById(insert.id())
                .orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("/document/using-jakarta-nosql-record")
    public PersonRecord fromTemplateWithRecord() {
        PersonRecord person = PersonRecord.randomPerson();
        PersonRecord insert = template.insert(person);
        return template.find(PersonRecord.class, insert.id()).orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("/keyvalue/using-pojo")
    public Person fromBucketWithPojo() {
        Person person = Person.randomPerson();
        bucketManager.put(person.getId(), person);
        return bucketManager
                .get(person.getId())
                .map(v -> v.get(Person.class))
                .orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("/keyvalue/using-record")
    public PersonRecord fromBucketWithRecord() {
        PersonRecord person = PersonRecord.randomPerson();
        bucketManager.put(person.id(), person);
        return bucketManager
                .get(person.id())
                .map(v -> v.get(PersonRecord.class))
                .orElseThrow(() -> new NotFoundException());
    }
}
