package org.uze.storages.utils;

import org.uze.storages.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public class ItemFactory {

    public static final int MAX_ITEMS = 2_000_000;

    private static final String[] BOOKS = new String[]{
            "ABC", "CDE", "DEF", "EFG", "HIJ", "KLMN", "OPQR", "SPQR"
    };

    private static final String[] PRODUCT_TYPES = new String[]{
            "AX", "DX", "ZX", "MMM", "XYZ", "WWW", "QQQ"
    };

    private static final String[] TYPES = new String[]{
            "FxSwap", "FxOption", "FxNonDelivarable", "Commodity"
    };

    private static final String[] STATUSES = new String[]{
            "Open", "Closed", "Matured", "Tenured", "Cancelled"
    };

    public static int averageKeySize() {
        return 20 * 2; // UUID = (20 chars) * (2 bytes per character)
    }

    public static int averageValueSize() {
        return averageKeySize() + 2 * (64 + 4 + 3 + 16 + 8) + 8 + 8 + 4;
    }

    public static Item create() {
        final ThreadLocalRandom rnd = ThreadLocalRandom.current();
        return new Item(
                UUID.randomUUID().toString(),
                randomString(rnd.nextInt(8, 64)),
                BOOKS[rnd.nextInt(0, BOOKS.length)],
                PRODUCT_TYPES[rnd.nextInt(0, PRODUCT_TYPES.length)],
                TYPES[rnd.nextInt(0, TYPES.length)],
                STATUSES[rnd.nextInt(0, STATUSES.length)],
                System.currentTimeMillis(),
                rnd.nextDouble(10.0, 999_000_000.0),
                rnd.nextInt(0, 255));
    }

    public static List<Item> createList(int count) {
        final List<Item> items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            items.add(ItemFactory.create());
        }
        return items;
    }

    public static List<Item> createList() {
        return createList(MAX_ITEMS);
    }

    private static String randomString(int length) {
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) ThreadLocalRandom.current().nextLong(31, 127));
        }
        return sb.toString();
    }
}
