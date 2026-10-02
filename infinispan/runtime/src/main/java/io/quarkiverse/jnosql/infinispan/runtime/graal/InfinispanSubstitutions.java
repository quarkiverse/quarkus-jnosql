package io.quarkiverse.jnosql.infinispan.runtime.graal;

import java.util.Optional;
import java.util.function.BooleanSupplier;

import org.infinispan.metrics.impl.MetricsRegistry;

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

@TargetClass(className = "org.infinispan.metrics.impl.MetricsComponentFactory", onlyWith = InfinispanSubstitutions.WithoutPrometheus.class)
final class Target_MetricsComponentFactory {
    @Substitute
    private static Optional<MetricsRegistry> tryLoadPrometheusRegistry(ClassLoader classLoader) {
        return Optional.empty();
    }

    @Substitute
    private static Optional<MetricsRegistry> tryLoadDeprecatedPrometheusRegistry(ClassLoader classLoader) {
        return Optional.empty();
    }
}
