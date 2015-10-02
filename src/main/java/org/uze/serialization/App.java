package org.uze.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uze.serialization.json.ItemMixIn;
import org.uze.serialization.org.uze.serialization.model.Item;
import org.uze.serialization.utils.ItemFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public class App {

    private final Logger logger = LogManager.getLogger(getClass());

    private final ObjectMapper mapper;

    public static void main(String[] args) throws Exception {
        new App().run(10_000);
    }

    public App() {
        final ObjectMapper mapper = new ObjectMapper();

        mapper.addMixIn(Item.class, ItemMixIn.class);

        this.mapper = mapper;
    }

    private void run(int size) throws Exception {
        logger.info("Creating {} items...", size);
        final List<Item> items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            items.add(ItemFactory.create());
        }
        logger.info("Items created");
        long dummy = 0;
        for (int i = 0; i < 15; i++) {
            final Stopwatch sw = Stopwatch.createStarted();
            dummy += testJson(items);
            final long ms1 = sw.elapsed(TimeUnit.MICROSECONDS);
            dummy += testSerializable(items);
            final long ms2 = sw.elapsed(TimeUnit.MICROSECONDS);
            logger.info("Round {}: Json time (mks): {}, Serializable time (mks): {}", i, ms1, ms2 - ms1);
        }
        logger.debug("dummy: {}", dummy);
    }

    private long testJson(List<Item> items) throws Exception {
        final String tmp = mapper.writeValueAsString(items);
        final List<Item> result = mapper.readValue(tmp, new TypeReference<List<Item>>() {
        });
        Preconditions.checkArgument(Objects.equals(items, result), "Json processing failed!");
        return result.hashCode();
    }

    private long testSerializable(List<Item> items) throws Exception {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(items);

            try (ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                 ObjectInputStream ois = new ObjectInputStream(is)) {
                final List<Item> result = (List<Item>) ois.readObject();
                Preconditions.checkArgument(Objects.equals(items, result), "Serialization failed!");
                return result.hashCode();
            }
        }
    }
}
