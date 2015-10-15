package org.uze.hft.utils;

import org.uze.hft.storage.ItemView;
import org.uze.hft.storage.strings.StringStorage;

/**
 * Created by Y.Kiselev on 02.10.2015.
 */
public class PartialItemViewConsumer extends DefaultItemViewConsumer {

    public PartialItemViewConsumer(StringStorage stringStorage) {
        super(stringStorage);
    }

    public void consume(ItemView itemView) {
        final String name = getString(itemView.getName());
        consume(name);

        final double value = itemView.getValue();
        consume(value);
    }
}
