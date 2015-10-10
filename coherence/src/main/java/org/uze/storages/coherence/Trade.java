package org.uze.storages.coherence;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.uze.codegen.pof.serializer.model.PofSerializable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
@PofSerializable(typeId = 10000, constructorArgs = {"id", "name", "book",
        "productType", "type", "status", "timestamp", "value", "version", "notional"})
public class Trade extends TradeBase implements Serializable {

    private static final long serialVersionUID = -5970518734921740239L;

    private final String productType;

    private final long timestamp;

    private final double value;

    private final int version;

    private String description;

    private Double notional;

    private Date createdAt;

    public String getProductType() {
        return productType;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getNotional() {
        return notional;
    }

    public void setNotional(Double notional) {
        this.notional = notional;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Trade(String id, String name, String book, String productType, String type, String status,
                 long timestamp, double value, int version, Double notional) {
        super(status, id, name, type, book);
        this.productType = productType;
        this.timestamp = timestamp;
        this.value = value;
        this.version = version;
        this.notional = notional;
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

        Trade item = (Trade) o;

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
