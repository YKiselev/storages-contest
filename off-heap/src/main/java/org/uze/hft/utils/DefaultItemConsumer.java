package org.uze.hft.utils;

import com.google.common.base.Preconditions;
import org.uze.storages.model.Item;

import java.util.Collection;

/**
 * Created by Y.Kiselev on 15.10.2015.
 */
public class DefaultItemConsumer extends AbstractConsumer {

    public void consume(Collection<Item> items) {
        for (Item item : items) {
            consume(item);
        }
    }

    public void consume(Item item) {
        final String id = item.getId();
        consume(id);

        final String name = item.getName();
        consume(name);

        final double value = item.getValue();
        consume(value);

        final String status = item.getStatus();
        consume(status);

        final int version = item.getVersion();
        Preconditions.checkArgument(version >= 0, "version");
        consume(version);
    }
}
