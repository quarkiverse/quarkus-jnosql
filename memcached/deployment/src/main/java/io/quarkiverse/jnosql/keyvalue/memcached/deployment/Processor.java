package io.quarkiverse.jnosql.keyvalue.memcached.deployment;

import org.eclipse.jnosql.databases.memcached.communication.QuarkusMemcachedBucketManagerFactoryProducer;
import org.eclipse.jnosql.databases.memcached.communication.QuarkusMemcachedBucketManagerProducer;
import org.eclipse.jnosql.databases.memcached.communication.QuarkusMemcachedKeyValueConfiguration;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import io.quarkiverse.jnosql.memcached.runtime.ContextObjectInputStream;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class Processor {

    private static final String FEATURE = "jnosql-memcached";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
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
