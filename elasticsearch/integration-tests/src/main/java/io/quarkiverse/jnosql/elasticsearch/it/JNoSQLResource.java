package io.quarkiverse.jnosql.elasticsearch.it;

import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.nosql.Template;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;

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

    @GET
    @Path("/using-jakarta-data")
    public Person fromRepositoryWithPOJO() throws InterruptedException {
        Person person = Person.randomPerson();
        Person insert = people.insert(person);
        TimeUnit.SECONDS.sleep(1);
        return people.findById(insert.getId()).orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("/using-jakarta-nosql")
    public Person fromTemplateWithPOJO() throws InterruptedException {
        Person person = Person.randomPerson();
        Person insert = template.insert(person);
        TimeUnit.SECONDS.sleep(1);
        return template.find(Person.class, insert.getId()).orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("/using-jakarta-data-record")
    public PersonRecord fromRepositoryWithRecord() throws InterruptedException {
        PersonRecord person = PersonRecord.randomPerson();
        PersonRecord insert = peopleRecord.save(person);
        TimeUnit.SECONDS.sleep(1);
        return peopleRecord.findById(insert.id())
                .orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("/using-jakarta-nosql-record")
    public PersonRecord fromTemplateWithRecord() throws InterruptedException {
        PersonRecord person = PersonRecord.randomPerson();
        PersonRecord insert = template.insert(person);
        TimeUnit.SECONDS.sleep(1);
        return template.find(PersonRecord.class, insert.id()).orElseThrow(() -> new NotFoundException());
    }
}
