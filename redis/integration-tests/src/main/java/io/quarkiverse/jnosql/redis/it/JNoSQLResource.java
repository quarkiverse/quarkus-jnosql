package io.quarkiverse.jnosql.redis.it;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.nosql.Template;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;

import org.eclipse.jnosql.communication.keyvalue.BucketManager;

@Path("/jnosql")
@ApplicationScoped
public class JNoSQLResource {

    @Inject
    private Template template;

    @Inject
    protected BucketManager bucketManager;

    @GET
    @Path("/template/using-pojo")
    public Person fromTemplateWithPojo() {
        Person person = template.insert(Person.randomPerson());
        return template
                .find(Person.class, person.getId())
                .orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("/template/using-record")
    public PersonRecord fromTemplateWithRecord() {
        PersonRecord person = template.insert(PersonRecord.randomPerson());
        return template
                .find(PersonRecord.class, person.id())
                .orElseThrow(() -> new NotFoundException());
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

    @GET
    @Path("/keyvalue/concurrent")
    public String fromBucketConcurrently() {
        int operations = 64;
        ExecutorService executor = Executors.newFixedThreadPool(16);
        try {
            List<Future<Boolean>> futures = IntStream.range(0, operations)
                    .mapToObj(i -> executor.submit(() -> {
                        Person person = Person.randomPerson();
                        bucketManager.put(person.getId(), person);
                        return bucketManager.get(person.getId()).isPresent();
                    }))
                    .toList();
            long succeeded = 0;
            for (Future<Boolean> future : futures) {
                if (future.get()) {
                    succeeded++;
                }
            }
            return String.valueOf(succeeded);
        } catch (ExecutionException e) {
            throw new InternalServerErrorException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InternalServerErrorException(e);
        } finally {
            executor.shutdown();
        }
    }
}
