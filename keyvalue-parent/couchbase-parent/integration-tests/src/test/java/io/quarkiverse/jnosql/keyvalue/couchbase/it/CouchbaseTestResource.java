package io.quarkiverse.jnosql.keyvalue.couchbase.it;

import java.time.Duration;
import java.util.*;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.query.QueryIndexManager;
import jakarta.data.exceptions.MappingException;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.databases.couchbase.communication.*;
import org.eclipse.jnosql.mapping.config.MappingConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.TestcontainersConfiguration;

import io.quarkiverse.jnosql.core.runtime.MicroProfileSettings;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions;
import static com.couchbase.client.java.manager.query.GetAllQueryIndexesOptions.getAllQueryIndexesOptions;

public class CouchbaseTestResource extends CouchbaseConfiguration implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CouchbaseTestResource.class);

    private static final String CONTAINER_IMAGE = "couchbase/server";
    private static final String COUCHDB_BUCKET_NAME = "quarkus";

    private CouchbaseContainer container;

    private final Settings settings = new MicroProfileSettings();

    @Override
    public Map<String, String> start() {
        LOGGER.info(TestcontainersConfiguration.getInstance().toString());

        update(settings);

        var bucketName = settings
                .get(MappingConfigurations.KEY_VALUE_DATABASE.get(), String.class)
                .orElse(COUCHDB_BUCKET_NAME);

        BucketDefinition bucketDefinition = new BucketDefinition(bucketName);

        container = new CouchbaseContainer(CONTAINER_IMAGE)
                .withBucket(bucketDefinition);

        container.start();

        setHost(container.getConnectionString());
        setUser(container.getUsername());
        setPassword(container.getPassword());

        var couchbaseSettings = toCouchbaseSettings();

        LOGGER.info(couchbaseSettings.toString());

        setupCluster(couchbaseSettings, bucketName);

        Map<String, String> config = new HashMap<>(Map.of(
                MappingConfigurations.KEY_VALUE_DATABASE.get(), bucketName,
                CouchbaseConfigurations.HOST.get(), container.getConnectionString(),
                CouchbaseConfigurations.USER.get(), container.getUsername(),
                CouchbaseConfigurations.PASSWORD.get(), container.getPassword(),
                CouchbaseConfigurations.COLLECTIONS.get(), String.join(",", couchbaseSettings.getCollections())));

        Optional.ofNullable(couchbaseSettings.getIndex())
                .ifPresent(index -> config.put(CouchbaseConfigurations.INDEX.get(), index));

        return config;

    }

    @Override
    protected void update(Settings settings) {
        validateCouchbaseSettings(settings);
        super.update(settings);
    }

    private void validateCouchbaseSettings(Settings settings) {
        List.of(
                CouchbaseConfigurations.INDEX).forEach(
                setting -> settings.get(setting, String.class)
                        .orElseThrow(() -> new MappingException("Please, inform the database filling up the property "
                                + setting.get())));
    }

    @Override
    public void stop() {
        try {
            if (container != null) {
                container.stop();
            }
        } catch (Exception e) {
            // ignored
        }
    }

    public void setupCluster(CouchbaseSettings settings, String database) {
        long start = System.currentTimeMillis();
        LOGGER.info("starting the setup with database: " + database);

        try (Cluster cluster = settings.getCluster()) {

            BucketManager buckets = cluster.buckets();
            try {
                buckets.getBucket(database);
            } catch (BucketNotFoundException exp) {
                LOGGER.info("The database/bucket does not exist, creating it: " + database);
                buckets.createBucket(BucketSettings.create(database));
            }
            Bucket bucket = cluster.bucket(database);
            bucket.waitUntilReady(Duration.ofSeconds(5));

            final CollectionManager manager = bucket.collections();
            List<ScopeSpec> scopes = manager.getAllScopes();
            String finalScope = settings.getScope().orElseGet(() -> bucket.defaultScope().name());
            ScopeSpec spec = scopes.stream().filter(s -> finalScope.equals(s.name()))
                    .findFirst().orElseThrow();

            for (String collection : collections) {
                if (spec.collections().stream().noneMatch(c -> collection.equals(c.name()))) {
                    LOGGER.info("creating '" + collection + "' in '" + finalScope + "' scope");
                    await(() -> manager.createCollection(CollectionSpec.create(collection, finalScope)));
                }
            }
            if (index != null) {
                QueryIndexManager queryIndexManager = cluster.queryIndexes();
                List<QueryIndex> indexes = queryIndexManager.getAllIndexes(database, getAllQueryIndexesOptions()
                        .scopeName(finalScope).collectionName(index));
                if (indexes.isEmpty()) {
                    LOGGER.info("Index does not exist, creating primary key with scope "
                            + scope + " collection " + index + " at database " + database);
                    final var _queryIndexManager = queryIndexManager;
                    await(() -> _queryIndexManager.createPrimaryIndex(database, createPrimaryQueryIndexOptions()
                            .scopeName(finalScope).collectionName(index)));
                }

                for (String collection : collections) {
                    queryIndexManager = cluster.queryIndexes();
                    indexes = queryIndexManager.getAllIndexes(database, getAllQueryIndexesOptions()
                            .scopeName(finalScope).collectionName(collection));
                    if (indexes.isEmpty()) {
                        LOGGER.info("Index for " + collection + " collection does not exist, creating primary key with scope "
                                + scope + " collection " + collection + " at database " + database);
                        final var _queryIndexManager = queryIndexManager;
                        await(() -> _queryIndexManager
                                .createPrimaryIndex(database, createPrimaryQueryIndexOptions()
                                        .scopeName(finalScope).collectionName(collection)));
                    }
                }

            }

            long end = System.currentTimeMillis() - start;
            LOGGER.info("Finished the setup with database: " + database + " end with millis "
                    + end);
        }
    }

    private void await(Runnable runnable) {
        Awaitility.
                given()
                .ignoreExceptions()
                .await().until(() -> {
                    try {
                        runnable.run();
                    } catch (RuntimeException ex) {
                        LOGGER.warn("occurred an issue, but it'll be retried: " + ex.getMessage(), ex);
                        throw ex;
                    }
                    return true;
                });
    }

}
