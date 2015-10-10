package org.uze.storages.coherence;

import com.google.common.base.Stopwatch;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.NamedCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uze.storages.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Y.Kiselev on 09.10.2015.
 */
public class ClientApp {

    private final Logger logger = LogManager.getLogger(getClass());

    private void run() {
        final Cluster cluster = CacheFactory.ensureCluster();

        final NamedCache cache = CacheFactory.getCache("Items");

        logger.info("Cache has {} items", cache.size());

        // noinspection unchecked
        final ArrayList<String> keyList = new ArrayList<>(cache.keySet());

        logger.info("Collected {} keys", keyList.size());

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