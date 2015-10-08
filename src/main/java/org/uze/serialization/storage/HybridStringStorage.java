package org.uze.serialization.storage;

import com.google.common.base.Preconditions;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Y.Kiselev on 04.10.2015.
 */
public class HybridStringStorage implements StringStorage {

    private final ChronicleMap<String, Long> stringToIds;

    private final String[] strings;

    private final AtomicInteger counter = new AtomicInteger(0);

    public HybridStringStorage(int entries, int averageStringSizeInBytes) {
        stringToIds = ChronicleMapBuilder.of(String.class, Long.class)
                .averageKeySize(averageStringSizeInBytes)
                .entries(entries)
                .create();

        strings = new String[entries];
    }

    @Override
    public long put(String value) {
        final Long id = stringToIds.get(value);
        if (id != null) {
            return id;
        }
        Preconditions.checkArgument(counter.get() < strings.length, "Out of slots!");
        final int idx = counter.getAndIncrement();
        strings[idx] = value;
        stringToIds.put(value, (long) idx);
        return idx;
    }

    @Override
    public String get(long id) {
        final int idx = (int) id;
        Preconditions.checkArgument(idx == id, "Truncated: " + id);
        Preconditions.checkElementIndex(idx, counter.get());
        return strings[idx];
    }

    @Override
    public void printStat(Logger logger) {
        // todo
    }
}
