package org.uze.serialization.storage.model;

import org.uze.serialization.storage.ItemView;

import java.util.Collection;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public class ItemViewDataModel extends DataModel<String, Collection<ItemView>> {

    public static final ItemViewDataModel INSTANCE = new ItemViewDataModel();
}
