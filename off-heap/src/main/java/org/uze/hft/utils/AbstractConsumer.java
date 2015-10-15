package org.uze.hft.utils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Y.Kiselev on 15.10.2015.
 */
public class AbstractConsumer implements BlackHole {

    private final AtomicLong blackHole = new AtomicLong();

    @Override
    public final long getValue() {
        return blackHole.get();
    }

    protected final void consume(Object value){
        blackHole.addAndGet(System.identityHashCode(Objects.requireNonNull(value)));
    }

    protected final void consume(long value){
        blackHole.addAndGet(value);
    }

    protected final void consume(double value){
        blackHole.addAndGet(Double.doubleToLongBits(value));
    }

}
