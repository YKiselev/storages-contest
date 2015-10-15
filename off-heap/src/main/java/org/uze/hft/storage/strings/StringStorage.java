package org.uze.hft.storage.strings;

import org.apache.logging.log4j.Logger;

/**
 * Created by Y.Kiselev on 03.10.2015.
 */
public interface StringStorage {

    long put(String value);

    String get(long id);

    void printStat(Logger logger);
}
