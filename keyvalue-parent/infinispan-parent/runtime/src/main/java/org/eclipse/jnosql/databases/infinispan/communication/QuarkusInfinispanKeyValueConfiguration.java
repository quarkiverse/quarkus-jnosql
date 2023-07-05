package org.eclipse.jnosql.databases.infinispan.communication;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;
import org.infinispan.client.hotrod.RemoteCacheManager;

@Singleton
public class QuarkusInfinispanKeyValueConfiguration implements KeyValueConfiguration {

    @Inject
    protected RemoteCacheManager client;

    @Override
    public InfinispanBucketManagerFactory apply(Settings settings) throws NullPointerException {
        return new InfinispanBucketManagerFactory(
                client);
    }

}
