package org.uze.hft;

import com.google.common.collect.Lists;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.uze.hft.marshallers.ItemBytesMarshaller;
import org.uze.hft.marshallers.ListBytesMarshaller;
import org.uze.hft.utils.BlackHole;
import org.uze.hft.utils.DefaultItemConsumer;
import org.uze.hft.utils.PartialItemConsumer;
import org.uze.storages.model.Item;
import org.uze.storages.utils.ItemFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public class ManyItemsPerEntryApp extends AbstractTest {

    public static final int ITEMS_PER_ENTRY = 300;
    private ChronicleMap<Long, List<Item>> keyToItems;
    private List<Long> keyList;
    private DefaultItemConsumer consumer;

    public static void main(String[] args) throws Exception {
        new ManyItemsPerEntryApp().run();
    }

    public ManyItemsPerEntryApp() {
        super(MAX_ITEMS_TO_READ_PER_PASS / ITEMS_PER_ENTRY);
    }

    @Override
    protected BlackHole init(List<Item> items, boolean usePartialConsumer) {
        getLogger().info("Creating map...");
        // noinspection unchecked
        keyToItems = ChronicleMapBuilder.of(Long.class, List.class)
                .immutableKeys()
                .averageValueSize(ItemFactory.averageValueSize() * ITEMS_PER_ENTRY)
                .valueMarshaller(new ListBytesMarshaller(ItemBytesMarshaller.INSTANCE))
                .entries(items.size() / ITEMS_PER_ENTRY)
                .create();

        getLogger().info("Storing items...");
        keyList = new ArrayList<>(items.size());
        long key = 1;
        for (List<Item> entryList : Lists.partition(items, ITEMS_PER_ENTRY)) {
            keyList.add(key);
            keyToItems.put(key, entryList);
        }
        getLogger().info("{} items in {} keys ({} items per entry)", items.size(), keyList.size(),
                items.size() / keyList.size());

        return consumer = usePartialConsumer ? new PartialItemConsumer() : new DefaultItemConsumer();
    }

    @Override
    protected long pass(int keysToRead) {
        long result = 0;
        for (int i = 0; i < keysToRead; i++) {
            final int keyIndex = ThreadLocalRandom.current().nextInt(0, keyList.size());
            final Long key = keyList.get(keyIndex);
            final List<Item> itemList = keyToItems.get(key);
            consumer.consume(itemList);
            result += itemList.size();
        }
        return result;
    }
}
