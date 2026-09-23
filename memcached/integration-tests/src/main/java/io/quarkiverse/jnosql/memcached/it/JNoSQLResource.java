package io.quarkiverse.jnosql.memcached.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.nosql.Template;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.eclipse.jnosql.communication.keyvalue.BucketManager;

@Path("/jnosql")
@ApplicationScoped
public class JNoSQLResource {

    @Inject
    Template template;

    @Inject
    BucketManager bucketManager;

    @PUT
    @Path("/template")
    public Person insertWithTemplate(Person person) {
        return template.insert(person);
    }

    @GET
    @Path("/template/{id}")
    public Person findWithTemplate(@PathParam("id") String id) {
        return template.find(Person.class, id).orElseThrow(NotFoundException::new);
    }

    @DELETE
    @Path("/template/{id}")
    public void deleteWithTemplate(@PathParam("id") String id) {
        template.delete(Person.class, id);
    }

    @PUT
    @Path("/keyvalue")
    public Person insertWithBucketManager(Person person) {
        bucketManager.put(person.getId(), person);
        return person;
    }

    @GET
    @Path("/keyvalue/{id}")
    public Person findWithBucketManager(@PathParam("id") String id) {
        return bucketManager.get(id).map(value -> value.get(Person.class)).orElseThrow(NotFoundException::new);
    }

    @DELETE
    @Path("/keyvalue/{id}")
    public void deleteWithBucketManager(@PathParam("id") String id) {
        bucketManager.delete(id);
    }
}
