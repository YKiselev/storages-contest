package org.uze.hft.storage.model;

import net.openhft.lang.model.constraints.MaxSize;

/**
 * Created by Y.Kiselev on 02.10.2015.
 */
public interface ItemViewByteArray extends ByteArray {

    /**
     * @param index keep this in sync with ManyItemViewsPerEntryApp#MAX_ITEMS_PER_ENTRY * org.uze.hft.storage.model.ItemViewWriter#ITEM_VIEW_BYTES + HEADER_SIZE
     */
    void setByteAt(@MaxSize(217 * 1024) int index, byte value);
}
