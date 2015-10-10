package org.uze.storages.coherence;

import org.uze.codegen.pof.serializer.model.PofSerializable;
import org.uze.storages.model.Item;

/**
 * This class used only to force generation of PofSerializers for classes not annotated with {@link PofSerializable}
 * Created by Y.Kiselev on 10.10.2015.
 */
class PofSerializerTypes {

    @PofSerializable(typeId = 10_002,
            constructorArgs = {"id", "name", "book", "productType", "type", "status", "timestamp",
                    "value", "version"})
    public Item item;
}
