package org.uze.serialization.marshallers;

import net.openhft.lang.io.Bytes;
import net.openhft.lang.io.serialization.BytesMarshaller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Y.Kiselev on 14.10.2015.
 */
public class ListBytesMarshaller<T> implements BytesMarshaller<List<T>> {

    private static final long serialVersionUID = -2592243994776267233L;

    private final BytesMarshaller<T> itemMarshaller;

    public ListBytesMarshaller(BytesMarshaller<T> itemMarshaller) {
        this.itemMarshaller = itemMarshaller;
    }

    private static void write(Bytes bytes, String value) {
        bytes.writeUTFΔ(value);
    }

    private static String readString(Bytes bytes) {
        return bytes.readUTFΔ();
    }

    @Override
    public void write(Bytes bytes, List<T> value) {
        bytes.writeCompactInt(value.size());

        for (T item : value) {
            itemMarshaller.write(bytes, item);
        }
    }

    @Override
    public List<T> read(Bytes bytes) {
        return read(bytes, null);
    }

    @Override
    public List<T> read(Bytes bytes, List<T> items) {
        final int count = bytes.readCompactInt();

        final List<T> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(itemMarshaller.read(bytes));
        }
        return result;
    }
}
