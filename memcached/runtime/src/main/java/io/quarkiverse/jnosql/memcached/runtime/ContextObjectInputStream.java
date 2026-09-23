package io.quarkiverse.jnosql.memcached.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ContextObjectInputStream extends ObjectInputStream {

    public ContextObjectInputStream(InputStream input) throws IOException {
        super(input);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass descriptor) throws IOException, ClassNotFoundException {
        try {
            return Class.forName(descriptor.getName(), false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            // ObjectInputStream also resolves primitive type descriptors.
            return super.resolveClass(descriptor);
        }
    }
}
