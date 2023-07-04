package org.eclipse.jnosql.databases.infinispan.communication;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.ClassAllowList;

@Singleton
public class QuarkusInfinispanKeyValueConfiguration implements KeyValueConfiguration {

    @Inject
    protected RemoteCacheManager client;

    @Override
    public InfinispanBucketManagerFactory apply(Settings settings) throws NullPointerException {
        ClassAllowList classAllowList = client.getConfiguration().getClassAllowList();
        classAllowList.addClasses("io.quarkiverse.jnosql.keyvalue.infinispan.it.Person", "java.util.Arrays$ArrayList");
        client.getMarshaller().initialize(classAllowList);
        return new InfinispanBucketManagerFactory(
                client);
    }

}
