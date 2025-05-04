package io.quarkiverse.jnosql.hazelcast.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;

import org.eclipse.jnosql.communication.keyvalue.BucketManager;

@Path("/jnosql")
@ApplicationScoped
public class JNoSQLResource {

    @Inject
    protected BucketManager bucketManager;

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
