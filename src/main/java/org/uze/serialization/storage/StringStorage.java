package org.uze.serialization.storage;

import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public class StringStorage {

    private final ChronicleMap<String, Long> stringToIds;

    private final ChronicleMap<Long, String> idToStrings;

    private final AtomicLong sequence = new AtomicLong();

    public StringStorage(int size, int averageStringSizeInBytes) {
        stringToIds = ChronicleMapBuilder.of(String.class, Long.class)
                .averageKeySize(averageStringSizeInBytes)
                .entries(size)
                .create();

        idToStrings = ChronicleMapBuilder.of(Long.class, String.class)
                .averageValueSize(averageStringSizeInBytes)
                .entries(size)
                .create();
    }

    public synchronized long put(String value) {
        Long id = stringToIds.get(value);
        if (id == null) {
            id = sequence.incrementAndGet();
            stringToIds.put(value, id);
            idToStrings.put(id, value);
        }
        return id;
    }

    public String get(long id) {
        return idToStrings.get(id);
    }
}
