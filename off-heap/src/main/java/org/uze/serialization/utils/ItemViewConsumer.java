package org.uze.serialization.utils;

import org.uze.serialization.storage.ItemView;

/**
 * Created by Y.Kiselev on 15.10.2015.
 */
public interface ItemViewConsumer {

    long getDump();

    void consume(ItemView itemView);
}
