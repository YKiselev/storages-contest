package org.uze.serialization.utils;

import com.google.common.base.Preconditions;
import org.uze.serialization.storage.ItemView;
import org.uze.serialization.storage.StringStorage;

import java.util.Objects;

/**
 * Created by Y.Kiselev on 02.10.2015.
 */
public class DefaultItemViewConsumer extends AbstractConsumer {

    private final StringStorage stringStorage;

    public DefaultItemViewConsumer(StringStorage stringStorage) {
        this.stringStorage = stringStorage;
    }

    protected final String getString(long id) {
        return Objects.requireNonNull(stringStorage.get(id));
    }

    public void consume(ItemView itemView) {
        final String id = getString(itemView.getId());
        dump(id);

        final String name = getString(itemView.getName());
        dump(name);

        final double value = itemView.getValue();
        dump(value);

        final String status = getString(itemView.getStatus());
        dump(status);

        final int version = itemView.getVersion();
        Preconditions.checkArgument(version >= 0, "version");
        dump(version);
    }
}
