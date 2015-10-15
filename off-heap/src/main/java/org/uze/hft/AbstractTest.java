package org.uze.hft;

import com.google.common.base.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uze.hft.utils.BlackHole;
import org.uze.storages.model.Item;
import org.uze.storages.utils.ItemFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public abstract class AbstractTest {

    public static final int MAX_ITEMS_TO_READ_PER_PASS = 100_000;

    private final Logger logger = LogManager.getLogger(getClass());

    private final int maxKeysToReadPerPass;

    public Logger getLogger() {
        return logger;
    }

    protected int getMaxItems() {
        return ItemFactory.MAX_ITEMS;
    }

    public AbstractTest(int maxKeysToReadPerPass) {
        this.maxKeysToReadPerPass = maxKeysToReadPerPass;
    }

    /**
     * @param items the input list of items
     */
    protected abstract BlackHole init(List<Item> items, boolean usePartialConsumer);

    /**
     * @param keysToRead the number of entries to read per pass
     * @return the number of items read (which may be more than number of entries for many items per entry setups)
     */
    protected abstract long pass(int keysToRead);

    /**
     * The main method to run from subclass
     *
     * @throws Exception
     */
    protected final void run() throws Exception {
        final boolean usePartialConsumer = Boolean.getBoolean("usePartialConsumer");
        logger.info("Using {} consumer", usePartialConsumer ? "partial" : "full");

        final int maxItems = getMaxItems();

        logger.info("Generating list of {} items...", maxItems);

        final BlackHole blackHole = init(ItemFactory.createList(maxItems), usePartialConsumer);

        logger.info("Entering main loop");

        final ThreadLocalRandom rnd = ThreadLocalRandom.current();
        long totalElapsed = 0;
        long totalItems = 0;
        final Stopwatch timer = Stopwatch.createStarted();
        while (!Thread.currentThread().isInterrupted()) {
            final Stopwatch sw = Stopwatch.createStarted();
            final int size = rnd.nextInt(1, maxKeysToReadPerPass);
            totalItems += pass(size);
            final long elapsed = sw.elapsed(TimeUnit.MICROSECONDS);
            logger.debug("{}: {} mks", size, elapsed);
            totalElapsed += elapsed;

            if (timer.elapsed(TimeUnit.SECONDS) >= 5) {
                logger.info("Speed: {} items/sec", (long) (1_000_000.0 * totalItems / totalElapsed));
                timer.reset().start();
            }
        }

        // force consume usage
        logger.debug("Black hole value is {}", blackHole.getValue());
    }

}
