package org.uze.storages.coherence;

import com.google.common.base.Preconditions;
import org.uze.storages.model.Item;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Y.Kiselev on 02.10.2015.
 */
public class ItemConsumer {

    private final AtomicLong dump = new AtomicLong();

    public long getDump() {
        return dump.get();
    }

    public void consume(Item itemView) {
        final String id = itemView.getId();
        dump.addAndGet(System.identityHashCode(id));

        final String name = itemView.getName();
        dump.addAndGet(System.identityHashCode(name));

        final double value = itemView.getValue();
        dump.addAndGet(Double.doubleToLongBits(value));

        final String status = itemView.getStatus();
        dump.addAndGet(System.identityHashCode(status));

        final int version = itemView.getVersion();
        Preconditions.checkArgument(version >= 0, "version");
        dump.addAndGet(version);
    }
}
