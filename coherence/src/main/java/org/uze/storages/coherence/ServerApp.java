package org.uze.storages.coherence;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.DistributedCacheService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Created by Y.Kiselev on 09.10.2015.
 */
public class ServerApp {

    private final Logger logger = LogManager.getLogger(getClass());

    private void run() {
        final Cluster cluster = CacheFactory.ensureCluster();

        final DistributedCacheService dcs = DistributedCacheService.class.cast(CacheFactory.getService("DistributedBinaryCache"));
        Objects.requireNonNull(dcs, "dcs");
        logger.info("LocalStorageEnabled? {}", dcs.isLocalStorageEnabled());



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
