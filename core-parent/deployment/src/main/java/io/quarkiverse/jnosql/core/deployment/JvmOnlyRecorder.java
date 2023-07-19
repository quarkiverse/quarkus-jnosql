package io.quarkiverse.jnosql.core.deployment;

import io.quarkus.runtime.annotations.Recorder;
import org.jboss.logging.Logger;

@Recorder
public class JvmOnlyRecorder {
    private static final Logger LOG = Logger.getLogger(JvmOnlyRecorder.class);

    public JvmOnlyRecorder() {
    }

    public static void warnJvmInNative(Logger log, String feature) {
        log.warnf("The %s extension was not tested in native mode. You may want to report about the success or failure running it in native mode on https://github.com/apache/camel-quarkus/issues?q=is%%3Aissue+%s", feature, feature.replace('-', '+'));
    }

    public void warnJvmInNative(String feature) {
        warnJvmInNative(LOG, feature);
    }
}
