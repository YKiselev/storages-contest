package org.uze.serialization.storage;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public interface ItemView {

    long getId();

    long getName();

    long getBook();

    long getProductType();

    long getType();

    long getStatus();

    long getTimestamp();

    double getValue();

    int getVersion();

    void setId(long id);

    void setName(long name);

    void setBook(long book);

    void setProductType(long productType);

    void setType(long type);

    void setStatus(long status);

    void setTimestamp(long timestamp);

    void setValue(double value);

    void setVersion(int version);

}
