package io.quarkiverse.jnosql.neo4j.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;

import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.eclipse.jnosql.mapping.graph.GraphTemplate;

@Path("/jnosql")
@ApplicationScoped
public class JNoSQLResource {

    @Inject
    @Database(DatabaseType.GRAPH)
    protected GraphTemplate template;

    @GET
    @Path("/using-jakarta-nosql")
    public Person fromTemplateWithPOJO() {
        Person person = Person.randomPerson();
        Person insert = template.insert(person);
        return template.find(Person.class, insert.getId()).orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("/using-jakarta-nosql-record")
    public PersonRecord fromTemplateWithRecord() {
        PersonRecord person = PersonRecord.randomPerson();
        PersonRecord insert = template.insert(person);
        return template.find(PersonRecord.class, insert.id()).orElseThrow(() -> new NotFoundException());
    }
}
