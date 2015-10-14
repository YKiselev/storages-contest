package org.uze.serialization;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uze.serialization.marshallers.ItemBytesMarshaller;
import org.uze.serialization.marshallers.ListBytesMarshaller;
import org.uze.serialization.utils.ItemConsumer;
import org.uze.storages.model.Item;
import org.uze.storages.utils.ItemFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public class ManyItemsPerEntryApp {

    private final Logger logger = LogManager.getLogger(getClass());

    public static void main(String[] args) throws Exception {
        new ManyItemsPerEntryApp().run();
    }

    private void run() throws Exception {
        logger.info("Starting...");
        final int maxItems = ItemFactory.MAX_ITEMS;
        final int itemsPerEntry = 300;

        logger.info("Creating map...");
        // noinspection unchecked
        final ChronicleMap<Long, List<Item>> keyToItems = ChronicleMapBuilder.of(Long.class, List.class)
                .immutableKeys()
                        //.averageKeySize(ItemFactory.averageKeySize())
                .averageValueSize(ItemFactory.averageValueSize() * itemsPerEntry)
                .valueMarshaller(new ListBytesMarshaller(ItemBytesMarshaller.INSTANCE))
                .entries(maxItems / itemsPerEntry)
                .create();

        final List<Long> keyList = new ArrayList<>(maxItems);
        {
            logger.info("Generating list of {} items...", maxItems);
            final List<Item> items = ItemFactory.createList(maxItems);

            logger.info("Storing items...");
            long key = 1;
            for (List<Item> entryList : Lists.partition(items, itemsPerEntry)) {
                keyList.add(key);
                keyToItems.put(key, entryList);
            }
            logger.info("{} items in {} keys ({} items per entry)", items.size(), keyList.size(), items.size() / keyList.size());
        }

        logger.info("Entering main loop");

        final ThreadLocalRandom rnd = ThreadLocalRandom.current();
        final int[] sizes = new int[]{1, 5, 10, 50};
        long totalElapsed = 0;
        long totalItems = 0;
        final ItemConsumer consumer = new ItemConsumer();
        final Stopwatch timer = Stopwatch.createStarted();
        while (!Thread.currentThread().isInterrupted()) {
            final Stopwatch sw = Stopwatch.createStarted();
            final int index = rnd.nextInt(0, sizes.length);
            final int size = sizes[index];
            for (int i = 0; i < size; i++) {
                final int keyIndex = rnd.nextInt(0, keyList.size());
                final Long key = keyList.get(keyIndex);
                final List<Item> itemList = keyToItems.get(key);
                consumer.consume(itemList);
                totalItems += itemList.size();
            }
            final long elapsed = sw.elapsed(TimeUnit.MICROSECONDS);
            logger.debug("{}: {} mks", size, elapsed);
            totalElapsed += elapsed;

            if (timer.elapsed(TimeUnit.SECONDS) >= 5) {
                logger.info("Speed: {} items/sec", (long) (1_000_000.0 * totalItems / totalElapsed));
                timer.reset().start();
            }
        }

        logger.debug("dump is {}", consumer.getDump());
    }

}
