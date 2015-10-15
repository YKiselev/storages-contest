package org.uze.hft.storage.model;

import org.uze.hft.storage.ItemView;

import java.io.Serializable;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public class ItemViewImpl implements ItemView, Serializable {

    private static final long serialVersionUID = 256112213134221188L;

    public static ItemViewImpl SAMPLE = new ItemViewImpl(
            Long.MAX_VALUE,
            Long.MAX_VALUE,
            Long.MAX_VALUE,
            Long.MAX_VALUE,
            Long.MAX_VALUE,
            Long.MAX_VALUE,
            Long.MAX_VALUE,
            Double.MAX_VALUE,
            Integer.MAX_VALUE
    );

    private long id;

    private long name;

    private long book;

    private long productType;

    private long type;

    private long status;

    private long timestamp;

    private double value;

    private int version;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getName() {
        return name;
    }

    @Override
    public long getBook() {
        return book;
    }

    @Override
    public long getProductType() {
        return productType;
    }

    @Override
    public long getType() {
        return type;
    }

    @Override
    public long getStatus() {
        return status;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void setName(long name) {
        this.name = name;
    }

    @Override
    public void setBook(long book) {
        this.book = book;
    }

    @Override
    public void setProductType(long productType) {
        this.productType = productType;
    }

    @Override
    public void setType(long type) {
        this.type = type;
    }

    @Override
    public void setStatus(long status) {
        this.status = status;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    public ItemViewImpl(long id, long name, long book, long productType, long type, long status, long timestamp, double value, int version) {
        this.id = id;
        this.name = name;
        this.book = book;
        this.productType = productType;
        this.type = type;
        this.status = status;
        this.timestamp = timestamp;
        this.value = value;
        this.version = version;
    }
}
