package org.uze.serialization.marshallers;

import net.openhft.lang.io.Bytes;
import net.openhft.lang.io.serialization.BytesMarshaller;
import org.uze.storages.model.Item;

/**
 * Created by Y.Kiselev on 14.10.2015.
 */
public class ItemBytesMarshaller implements BytesMarshaller<Item> {

    private static final long serialVersionUID = -1089528148171500855L;

    public static final ItemBytesMarshaller INSTANCE = new ItemBytesMarshaller();

    @Override
    public void write(Bytes bytes, Item item) {
        bytes.writeUTF?(item.getId());
        bytes.writeUTF?(item.getName());
        bytes.writeUTF?(item.getBook());
        bytes.writeUTF?(item.getProductType());
        bytes.writeUTF?(item.getType());
        bytes.writeUTF?(item.getStatus());
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
                bytes.readUTF(),
                bytes.readUTF(),
                bytes.readUTF(),
                bytes.readUTF(),
                bytes.readUTF(),
                bytes.readUTF(),
                bytes.readLong(),
                bytes.readDouble(),
                bytes.readCompactInt()
        );
    }
}
