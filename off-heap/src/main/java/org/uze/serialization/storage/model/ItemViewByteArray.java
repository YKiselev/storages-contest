package org.uze.serialization.storage.model;

import net.openhft.lang.model.constraints.MaxSize;

/**
 * Created by Y.Kiselev on 02.10.2015.
 */
public interface ItemViewByteArray extends ByteArray {

    void setByteAt(@MaxSize(256 * 1024) int index, byte value);
}
