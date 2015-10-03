package org.uze.serialization.utils;

import com.google.common.base.Preconditions;
import org.uze.serialization.storage.ItemView;
import org.uze.serialization.storage.StringStorage;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Y.Kiselev on 02.10.2015.
 */
public class ItemViewConsumer {

    private final AtomicLong dump = new AtomicLong();

    private final StringStorage stringStorage;

    public long getDump() {
        return dump.get();
    }

    public ItemViewConsumer(StringStorage stringStorage) {
        this.stringStorage = stringStorage;
    }

    public void consume(ItemView itemView) {
        final String id = stringStorage.get(itemView.getId());
        Objects.requireNonNull(id, "id");
        dump.addAndGet(System.identityHashCode(id));

        final String name = stringStorage.get(itemView.getName());
        Objects.requireNonNull(name, "name");
        dump.addAndGet(System.identityHashCode(name));

        final double value = itemView.getValue();
        dump.addAndGet(Double.doubleToLongBits(value));

        final String status = stringStorage.get(itemView.getStatus());
        Objects.requireNonNull(status, "status");

        final int version = itemView.getVersion();
        if (version < 0) {
            dump.addAndGet(version);
        }
        Preconditions.checkArgument(version >= 0, "version");
        dump.addAndGet(version);
    }
}
