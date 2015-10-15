package org.uze.hft.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.uze.storages.model.Item;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public abstract class ItemMixIn extends Item {

    @JsonCreator
    public ItemMixIn(@JsonProperty("id") String id, @JsonProperty("name") String name, @JsonProperty("book") String book,
                     @JsonProperty("productType") String productType, @JsonProperty("type") String type,
                     @JsonProperty("status") String status, @JsonProperty("timestamp") long timestamp,
                     @JsonProperty("value") double value, @JsonProperty("version") int version) {
        super(id, name, book, productType, type, status, timestamp, value, version);
    }
}
