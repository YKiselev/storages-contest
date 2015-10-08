package org.uze.serialization.storage.model;

import org.uze.serialization.storage.ItemView;

import java.nio.ByteBuffer;

/**
 * Created by Y.Kiselev on 02.10.2015.
 */
public final class ItemViewWriter {

    public static final int LONG_BYTES = Long.SIZE / Byte.SIZE;

    public static final int DOUBLE_BYTES = Double.SIZE / Byte.SIZE;

    public static final int INTEGER_BYTES = Integer.SIZE / Byte.SIZE;

    public static final int SHORT_BYTES = Short.SIZE / Byte.SIZE;

    public static final int ITEM_VIEW_BYTES = 7 * LONG_BYTES + DOUBLE_BYTES + 2 * INTEGER_BYTES;

    public static final int ID = 0;

    public static final int NAME = ID + LONG_BYTES;

    public static final int BOOK = NAME + LONG_BYTES;

    public static final int PRODOUCT_TYPE = BOOK + LONG_BYTES;

    public static final int TYPE = PRODOUCT_TYPE + LONG_BYTES;

    public static final int STATUS = TYPE + LONG_BYTES;

    public static final int TIMESTAMP = STATUS + LONG_BYTES;

    public static final int VALUE = TIMESTAMP + LONG_BYTES;

    public static final int VERSION = VALUE + DOUBLE_BYTES;

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
        // see also {@code org.uze.serialization.storage.model.ItemViewWriter.ITEM_VIEW_BYTES}
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

        }

        @Override
        public void setName(long name) {

        }

        @Override
        public void setBook(long book) {

        }

        @Override
        public void setProductType(long productType) {

        }

        @Override
        public void setType(long type) {

        }

        @Override
        public void setStatus(long status) {

        }

        @Override
        public void setTimestamp(long timestamp) {

        }

        @Override
        public void setValue(double value) {

        }

        @Override
        public void setVersion(int version) {

        }

        private byte readByte(int offset) {
            return array.getByteAt(offset);
        }

        public long readLong(int offset) {
            long result = 0;
            for (int i = 0; i < LONG_BYTES; i++) {
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
            for (int i = 0; i < INTEGER_BYTES; i++) {
                result <<= Byte.SIZE;
                result |= (readByte(offset++) & 0xff);
            }
            return result;
        }

        public short readShort(int offset) {
            short result = 0;
            for (int i = 0; i < SHORT_BYTES; i++) {
                result <<= Byte.SIZE;
                result |= (readByte(offset++) & 0xff);
            }
            return result;
        }

    }
}
