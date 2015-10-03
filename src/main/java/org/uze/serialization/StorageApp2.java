package org.uze.serialization;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import net.openhft.chronicle.map.ReadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uze.serialization.org.uze.serialization.model.Item;
import org.uze.serialization.storage.HybridStringStorage;
import org.uze.serialization.storage.ItemView;
import org.uze.serialization.storage.StringStorage;
import org.uze.serialization.storage.UnsafeStringStorage;
import org.uze.serialization.storage.model.*;
import org.uze.serialization.utils.ItemFactory;
import org.uze.serialization.utils.ItemViewConsumer;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public class StorageApp2 implements Closeable {

    private final Logger logger = LogManager.getLogger(getClass());

    //private StringStorage stringStorage = new SimpleStringStorage(2_000_000, 80);

    private StringStorage stringStorage = new UnsafeStringStorage(2_000_000, 160);

    //private StringStorage stringStorage = new HybridStringStorage(5_000_000, 40);

    public static void main(String[] args) throws Exception {
        try (StorageApp2 app = new StorageApp2()) {
            app.run();
        }
    }

    @Override
    public void close() throws IOException {
        if (stringStorage instanceof Closeable) {
            ((Closeable) stringStorage).close();
        }
    }

    private ItemView transform(Item item, StringStorage stringStorage) {
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

    private static List<Item> createList(int count) {
        final List<Item> items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            items.add(ItemFactory.create());
        }
        return items;
    }

    private int fill(ByteArray array, Collection<ItemView> itemViews) {
        final ByteBuffer buffer = ByteBuffer.allocate(256 * 1024);
        buffer.putLong(0); // reserve place for object count
        int counter = 0;
        for (ItemView itemView : itemViews) {
            ItemViewWriter.write(buffer, itemView);
            counter++;
            final int size = buffer.position() / counter;
            if (size > buffer.capacity() - buffer.position()) {
                break;
            }
        }
        buffer.putLong(0, counter);
        buffer.rewind();
        int offset = 0;
        while (buffer.hasRemaining()) {
            array.setByteAt(offset, buffer.get());
            offset++;
        }
        return counter;
    }

    private void run() {
        try {
            logger.info("Starting...");
            final int maxItems = 2_000_000;

            logger.info("Creating map...");
            final ChronicleMap<Long, ItemViewByteArray> keyToItems = ChronicleMapBuilder.of(Long.class, ItemViewByteArray.class)
                    .immutableKeys()
                    .entries(maxItems / 1000)
                    .create();

            final List<Long> keyList = new ArrayList<>(maxItems);
            {
                logger.info("Generating list of {} items...", maxItems);
                final List<Item> items = createList(maxItems);
                logger.info("Transforming...");
                final List<ItemView> itemViews = new ArrayList<>(maxItems);
                for (Item item : items) {
                    itemViews.add(transform(item, stringStorage));
                }


                logger.info("Storing item views...");
                long key = 1;
                int distributed = 0;
                while (distributed < itemViews.size()) {
                    final ItemViewByteArray byteArray = keyToItems.newValueInstance();
                    final int filled = fill(byteArray, itemViews.subList(distributed, itemViews.size()));
                    Preconditions.checkArgument(filled > 0, "No items filled!");
                    distributed += filled;
                    keyToItems.put(key, byteArray);
                    keyList.add(key);
                    key++;
                }
                logger.info("{} items in {} keys", distributed, keyList.size());
            }

            logger.info("Entering main loop");

            final ThreadLocalRandom rnd = ThreadLocalRandom.current();
            final int[] sizes = new int[]{1, 5, 10, 50};
            long totalElapsed = 0;
            long totalItems = 0;
            final ItemViewConsumer consumer = new ItemViewConsumer(stringStorage);
            final Stopwatch timer = Stopwatch.createStarted();
            final ItemViewByteArray valueInstance = keyToItems.newValueInstance();
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(500);

                final Stopwatch sw = Stopwatch.createStarted();
                final int index = rnd.nextInt(0, sizes.length);
                final int size = sizes[index];
                for (int i = 0; i < size; i++) {
                    final int keyIndex = rnd.nextInt(0, keyList.size());
                    final Long key = keyList.get(keyIndex);
                    try (ReadContext<Long, ItemViewByteArray> context = keyToItems.getUsingLocked(key, valueInstance)) {
                        totalItems += consume(valueInstance, consumer);
                    }
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
        } catch (Exception ex) {
            logger.error("Run failed!", ex);
        }
    }

    private int consume(ByteArray array, ItemViewConsumer consumer) {
        final ByteArrayReader reader = new ByteArrayReader(array);

        final int count = (int) reader.readLong();
        int offset = Long.SIZE / Byte.SIZE;
        for (int i = 0; i < count; i++) {
            final ItemView itemView = ItemViewWriter.createView(array, offset);
            offset += ItemViewWriter.ITEM_VIEW_BYTES;

            consumer.consume(itemView);
        }
        return count;
    }
}
