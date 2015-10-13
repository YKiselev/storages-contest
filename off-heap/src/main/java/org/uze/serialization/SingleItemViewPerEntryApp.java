package org.uze.serialization;

import com.google.common.base.Stopwatch;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import net.openhft.chronicle.map.ReadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uze.serialization.storage.ItemView;
import org.uze.serialization.storage.StringStorage;
import org.uze.serialization.storage.UnsafeStringStorage;
import org.uze.serialization.storage.model.ItemViewImpl;
import org.uze.serialization.utils.ItemViewConsumer;
import org.uze.storages.model.Item;
import org.uze.storages.utils.ItemFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public class SingleItemViewPerEntryApp {

    private final Logger logger = LogManager.getLogger(getClass());

    //private StringStorage stringStorage = new SimpleStringStorage(2_000_000, 80);

    private StringStorage stringStorage = new UnsafeStringStorage(2_000_000, 160);

    public static void main(String[] args) throws Exception {
        new SingleItemViewPerEntryApp().run();
    }

    public static ItemView transform(Item item, StringStorage stringStorage) {
        return new ItemViewImpl(
                stringStorage.put(item.getId()),
                stringStorage.put(item.getName()),
                stringStorage.put(item.getBook()),
                stringStorage.put(item.getProductType()),
                stringStorage.put(item.getType()),
                stringStorage.put(item.getStatus()),
                item.getTimestamp(),
                item.getValue(),
                item.getVersion()
        );
    }

    private void run() throws Exception {
        logger.info("Starting...");
        final int maxItems = 2_000_000;

        logger.info("Creating map...");
        final ChronicleMap<Long, ItemView> keyToItems = ChronicleMapBuilder.of(Long.class, ItemView.class)
                .immutableKeys()
                .constantValueSizeBySample(ItemViewImpl.SAMPLE)
                .entries(maxItems)
                .create();

        final List<Long> keyList = new ArrayList<>(maxItems);
        {
            logger.info("Generating list of {} items...", maxItems);
            final List<Item> items = ItemFactory.createList(maxItems);
            logger.info("Transforming...");
            final List<ItemView> itemViews = new ArrayList<>(maxItems);
            for (Item item : items) {
                itemViews.add(transform(item, stringStorage));
            }


            logger.info("Storing item views...");
            for (ItemView itemView : itemViews) {
                final long id = itemView.getId();
                keyList.add(id);
                keyToItems.put(id, itemView);
            }
        }

        logger.info("Entering main loop");

        final ThreadLocalRandom rnd = ThreadLocalRandom.current();
        final int[] sizes = new int[]{10, 5_000, 10_000, 50_000, 100_000};
        long totalElapsed = 0;
        long totalItems = 0;
        final ItemViewConsumer consumer = new ItemViewConsumer(stringStorage);
        final Stopwatch timer = Stopwatch.createStarted();
        final ItemView valueInstance = keyToItems.newValueInstance();
        while (!Thread.currentThread().isInterrupted()) {
            final Stopwatch sw = Stopwatch.createStarted();
            final int index = rnd.nextInt(0, sizes.length);
            final int size = sizes[index];
            for (int i = 0; i < size; i++) {
                final int keyIndex = rnd.nextInt(0, keyList.size());
                final Long key = keyList.get(keyIndex);
                try (ReadContext<Long, ItemView> context = keyToItems.getUsingLocked(key, valueInstance)) {
                    consumer.consume(valueInstance);
                }
            }
            final long elapsed = sw.elapsed(TimeUnit.MICROSECONDS);
            logger.debug("{}: {} mks", size, elapsed);
            totalItems += size;
            totalElapsed += elapsed;

            if (timer.elapsed(TimeUnit.SECONDS) >= 5) {
                logger.info("Speed: {} items/sec", (long)(1_000_000.0 * totalItems / totalElapsed));
                timer.reset().start();
            }
        }

        logger.debug("dump is {}", consumer.getDump());
    }
}
