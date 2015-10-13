package org.uze.serialization.utils;

import com.google.common.base.Preconditions;
import org.uze.serialization.storage.ItemView;
import org.uze.serialization.storage.StringStorage;
import org.uze.storages.model.Item;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Y.Kiselev on 02.10.2015.
 */
public class ItemConsumer {

    private final AtomicLong dump = new AtomicLong();

    public long getDump() {
        return dump.get();
    }

    public void consume(Item item) {
        final String id = item.getId();
        dump.addAndGet(System.identityHashCode(id));

        final String name = item.getName();
        dump.addAndGet(System.identityHashCode(name));

        final double value = item.getValue();
        dump.addAndGet(Double.doubleToLongBits(value));

        final String status = item.getStatus();
        dump.addAndGet(System.identityHashCode(status));

        final int version = item.getVersion();
        Preconditions.checkArgument(version >= 0, "version");
        dump.addAndGet(version);
    }
}
