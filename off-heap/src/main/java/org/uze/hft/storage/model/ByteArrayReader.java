package org.uze.hft.storage.model;

/**
 * Created by Y.Kiselev on 02.10.2015.
 */
public class ByteArrayReader {

    public static final int INT_BYTES = Integer.SIZE / Byte.SIZE;

    public static final int SHORT_BYTES = Short.SIZE / Byte.SIZE;

    public static final int LONG_BYTES = Long.SIZE / Byte.SIZE;

    public static final int DOUBLE_BYTES = Double.SIZE / Byte.SIZE;

    public static final int INTEGER_BYTES = Integer.SIZE / Byte.SIZE;

    private final ByteArray byteArray;

    private int offset;

    public ByteArrayReader(ByteArray byteArray) {
        this.byteArray = byteArray;
    }

    private byte readByte() {
        return byteArray.getByteAt(offset++);
    }

    public long readLong() {
        long result = 0;
        for (int i = 0; i < LONG_BYTES; i++) {
            result <<= Byte.SIZE;
            result |= (readByte() & 0xff);
        }
        return result;
    }

    public int readInt() {
        int result = 0;
        for (int i = 0; i < INT_BYTES; i++) {
            result <<= Byte.SIZE;
            result |= (readByte() & 0xff);
        }
        return result;
    }

    public short readShort() {
        short result = 0;
        for (int i = 0; i < SHORT_BYTES; i++) {
            result <<= Byte.SIZE;
            result |= (readByte() & 0xff);
        }
        return result;
    }

    public void skip(int bytes) {
        offset += bytes;
    }
}
