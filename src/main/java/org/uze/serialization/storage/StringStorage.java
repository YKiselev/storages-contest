package org.uze.serialization.storage;

/**
 * Created by Y.Kiselev on 03.10.2015.
 */
public interface StringStorage {

    long put(String value);

    String get(long id);
}
