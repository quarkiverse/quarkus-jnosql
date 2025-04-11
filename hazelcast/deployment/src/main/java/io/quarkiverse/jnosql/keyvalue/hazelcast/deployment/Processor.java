package io.quarkiverse.jnosql.keyvalue.hazelcast.deployment;

import java.util.stream.Stream;

import org.eclipse.jnosql.databases.hazelcast.communication.QuarkusHazelcastBucketManagerProducer;
import org.eclipse.jnosql.databases.hazelcast.communication.QuarkusHazelcastKeyValueConfiguration;

import com.hazelcast.internal.memory.GlobalMemoryAccessorRegistry;
import com.hazelcast.internal.memory.HeapMemoryAccessor;
import com.hazelcast.internal.memory.impl.AlignmentUtil;
import com.hazelcast.internal.memory.impl.EndiannessUtil;
import com.hazelcast.internal.util.ICMPHelper;
import com.hazelcast.internal.util.JVMUtil;
import com.hazelcast.internal.util.ThreadAffinityHelper;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.query.impl.predicates.MultiPartitionPredicateImpl;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-hazelcast";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusHazelcastKeyValueConfiguration.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusHazelcastBucketManagerProducer.class));
    }

    @BuildStep
    void markRuntimeInitializedClasses(BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitializedClassesProducer) {

        Stream.of(ICMPHelper.class,
                ThreadAffinityHelper.class,
                JVMUtil.class,
                AlignmentUtil.class,
                EndiannessUtil.class,
                GlobalMemoryAccessorRegistry.class,
                HeapMemoryAccessor.class,
                SwCounter.class,
                MultiPartitionPredicateImpl.class)
                .map(Class::getName)
                .map(RuntimeInitializedClassBuildItem::new)
                .forEach(runtimeInitializedClassesProducer::produce);

    }

}
