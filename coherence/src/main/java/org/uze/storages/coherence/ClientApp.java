package org.uze.storages.coherence;

import com.google.common.base.Stopwatch;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.DistributedCacheService;
import com.tangosol.net.NamedCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uze.storages.model.Item;
import org.uze.storages.utils.ItemFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Y.Kiselev on 09.10.2015.
 */
public class ClientApp {

    private final Logger logger = LogManager.getLogger(getClass());

    private void run() {
//        final DistributedCacheService dcs = DistributedCacheService.class.cast(CacheFactory.getService("DistributedBinaryCache"));
//        Objects.requireNonNull(dcs, "dcs");
//        logger.info("LocalStorageEnabled? {}", dcs.isLocalStorageEnabled());

        final Cluster cluster = CacheFactory.ensureCluster();

        final NamedCache cache = CacheFactory.getCache("Items");

        final ArrayList<String> keyList;

        if (cache.isEmpty()) {
            logger.info("Generating items...");
            final List<Item> items = ItemFactory.createList();

            logger.info("Building map...");
            keyList = new ArrayList<>(items.size());
            final Map<String, Item> map = new HashMap<>(items.size());
            for (Item item : items) {
                keyList.add(item.getId());
                map.put(item.getId(), item);
            }

            logger.info("Storing map...");

            cache.putAll(map);

            logger.info("{} items stored", items.size());
        } else {
            keyList = new ArrayList<>(cache.keySet());
            logger.info("Cache contains {} items", keyList.size());
        }

        final ThreadLocalRandom rnd = ThreadLocalRandom.current();
        final int[] sizes = new int[]{10, 5_000, 10_000, 50_000, 100_000};
        long totalElapsed = 0;
        long totalItems = 0;
        final ItemConsumer consumer = new ItemConsumer();
        final Stopwatch timer = Stopwatch.createStarted();
        while (!Thread.currentThread().isInterrupted()) {
            final Stopwatch sw = Stopwatch.createStarted();
            final int index = rnd.nextInt(0, sizes.length);
            final int size = sizes[index];
            final List<String> keys = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                final int keyIndex = rnd.nextInt(0, keyList.size());
                final String key = keyList.get(keyIndex);
                keys.add(key);
            }
            // noinspection unchecked
            final Map<String, Item> itemMap = cache.getAll(keys);
            for (Map.Entry<String, Item> entry : itemMap.entrySet()) {
                consumer.consume(entry.getValue());
                totalItems++;
            }

            final long elapsed = sw.elapsed(TimeUnit.MICROSECONDS);
            logger.debug("{}: {} mks", size, elapsed);
            totalElapsed += elapsed;

            if (timer.elapsed(TimeUnit.SECONDS) >= 5) {
                logger.info("Speed: {} items/sec", (long) (1_000_000.0 * totalItems / totalElapsed));
                timer.reset().start();
            }
        }
    }

    public static void main(String[] args) {
        new ClientApp().run();
    }
}