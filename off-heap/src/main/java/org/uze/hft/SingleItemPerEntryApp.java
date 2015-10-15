package org.uze.hft;

import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.uze.hft.marshallers.ItemBytesMarshaller;
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
public class SingleItemPerEntryApp extends AbstractTest {

    private ChronicleMap<String, Item> keyToItems;

    private List<String> keyList;

    private DefaultItemConsumer consumer;

    public static void main(String[] args) throws Exception {
        new SingleItemPerEntryApp().run();
    }

    public SingleItemPerEntryApp() {
        super(MAX_ITEMS_TO_READ_PER_PASS);
    }

    @Override
    protected BlackHole init(List<Item> items, boolean usePartialConsumer) {
        getLogger().info("Creating map...");
        keyToItems = ChronicleMapBuilder.of(String.class, Item.class)
                .immutableKeys()
                .averageKeySize(ItemFactory.averageKeySize())
                .averageValueSize(ItemFactory.averageValueSize())
                .valueMarshaller(ItemBytesMarshaller.INSTANCE)
                .entries(items.size())
                .create();

        keyList = new ArrayList<>(items.size());
        getLogger().info("Storing items...");
        for (Item item : items) {
            final String id = item.getId();
            keyList.add(id);
            keyToItems.put(id, item);
        }

        return consumer = usePartialConsumer ? new PartialItemConsumer() : new DefaultItemConsumer();
    }

    @Override
    protected long pass(int keysToRead) {
        for (int i = 0; i < keysToRead; i++) {
            final int keyIndex = ThreadLocalRandom.current().nextInt(0, keyList.size());
            final String key = keyList.get(keyIndex);
            final Item item = keyToItems.get(key);
            consumer.consume(item);
        }
        return keysToRead;
    }
}
