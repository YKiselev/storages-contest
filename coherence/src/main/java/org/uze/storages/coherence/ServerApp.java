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

/**
 * Created by Y.Kiselev on 09.10.2015.
 */
public class ServerApp {

    private final Logger logger = LogManager.getLogger(getClass());

    private void run() {
        //final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/app.xml");

        final Cluster cluster = CacheFactory.ensureCluster();

        final NamedCache cache = CacheFactory.getCache("Items");

        logger.info("Generating items...");
        final List<Item> items = ItemFactory.createList();
        logger.info("Transforming...");

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
