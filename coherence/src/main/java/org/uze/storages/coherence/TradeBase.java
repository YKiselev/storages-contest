package org.uze.storages.coherence;

import org.uze.codegen.pof.serializer.model.PofSerializable;

import java.io.Serializable;

/**
 * Created by Y.Kiselev on 10.10.2015.
 */
//@PofSerializable(typeId = 10000, constructorArgs = {"id", "name", "book", "type", "status"})
public class TradeBase implements Serializable {

    private static final long serialVersionUID = 3782759707220833597L;

    protected final String id;

    protected final String name;

    protected final String book;

    protected final String type;

    protected final String status;

    public TradeBase(String status, String id, String name, String type, String book) {
        this.status = status;
        this.id = id;
        this.name = name;
        this.type = type;
        this.book = book;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBook() {
        return book;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }
}
