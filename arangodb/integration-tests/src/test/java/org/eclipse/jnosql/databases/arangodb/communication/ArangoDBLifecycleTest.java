package org.eclipse.jnosql.databases.arangodb.communication;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

class ArangoDBLifecycleTest {

    @Test
    void shouldCloseDocumentManager() {
        ArangoDBDocumentManager manager = mock(ArangoDBDocumentManager.class);

        new QuarkusArangoDBDocumentManagerProducer().close(manager);

        verify(manager).close();
    }

    @Test
    void shouldCloseBucketManager() {
        ArangoDBBucketManager manager = mock(ArangoDBBucketManager.class);

        new QuarkusArangoDBBucketManagerProducer().close(manager);

        verify(manager).close();
    }

    @Test
    void shouldPropagateDocumentShutdownFailure() {
        ArangoDBDocumentManager manager = mock(ArangoDBDocumentManager.class);
        IllegalStateException failure = new IllegalStateException("Cannot close document manager");
        doThrow(failure).when(manager).close();

        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> new QuarkusArangoDBDocumentManagerProducer().close(manager)));
    }

    @Test
    void shouldPropagateBucketShutdownFailure() {
        ArangoDBBucketManager manager = mock(ArangoDBBucketManager.class);
        IllegalStateException failure = new IllegalStateException("Cannot close bucket manager");
        doThrow(failure).when(manager).close();

        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> new QuarkusArangoDBBucketManagerProducer().close(manager)));
    }
}
