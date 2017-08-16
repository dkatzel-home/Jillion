package org.jcvi.jillion.core.util.streams;
/**
 * Functional interface that takes 2 parameters, a primitive index (offset)
 * and the long value at that offset.
 * @author dkatzel
 *
 * @param <E> the exception that could be thrown.
 *
 * @since 5.3
 */
@FunctionalInterface
public interface ThrowingIntIndexedLongConsumer<E extends Throwable> {

    void accept(int index, long value) throws E;
}
