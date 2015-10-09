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

    private String getString(long id){
        return Objects.requireNonNull(stringStorage.get(id));
    }

    public void consume(ItemView itemView) {
        final String id = getString(itemView.getId());
        dump.addAndGet(System.identityHashCode(id));

        final String name = getString(itemView.getName());
        dump.addAndGet(System.identityHashCode(name));

        final double value = itemView.getValue();
        dump.addAndGet(Double.doubleToLongBits(value));

        final String status = getString(itemView.getStatus());
        dump.addAndGet(System.identityHashCode(status));

        final int version = itemView.getVersion();
        Preconditions.checkArgument(version >= 0, "version");
        dump.addAndGet(version);
    }
}
