package org.eclipse.jnosql.databases.hazelcast.communication;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;

import com.hazelcast.core.HazelcastInstance;

@Singleton
public class QuarkusHazelcastKeyValueConfiguration implements KeyValueConfiguration {

    @Inject
    protected HazelcastInstance client;

    @Override
    public HazelcastBucketManagerFactory apply(Settings settings) throws NullPointerException {
        return new DefaultHazelcastBucketManagerFactory(
                client);
    }

}
