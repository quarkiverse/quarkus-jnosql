package io.quarkiverse.jnosql.core.deployment;

import org.jboss.logging.Logger;

import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class JvmOnlyRecorder {
    private static final Logger LOG = Logger.getLogger(JvmOnlyRecorder.class);

    public JvmOnlyRecorder() {
    }

    public static void warnJvmInNative(Logger log, String feature) {
        log.warnf(
                "The %s extension was not tested in native mode. You may want to report about the success or failure running it in native mode on https://github.com/quarkiverse/quarkus-jnosql/issues?q=is:issue+%s",
                feature, feature.replace('-', '+'));
    }

    public void warnJvmInNative(String feature) {
        warnJvmInNative(LOG, feature);
    }
}
