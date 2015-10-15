package org.uze.hft.storage;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by Y.Kiselev on 03.10.2015.
 */
public class UnsafeForAll {

    private static final Unsafe unsafe;

    static {
        try {
            @SuppressWarnings("ALL")
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }
}
