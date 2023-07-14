package io.quarkiverse.jnosql.keyvalue.hazelcast.it;

import java.util.Collections;
import java.util.Map;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class HazelcastServerTestResource implements QuarkusTestResourceLifecycleManager {

    private volatile HazelcastInstance member;

    @Override
    public Map<String, String> start() {
        Config config = new Config();
        System.out.println("STO CAZZO 666");
        config.getNetworkConfig().getJoin().getAutoDetectionConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getAzureConfig().setEnabled(false);
        member = Hazelcast.newHazelcastInstance(config);
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        if (member != null) {
            member.shutdown();
        }
    }
}
