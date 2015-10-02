package org.uze.serialization.org.uze.serialization.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public class Item implements Serializable {

    private static final long serialVersionUID = -5970518734921740239L;

    private final String id;

    private final String name;

    private final String book;

    private final String productType;

    private final String type;

    private final String status;

    private final long timestamp;

    private final double value;

    private final int version;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBook() {
        return book;
    }

    public String getProductType() {
        return productType;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getValue() {
        return value;
    }

    public int getVersion() {
        return version;
    }

    public Item(String id, String name, String book, String productType, String type, String status, long timestamp, double value, int version) {
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return new EqualsBuilder()
                .append(timestamp, item.timestamp)
                .append(value, item.value)
                .append(version, item.version)
                .append(id, item.id)
                .append(name, item.name)
                .append(book, item.book)
                .append(productType, item.productType)
                .append(type, item.type)
                .append(status, item.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(book)
                .append(productType)
                .append(type)
                .append(status)
                .append(timestamp)
                .append(value)
                .append(version)
                .toHashCode();
    }
}
