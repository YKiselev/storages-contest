package org.uze.storages.coherence;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.NamedCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.uze.storages.model.Item;
import org.uze.storages.utils.ItemFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Y.Kiselev on 09.10.2015.
 */
public class ServerApp {

    private final Logger logger = LogManager.getLogger(getClass());

    private void run() {
        final Cluster cluster = CacheFactory.ensureCluster();

        logger.info("Generating items...");
        final List<Item> items = ItemFactory.createList();

        logger.info("Storing...");
        final NamedCache cache = CacheFactory.getCache("Items");
        for (Item item : items) {
            cache.put(item.getId(), item);
        }

        logger.info("{} items stored", items.size());

        final Item item = items.get(0);

        final Object o = cache.get(item.getId());
        logger.info("Got {} for key {}", o, item.getId());

        // noinspection unchecked
        final Set<Map.Entry<?,?>> set = cache.entrySet();
        for (Map.Entry<?, ?> entry : set) {
            final Object key = entry.getKey();
            logger.info("First entry: key={} ({}), {}", key, key.getClass(), entry.getValue());
            break;
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        new ServerApp().run();
    }
}
