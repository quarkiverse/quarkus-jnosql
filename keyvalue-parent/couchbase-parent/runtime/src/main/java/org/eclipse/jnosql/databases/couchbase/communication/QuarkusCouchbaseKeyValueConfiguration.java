package org.eclipse.jnosql.databases.couchbase.communication;

import java.util.List;

import jakarta.data.exceptions.MappingException;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class QuarkusCouchbaseKeyValueConfiguration implements KeyValueConfiguration {

    @Override
    public CouchbaseBucketManagerFactory apply(Settings settings) throws NullPointerException {
        validateSettings(settings);
        return new CouchbaseKeyValueConfiguration().apply(settings);
    }

    protected void validateSettings(Settings settings) {
        List.of(
                CouchbaseConfigurations.HOST,
                CouchbaseConfigurations.USER,
                CouchbaseConfigurations.PASSWORD
        ).forEach(setting -> settings.get(setting, String.class).orElseThrow(
                () -> new MappingException("Please, inform the database filling up the property " + setting.get())));
    }
}
