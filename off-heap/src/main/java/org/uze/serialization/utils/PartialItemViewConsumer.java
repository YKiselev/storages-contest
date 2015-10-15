package org.uze.serialization.utils;

import org.uze.serialization.storage.ItemView;
import org.uze.serialization.storage.StringStorage;

/**
 * Created by Y.Kiselev on 02.10.2015.
 */
public class PartialItemViewConsumer extends DefaultItemViewConsumer {

    public PartialItemViewConsumer(StringStorage stringStorage) {
        super(stringStorage);
    }

    public void consume(ItemView itemView) {
        final String name = getString(itemView.getName());
        dump(name);

        final double value = itemView.getValue();
        dump(value);
    }
}
