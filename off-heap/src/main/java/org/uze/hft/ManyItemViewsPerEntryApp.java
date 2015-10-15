package org.uze.hft;

import com.google.common.base.Preconditions;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import net.openhft.chronicle.map.ReadContext;
import org.uze.hft.storage.ItemView;
import org.uze.hft.storage.strings.StringStorage;
import org.uze.hft.storage.strings.UnsafeStringStorage;
import org.uze.hft.storage.model.ByteArray;
import org.uze.hft.storage.model.ByteArrayReader;
import org.uze.hft.storage.model.ItemViewByteArray;
import org.uze.hft.storage.model.ItemViewWriter;
import org.uze.hft.utils.BlackHole;
import org.uze.hft.utils.DefaultItemViewConsumer;
import org.uze.hft.utils.PartialItemViewConsumer;
import org.uze.storages.model.Item;
import org.uze.storages.utils.ItemFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public class ManyItemViewsPerEntryApp extends AbstractTest implements Closeable {

    public static final int MAX_ITEMS_PER_ENTRY = 3_000;

    private StringStorage stringStorage = new UnsafeStringStorage(2_000_000, 160);

    //private StringStorage stringStorage = new SimpleStringStorage(2_000_000, 80);

    //private StringStorage stringStorage = new HybridStringStorage(5_000_000, 40);

    private ChronicleMap<Long, ItemViewByteArray> keyToItems;

    private List<Long> keyList;

    private DefaultItemViewConsumer consumer;

    public static void main(String[] args) throws Exception {
        try (ManyItemViewsPerEntryApp app = new ManyItemViewsPerEntryApp()) {
            app.run();
        }
    }

    public ManyItemViewsPerEntryApp() {
        super(MAX_ITEMS_TO_READ_PER_PASS / MAX_ITEMS_PER_ENTRY);
    }

    @Override
    public void close() throws IOException {
        if (stringStorage instanceof Closeable) {
            ((Closeable) stringStorage).close();
        }
    }

    @Override
    protected BlackHole init(List<Item> items, boolean usePartialConsumer) {
        getLogger().info("Creating map...");
        keyToItems = ChronicleMapBuilder.of(Long.class, ItemViewByteArray.class)
                .immutableKeys()
                .entries(items.size() / MAX_ITEMS_PER_ENTRY)
                .create();

        keyList = new ArrayList<>(ItemFactory.MAX_ITEMS);

        getLogger().info("Transforming {} items...", items.size());
        final List<ItemView> itemViews = new ArrayList<>(items.size());
        for (Item item : items) {
            itemViews.add(SingleItemViewPerEntryApp.transform(item, stringStorage));
        }

        getLogger().info("Storing item views...");
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
        getLogger().info("{} items in {} keys ({} items per entry)", distributed, keyList.size(),
                distributed / keyList.size());


        return consumer = usePartialConsumer ? new PartialItemViewConsumer(stringStorage)
                : new DefaultItemViewConsumer(stringStorage);
    }

    @Override
    protected long pass(int keysToRead) {
        final ItemViewByteArray valueInstance = keyToItems.newValueInstance();

        long result = 0;
        for (int i = 0; i < keysToRead; i++) {
            final int keyIndex = ThreadLocalRandom.current().nextInt(0, keyList.size());
            final Long key = keyList.get(keyIndex);
            try (ReadContext<Long, ItemViewByteArray> context = keyToItems.getUsingLocked(key, valueInstance)) {
                result += consume(valueInstance, consumer);
            }
        }

        return result;
    }

    private int fill(ByteArray array, Collection<ItemView> itemViews) {
        final ByteBuffer buffer = ByteBuffer.allocate(ByteArrayReader.LONG_BYTES +
                MAX_ITEMS_PER_ENTRY * ItemViewWriter.ITEM_VIEW_BYTES);
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

    private static int consume(ByteArray array, DefaultItemViewConsumer consumer) {
        final ByteArrayReader reader = new ByteArrayReader(array);

        final int count = (int) reader.readLong();
        // add counter offset
        int offset = Long.SIZE / Byte.SIZE;
        for (int i = 0; i < count; i++) {
            final ItemView itemView = ItemViewWriter.createView(array, offset);
            offset += ItemViewWriter.ITEM_VIEW_BYTES;

            consumer.consume(itemView);
        }
        return count;
    }
}
