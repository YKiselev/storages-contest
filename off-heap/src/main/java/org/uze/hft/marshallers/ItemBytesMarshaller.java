package org.uze.hft.marshallers;

import net.openhft.lang.io.Bytes;
import net.openhft.lang.io.serialization.BytesMarshaller;
import org.uze.storages.model.Item;

/**
 * Created by Y.Kiselev on 14.10.2015.
 */
public class ItemBytesMarshaller implements BytesMarshaller<Item> {

    private static final long serialVersionUID = -1089528148171500855L;

    public static final ItemBytesMarshaller INSTANCE = new ItemBytesMarshaller();

    private static void write(Bytes bytes, String value) {
        bytes.writeUTFΔ(value);
    }

    private static String readString(Bytes bytes){
        return bytes.readUTFΔ();
    }

    @Override
    public void write(Bytes bytes, Item item) {
        write(bytes, item.getId());
        write(bytes, item.getName());
        write(bytes, item.getBook());
        write(bytes, item.getProductType());
        write(bytes, item.getType());
        write(bytes, item.getStatus());
        bytes.writeLong(item.getTimestamp());
        bytes.writeDouble(item.getValue());
        bytes.writeCompactInt(item.getVersion());
    }

    @Override
    public Item read(Bytes bytes) {
        return read(bytes, null);
    }

    @Override
    public Item read(Bytes bytes, Item item) {
        return new Item(
                readString(bytes),
                readString(bytes),
                readString(bytes),
                readString(bytes),
                readString(bytes),
                readString(bytes),
                bytes.readLong(),
                bytes.readDouble(),
                bytes.readCompactInt()
        );
    }
}
