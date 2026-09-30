package io.quarkiverse.jnosql.infinispan.runtime.graal;

import java.util.function.BooleanSupplier;

import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.metrics.impl.MetricsRegistry;
import org.infinispan.metrics.impl.NoMetricRegistry;
import org.infinispan.util.logging.Log;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

final class InfinispanSubstitutions {

    private InfinispanSubstitutions() {
    }

    private static boolean isPresent(String name) {
        try {
            Class.forName(name, false, InfinispanSubstitutions.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    static final class WithoutRaft implements BooleanSupplier {
        @Override
        public boolean getAsBoolean() {
            return !isPresent("org.jgroups.protocols.raft.RAFT");
        }
    }

    static final class WithoutPrometheus implements BooleanSupplier {
        @Override
        public boolean getAsBoolean() {
            return !isPresent("io.micrometer.prometheusmetrics.PrometheusMeterRegistry")
                    && !isPresent("io.micrometer.prometheus.PrometheusMeterRegistry");
        }
    }
}

@TargetClass(className = "org.infinispan.remoting.transport.jgroups.JGroupsTransport", onlyWith = InfinispanSubstitutions.WithoutRaft.class)
final class Target_JGroupsTransport {
    @Substitute
    private void initRaftManager() {
        // The original method does nothing when the Raft library is absent.
    }
}

@TargetClass(NoMetricRegistry.class)
final class Target_NoMetricRegistry {
    @Alias
    static MetricsRegistry NO_OP_INSTANCE;
}

@TargetClass(className = "org.infinispan.metrics.impl.MetricsComponentFactory", onlyWith = InfinispanSubstitutions.WithoutPrometheus.class)
final class Target_MetricsComponentFactory {
    @Alias
    private static Log log;

    @Alias
    GlobalConfiguration globalConfig;

    @Alias
    private MetricsRegistry registry;

    @Substitute
    private synchronized MetricsRegistry createMetricRegistry(ClassLoader classLoader) {
        if (registry == null) {
            if (globalConfig.metrics().enabled()) {
                log.warnFallbackToNoOpMetrics(NoMetricRegistry.class.getSimpleName());
            }
            registry = Target_NoMetricRegistry.NO_OP_INSTANCE;
        }
        return registry;
    }
}
