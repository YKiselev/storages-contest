package org.uze.serialization.storage.model;

/**
 * Created by Y.Kiselev on 02.10.2015.
 */
public interface ByteArray {

    void setByteAt(int index, byte value);

    byte getByteAt(int index);
}
