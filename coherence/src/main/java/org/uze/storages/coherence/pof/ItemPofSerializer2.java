package org.uze.storages.coherence.pof;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import org.uze.storages.model.Item;

import java.io.IOException;

/**
 * Created by Y.Kiselev on 09.10.2015.
 */
public class ItemPofSerializer2 implements PofSerializer {

    private static final int pof_id = 1;

    private static final int pof_name = 2;

    private static final int pof_book = 3;

    private static final int pof_productType = 4;

    private static final int pof_type = 5;

    private static final int pof_status = 5;

    private static final int pof_timestamp = 6;

    private static final int pof_value = 7;

    private static final int pof_version = 8;

    @Override
    public void serialize(PofWriter writer, Object o) throws IOException {
        final Item obj = (Item)o;

        writer.writeString(pof_id, obj.getId());
        writer.writeRemainder(null);
    }

    @Override
    public Object deserialize(PofReader reader) throws IOException {
        final boolean book = reader.readBoolean(pof_book);

        return null;
    }
}
