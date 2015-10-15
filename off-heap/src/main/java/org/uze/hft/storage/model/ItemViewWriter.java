package org.uze.hft.storage.model;

import org.uze.hft.storage.ItemView;

import java.nio.ByteBuffer;

/**
 * Created by Y.Kiselev on 02.10.2015.
 */
public final class ItemViewWriter {

    public static final int ITEM_VIEW_BYTES = 7 * ByteArrayReader.LONG_BYTES
            + ByteArrayReader.DOUBLE_BYTES + 2 * ByteArrayReader.INTEGER_BYTES;

    public static final int ID = 0;

    public static final int NAME = ID + ByteArrayReader.LONG_BYTES;

    public static final int BOOK = NAME + ByteArrayReader.LONG_BYTES;

    public static final int PRODOUCT_TYPE = BOOK + ByteArrayReader.LONG_BYTES;

    public static final int TYPE = PRODOUCT_TYPE + ByteArrayReader.LONG_BYTES;

    public static final int STATUS = TYPE + ByteArrayReader.LONG_BYTES;

    public static final int TIMESTAMP = STATUS + ByteArrayReader.LONG_BYTES;

    public static final int VALUE = TIMESTAMP + ByteArrayReader.LONG_BYTES;

    public static final int VERSION = VALUE + ByteArrayReader.DOUBLE_BYTES;

    public static void write(ByteBuffer buf, ItemView itemView) {
        buf.putLong(itemView.getId());
        buf.putLong(itemView.getName());
        buf.putLong(itemView.getBook());
        buf.putLong(itemView.getProductType());
        buf.putLong(itemView.getType());
        buf.putLong(itemView.getStatus());
        buf.putLong(itemView.getTimestamp());
        buf.putDouble(itemView.getValue());
        buf.putInt(itemView.getVersion());
        // see also {@code org.uze.serialization.storage.org.uze.storages.model.ItemViewWriter.ITEM_VIEW_BYTES}
        buf.putInt(0);
    }

    public static ItemView createView(ByteArray array, int offset) {
        return new MyItemView(array, offset);
    }

    private static class MyItemView implements ItemView {

        private final ByteArray array;

        private final int base;

        public MyItemView(ByteArray array, int base) {
            this.array = array;
            this.base = base;
        }

        @Override
        public long getId() {
            return readLong(base + ID);
        }

        @Override
        public long getName() {
            return readLong(base + NAME);
        }

        @Override
        public long getBook() {
            return readLong(base + BOOK);
        }

        @Override
        public long getProductType() {
            return readLong(base + PRODOUCT_TYPE);
        }

        @Override
        public long getType() {
            return readLong(base + TYPE);
        }

        @Override
        public long getStatus() {
            return readLong(base + STATUS);
        }

        @Override
        public long getTimestamp() {
            return readLong(base + TIMESTAMP);
        }

        @Override
        public double getValue() {
            return readDouble(base + VALUE);
        }

        @Override
        public int getVersion() {
            return readInt(base + VERSION);
        }

        @Override
        public void setId(long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setName(long name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBook(long book) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setProductType(long productType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setType(long type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setStatus(long status) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setTimestamp(long timestamp) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setValue(double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setVersion(int version) {
            throw new UnsupportedOperationException();
        }

        private byte readByte(int offset) {
            return array.getByteAt(offset);
        }

        public long readLong(int offset) {
            long result = 0;
            for (int i = 0; i < ByteArrayReader.LONG_BYTES; i++) {
                result <<= Byte.SIZE;
                result |= (readByte(offset++) & 0xff);
            }
            return result;
        }

        public double readDouble(int offset) {
            return Double.longBitsToDouble(readLong(offset));
        }

        public int readInt(int offset) {
            int result = 0;
            for (int i = 0; i < ByteArrayReader.INTEGER_BYTES; i++) {
                result <<= Byte.SIZE;
                result |= (readByte(offset++) & 0xff);
            }
            return result;
        }

        public short readShort(int offset) {
            short result = 0;
            for (int i = 0; i < ByteArrayReader.SHORT_BYTES; i++) {
                result <<= Byte.SIZE;
                result |= (readByte(offset++) & 0xff);
            }
            return result;
        }

    }
}
