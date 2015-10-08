package org.uze.serialization.storage;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Y.Kiselev on 03.10.2015.
 */
public class UnsafeStringStorage implements StringStorage, AutoCloseable {

    public static final int CHAR_BYTES = Character.SIZE / Byte.SIZE;

    public static final int INTEGER_BYTES = Integer.SIZE / Byte.SIZE;

    private final Logger logger = LogManager.getLogger(getClass());

    public static final int LONG_BYTES = Long.SIZE / Byte.SIZE;

    private static final int HEADER_SIZE = 32;

    private static final long LOCK_OFFSET = 0L;

    private static final long POSITION_OFFSET = LOCK_OFFSET + LONG_BYTES;

    private final ChronicleMap<String, Long> stringToIds;

    private final long address;

    private final long rawAddress;

    private final Unsafe unsafe = UnsafeForAll.getUnsafe();

    private final long length;

    private int byteArrayBaseOffset = unsafe.arrayBaseOffset(byte[].class);

    private int charArrayBaseOffset = unsafe.arrayBaseOffset(char[].class);
    private final Field stringValueField;

    public UnsafeStringStorage(int size, int averageStringSizeInBytes) {
        stringToIds = ChronicleMapBuilder.of(String.class, Long.class)
                .averageKeySize(averageStringSizeInBytes)
                .entries(size)
                .create();

        length = size * averageStringSizeInBytes;
        rawAddress = unsafe.allocateMemory(length);
        address = align(rawAddress);
        if (address != rawAddress) {
            logger.info("Aligned: {}->{}", rawAddress, address);
        }
        lock();
        try {
            final long position = unsafe.getLong(address + POSITION_OFFSET);
            if (isInRange(position)) {
                // Ok
            } else {
                setNewPos(address + HEADER_SIZE);
            }
        } finally {
            unlock();
        }

        try {
            stringValueField = String.class.getDeclaredField("value");
            stringValueField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new AssertionError(e);
        }
    }

    private boolean isInRange(long addr) {
        return addr >= address && addr < address + length;
    }

    @Override
    public void close() throws Exception {
        unsafe.freeMemory(rawAddress);
    }

    private static long align(long address) {
        return address + address % 8;
    }

    private void lock() {
        final Stopwatch sw = Stopwatch.createStarted();
        final long id = Thread.currentThread().getId();
        while (sw.elapsed(TimeUnit.MILLISECONDS) < 1000) {
            if (unsafe.compareAndSwapLong(null, address + LOCK_OFFSET, 0L, id)) {
                return;
            }
        }
        logger.warn("Lock wait timed out, taking lock forcibly...");
        // take lock (probably stall one)
        unsafe.putLong(address + LOCK_OFFSET, id);
        Preconditions.checkState(unsafe.getLong(address + LOCK_OFFSET) == id, "Lock failed!");
    }

    private void unlock() {
        final long id = Thread.currentThread().getId();
        if (!unsafe.compareAndSwapLong(null, address + LOCK_OFFSET, id, 0L)) {
            logger.warn("Releasing lock not held by {}", id);
        }
    }

    @Override
    public synchronized long put(String value) {
        Long id = stringToIds.get(value);
        if (id == null) {
            id = storeUtf16(value);
            stringToIds.put(value, id);
        }
        return id;
    }

    private long storeUtf8(String value) {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        final long id;
        lock();
        try {
            id = unsafe.getLong(address + POSITION_OFFSET);
            Preconditions.checkArgument(isInRange(id + bytes.length), "Out of memory!");
            long pos = id;
            unsafe.putInt(pos, bytes.length);
            pos += INTEGER_BYTES;
            for (byte b : bytes) {
                unsafe.putByte(pos++, b);
            }
            setNewPos(pos);
        } finally {
            unlock();
        }
        return id;
    }

    private void setNewPos(long pos) {
        unsafe.putLong(address + POSITION_OFFSET, align(pos));
    }

    private long storeUtf16(String value) {
        final long id;
        final int charCount = value.length();
        lock();
        try {
            id = unsafe.getLong(address + POSITION_OFFSET);
            Preconditions.checkArgument(isInRange(id + charCount * CHAR_BYTES), "Out of memory!");
            long pos = id;
            unsafe.putInt(pos, charCount);
            pos += INTEGER_BYTES;
            for (int i = 0; i < value.length(); i++) {
                unsafe.putChar(pos, value.charAt(i));
                pos += CHAR_BYTES;
            }
            setNewPos(pos);
        } finally {
            unlock();
        }
        return id;
    }

    @Override
    public String get(long id) {
        return getUtf16(id);
    }

    private String getUtf8(long id) {
        long pos = id;
        final int len = unsafe.getInt(pos);
        Preconditions.checkArgument(isInRange(id) && len >= 0 && isInRange(id + len), "Invalid string id: " + id);
        pos += INTEGER_BYTES;
        final byte[] buff = new byte[len];
        unsafe.copyMemory(null, pos, buff, byteArrayBaseOffset, len);
        return new String(buff, StandardCharsets.UTF_8);
    }

    private String getUtf16(long id) {
        long pos = id;
        final int len = unsafe.getInt(pos);
        Preconditions.checkArgument(isInRange(id) && len >= 0 && isInRange(id + len * CHAR_BYTES), "Invalid string id: " + id);
        pos += INTEGER_BYTES;
//        try {
//            final char[] buff = new char[len];
//            unsafe.copyMemory(null, pos, buff, charArrayBaseOffset, len * CHAR_BYTES);
//            final String result = (String)unsafe.allocateInstance(String.class);
//            stringValueField.set(result, buff);
//            return result;
//        } catch (InstantiationException|IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
        final char[] buff = new char[len];
        unsafe.copyMemory(null, pos, buff, charArrayBaseOffset, len * CHAR_BYTES);
        return new String(buff);
    }

    @Override
    public void printStat(Logger logger) {
        final Map<Integer, Integer> map = new HashMap<>();

        for (Map.Entry<String, Long> entry : stringToIds.entrySet()) {
            final String str = entry.getKey();
            final int key = str.length();
            Integer counter = map.get(key);
            if (counter == null) {
                counter = 1;
            } else {
                counter = counter + 1;
            }
            map.put(key, counter);
        }

        final StringBuilder sb = new StringBuilder("String storage info (length:count):\n");
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }
        logger.info(sb.toString());
    }
}
