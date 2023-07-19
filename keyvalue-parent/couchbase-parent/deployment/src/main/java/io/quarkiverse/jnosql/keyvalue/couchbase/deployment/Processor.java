package io.quarkiverse.jnosql.keyvalue.couchbase.deployment;

import io.quarkiverse.jnosql.core.runtime.JvmOnlyRecorder;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.pkg.steps.NativeBuild;
import org.eclipse.jnosql.databases.couchbase.communication.QuarkusCouchbaseKeyValueConfiguration;
import org.jboss.logging.Logger;

class Processor {

    private static final Logger LOG = Logger.getLogger(Processor.class);

    private static final String FEATURE = "jnosql-keyvalue-couchbase";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    /**
     * Remove this once this extension starts supporting the native mode.
     */
    @BuildStep(onlyIf = NativeBuild.class)
    @Record(value = ExecutionTime.RUNTIME_INIT)
    void warnJvmInNative(JvmOnlyRecorder recorder) {
        JvmOnlyRecorder.warnJvmInNative(LOG, FEATURE); // warn at build time
        recorder.warnJvmInNative(FEATURE); // warn at runtime
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusCouchbaseKeyValueConfiguration.class));
    }

}
