package org.uze.hft.utils;

import com.google.common.base.Preconditions;
import org.uze.hft.storage.ItemView;
import org.uze.hft.storage.strings.StringStorage;

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
        consume(id);

        final String name = getString(itemView.getName());
        consume(name);

        final double value = itemView.getValue();
        consume(value);

        final String status = getString(itemView.getStatus());
        consume(status);

        final int version = itemView.getVersion();
        Preconditions.checkArgument(version >= 0, "version");
        consume(version);
    }
}
