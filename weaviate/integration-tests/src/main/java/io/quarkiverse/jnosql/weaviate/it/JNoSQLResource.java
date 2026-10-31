package io.quarkiverse.jnosql.weaviate.it;

import java.util.List;
import java.util.Map;

import jakarta.data.Limit;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.eclipse.jnosql.mapping.vector.DenseVector;
import org.eclipse.jnosql.mapping.vector.VectorTemplate;

@Path("/jnosql")
@Produces(MediaType.APPLICATION_JSON)
public class JNoSQLResource {

    @Inject
    @Database(DatabaseType.VECTOR)
    VectorTemplate template;

    @POST
    @Path("/seed")
    public String seed() {
        template.insert(new Article("1", "Quarkus", "java", DenseVector.of(1f, 0f, 0f, 0f)));
        template.insert(new Article("2", "Weaviate", "database", DenseVector.of(0f, 1f, 0f, 0f)));
        template.insert(new Article("3", "Quarkus Weaviate", "java", DenseVector.of(0.9f, 0.1f, 0f, 0f)));
        return "ok";
    }

    @GET
    @Path("/articles/{id}")
    public Map<String, Object> find(@PathParam("id") String id) {
        return template.find(Article.class, id)
                .map(a -> Map.<String, Object> of("id", a.getId(), "title", a.getTitle(), "category", a.getCategory()))
                .orElse(Map.of());
    }

    @GET
    @Path("/search")
    public List<String> search() {
        return template.search(Article.class)
                .vector(DenseVector.of(1f, 0f, 0f, 0f))
                .limit(Limit.of(2))
                .result().stream().map(Article::getId).toList();
    }
}
