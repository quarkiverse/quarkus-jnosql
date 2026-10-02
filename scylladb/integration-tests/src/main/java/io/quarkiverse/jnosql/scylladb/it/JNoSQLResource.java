package io.quarkiverse.jnosql.scylladb.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.nosql.Template;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;

import org.eclipse.jnosql.communication.semistructured.CommunicationEntity;
import org.eclipse.jnosql.communication.semistructured.DeleteQuery;
import org.eclipse.jnosql.communication.semistructured.SelectQuery;
import org.eclipse.jnosql.databases.scylladb.communication.ScyllaDBColumnManager;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;

@Path("/jnosql")
@ApplicationScoped
public class JNoSQLResource {

    @Inject
    @Database(DatabaseType.COLUMN)
    protected People people;

    @Inject
    @Database(DatabaseType.COLUMN)
    protected Template template;

    @Inject
    @Database(DatabaseType.COLUMN)
    protected PeopleRecord peopleRecord;

    @Inject
    @Database(DatabaseType.COLUMN)
    ScyllaDBColumnManager manager;

    @GET
    @Path("/column-crud")
    public String columnCrud() {
        Person person = Person.randomPerson();
        CommunicationEntity entity = CommunicationEntity.of("person");
        entity.add("_id", person.getId());
        entity.add("name", person.getName());
        entity.add("phones", person.getPhones());
        manager.insert(entity);
        var query = SelectQuery.select().from("person").where("_id").eq(person.getId()).build();
        manager.singleResult(query).orElseThrow(NotFoundException::new);
        entity.add("name", "updated");
        manager.update(entity);
        String name = manager.singleResult(query).orElseThrow(NotFoundException::new)
                .find("name", String.class).orElseThrow();
        manager.delete(DeleteQuery.delete().from("person").where("_id").eq(person.getId()).build());
        return name + ":" + manager.singleResult(query).isEmpty();
    }

    @GET
    @Path("/template-crud")
    public String templateCrud() {
        Person person = template.insert(Person.randomPerson());
        person.setName("updated");
        template.update(person);
        String name = template.find(Person.class, person.getId()).orElseThrow(NotFoundException::new).getName();
        template.delete(Person.class, person.getId());
        return name + ":" + template.find(Person.class, person.getId()).isEmpty();
    }

    @GET
    @Path("/repository-crud")
    public String repositoryCrud() {
        Person person = people.insert(Person.randomPerson());
        person.setName("updated");
        people.update(person);
        String name = people.findById(person.getId()).orElseThrow(NotFoundException::new).getName();
        people.deleteById(person.getId());
        return name + ":" + people.findById(person.getId()).isEmpty();
    }

    @GET
    @Path("/using-jakarta-data")
    public Person fromRepositoryWithPOJO() {
        Person person = Person.randomPerson();
        Person insert = people.insert(person);
        return people.findById(insert.getId()).orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("/using-jakarta-nosql")
    public Person fromTemplateWithPOJO() {
        Person person = Person.randomPerson();
        Person insert = template.insert(person);
        return template.find(Person.class, insert.getId()).orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("/using-jakarta-data-record")
    public PersonRecord fromRepositoryWithRecord() {
        PersonRecord person = PersonRecord.randomPerson();
        PersonRecord insert = peopleRecord.save(person);
        return peopleRecord.findById(insert.id())
                .orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("/using-jakarta-nosql-record")
    public PersonRecord fromTemplateWithRecord() {
        PersonRecord person = PersonRecord.randomPerson();
        PersonRecord insert = template.insert(person);
        return template.find(PersonRecord.class, insert.id()).orElseThrow(() -> new NotFoundException());
    }
}
