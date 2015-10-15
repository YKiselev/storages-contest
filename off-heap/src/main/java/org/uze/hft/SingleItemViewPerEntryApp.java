package org.uze.hft;

import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import net.openhft.chronicle.map.ReadContext;
import org.uze.hft.storage.ItemView;
import org.uze.hft.storage.strings.StringStorage;
import org.uze.hft.storage.strings.UnsafeStringStorage;
import org.uze.hft.storage.model.ItemViewImpl;
import org.uze.hft.utils.BlackHole;
import org.uze.hft.utils.DefaultItemViewConsumer;
import org.uze.hft.utils.PartialItemViewConsumer;
import org.uze.storages.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public class SingleItemViewPerEntryApp extends AbstractTest {

    //private StringStorage stringStorage = new SimpleStringStorage(2_000_000, 80);

    private StringStorage stringStorage = new UnsafeStringStorage(2_000_000, 160);

    private ChronicleMap<Long, ItemView> keyToItems;

    private List<Long> keyList;

    private DefaultItemViewConsumer consumer;

    public static void main(String[] args) throws Exception {
        new SingleItemViewPerEntryApp().run();
    }

    public SingleItemViewPerEntryApp() {
        super(MAX_ITEMS_TO_READ_PER_PASS);
    }

    @Override
    protected BlackHole init(List<Item> items, boolean usePartialConsumer) {
        getLogger().info("Creating map...");
        keyToItems = ChronicleMapBuilder.of(Long.class, ItemView.class)
                .immutableKeys()
                .constantValueSizeBySample(ItemViewImpl.SAMPLE)
                .entries(items.size())
                .create();

        keyList = new ArrayList<>(items.size());
        getLogger().info("Transforming...");
        final List<ItemView> itemViews = new ArrayList<>(items.size());
        for (Item item : items) {
            itemViews.add(transform(item, stringStorage));
        }

        getLogger().info("Storing item views...");
        for (ItemView itemView : itemViews) {
            final long id = itemView.getId();
            keyList.add(id);
            keyToItems.put(id, itemView);
        }

        return consumer = usePartialConsumer ? new PartialItemViewConsumer(stringStorage)
                : new DefaultItemViewConsumer(stringStorage);
    }

    @Override
    protected long pass(int keysToRead) {
        final ItemView valueInstance = keyToItems.newValueInstance();

        for (int i = 0; i < keysToRead; i++) {
            final int keyIndex = ThreadLocalRandom.current().nextInt(0, keyList.size());
            final Long key = keyList.get(keyIndex);
            try (ReadContext<Long, ItemView> context = keyToItems.getUsingLocked(key, valueInstance)) {
                consumer.consume(valueInstance);
            }
        }

        return keysToRead;
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
}
