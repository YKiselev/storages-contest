package org.uze.serialization.utils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Y.Kiselev on 15.10.2015.
 */
public class AbstractConsumer {

    private final AtomicLong dump = new AtomicLong();

    public final long getDump() {
        return dump.get();
    }

    protected final void dump(Object value){
        dump.addAndGet(System.identityHashCode(Objects.requireNonNull(value)));
    }

    protected final void dump(long value){
        dump.addAndGet(value);
    }

    protected final void dump(double value){
        dump.addAndGet(Double.doubleToLongBits(value));
    }

}
