package io.quarkiverse.jnosql.keyvalue.memcached.deployment;

import java.util.ArrayList;

import jakarta.nosql.Entity;

import org.eclipse.jnosql.databases.memcached.communication.QuarkusMemcachedBucketManagerFactoryProducer;
import org.eclipse.jnosql.databases.memcached.communication.QuarkusMemcachedBucketManagerProducer;
import org.eclipse.jnosql.databases.memcached.communication.QuarkusMemcachedKeyValueConfiguration;
import org.jboss.jandex.AnnotationTarget;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import io.quarkiverse.jnosql.memcached.runtime.ContextObjectInputStream;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-memcached";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    RuntimeInitializedClassBuildItem initializeMetricsRandomAtRuntime() {
        // Metrics' counter hashing must not capture a build-time random seed.
        return new RuntimeInitializedClassBuildItem("com.codahale.metrics.Striped64$HashCode");
    }

    @BuildStep
    void registerEntitySerialization(CombinedIndexBuildItem index,
            BuildProducer<ReflectiveHierarchyBuildItem> hierarchies) {
        index.getIndex().getAnnotations(Entity.class).stream()
                .filter(annotation -> annotation.target().kind() == AnnotationTarget.Kind.CLASS)
                .forEach(annotation -> hierarchies.produce(ReflectiveHierarchyBuildItem
                        .builder(annotation.target().asClass().name())
                        .serialization(true)
                        .build()));
    }

    @BuildStep
    ReflectiveClassBuildItem registerSerializedFieldTypes() {
        // JDK field types are excluded from reflective hierarchy traversal.
        return ReflectiveClassBuildItem.builder(String.class, ArrayList.class).serialization().build();
    }

    @BuildStep
    BytecodeTransformerBuildItem applicationClassLoaderForDeserialization() {
        String stream = ContextObjectInputStream.class.getName().replace('.', '/');
        // The client's plain ObjectInputStream cannot see reloadable application entities.
        return new BytecodeTransformerBuildItem("net.spy.memcached.transcoders.BaseSerializingTranscoder",
                (name, visitor) -> new ClassVisitor(Opcodes.ASM9, visitor) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                            String[] exceptions) {
                        MethodVisitor method = super.visitMethod(access, name, descriptor, signature, exceptions);
                        if (!name.equals("deserialize")) {
                            return method;
                        }
                        return new MethodVisitor(Opcodes.ASM9, method) {
                            @Override
                            public void visitTypeInsn(int opcode, String type) {
                                super.visitTypeInsn(opcode, type.equals("java/io/ObjectInputStream") ? stream : type);
                            }

                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String descriptor,
                                    boolean isInterface) {
                                super.visitMethodInsn(opcode,
                                        owner.equals("java/io/ObjectInputStream") && name.equals("<init>") ? stream : owner,
                                        name, descriptor, isInterface);
                            }
                        };
                    }
                });
    }

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusMemcachedKeyValueConfiguration.class));
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusMemcachedBucketManagerProducer.class));
        additionalBeanProducer
                .produce(AdditionalBeanBuildItem.unremovableOf(QuarkusMemcachedBucketManagerFactoryProducer.class));
    }

    @BuildStep
    void buildExcludedType(BuildProducer<ExcludedTypeBuildItem> excludedTypeProducer) {

        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.keyvalue.configuration.BucketManagerSupplier"));
        excludedTypeProducer.produce(
                new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.keyvalue.configuration.BucketManagerFactorySupplier"));
    }

}
