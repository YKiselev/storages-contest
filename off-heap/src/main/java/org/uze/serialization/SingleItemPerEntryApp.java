package org.uze.serialization;

import com.google.common.base.Stopwatch;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uze.serialization.marshallers.ItemBytesMarshaller;
import org.uze.serialization.utils.DefaultItemConsumer;
import org.uze.storages.model.Item;
import org.uze.storages.utils.ItemFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public class SingleItemPerEntryApp {

    private final Logger logger = LogManager.getLogger(getClass());

    public static void main(String[] args) throws Exception {
        new SingleItemPerEntryApp().run();
    }

    private void run() throws Exception {
        logger.info("Starting...");
        final int maxItems = ItemFactory.MAX_ITEMS;

        logger.info("Creating map...");
        final ChronicleMap<String, Item> keyToItems = ChronicleMapBuilder.of(String.class, Item.class)
                .immutableKeys()
                .averageKeySize(ItemFactory.averageKeySize())
                .averageValueSize(ItemFactory.averageValueSize())
                .valueMarshaller(ItemBytesMarshaller.INSTANCE)
                .entries(maxItems)
                .create();

        final List<String> keyList = new ArrayList<>(maxItems);
        {
            logger.info("Generating list of {} items...", maxItems);
            final List<Item> items = ItemFactory.createList(maxItems);

            logger.info("Storing items...");
            for (Item item : items) {
                final String id = item.getId();
                keyList.add(id);
                keyToItems.put(id, item);
            }
        }

        logger.info("Entering main loop");

        final ThreadLocalRandom rnd = ThreadLocalRandom.current();
        final int[] sizes = new int[]{10, 5_000, 10_000, 50_000, 100_000};
        long totalElapsed = 0;
        long totalItems = 0;
        final DefaultItemConsumer consumer = new DefaultItemConsumer();
        final Stopwatch timer = Stopwatch.createStarted();
        while (!Thread.currentThread().isInterrupted()) {
            final Stopwatch sw = Stopwatch.createStarted();
            final int index = rnd.nextInt(0, sizes.length);
            final int size = sizes[index];
            for (int i = 0; i < size; i++) {
                final int keyIndex = rnd.nextInt(0, keyList.size());
                final String key = keyList.get(keyIndex);
                final Item item = keyToItems.get(key);
                consumer.consume(item);
            }
            final long elapsed = sw.elapsed(TimeUnit.MICROSECONDS);
            logger.debug("{}: {} mks", size, elapsed);
            totalItems += size;
            totalElapsed += elapsed;

            if (timer.elapsed(TimeUnit.SECONDS) >= 5) {
                logger.info("Speed: {} items/sec", (long) (1_000_000.0 * totalItems / totalElapsed));
                timer.reset().start();
            }
        }

        logger.debug("dump is {}", consumer.getDump());
    }

}
