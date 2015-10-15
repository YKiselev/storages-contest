package org.uze.serialization.utils;

import org.uze.storages.model.Item;

/**
 * Created by Y.Kiselev on 15.10.2015.
 */
public class PartialItemConsumer extends DefaultItemConsumer {

    public void consume(Item item) {
        final String name = item.getName();
        dump(name);

        final double value = item.getValue();
        dump(value);
    }
}
