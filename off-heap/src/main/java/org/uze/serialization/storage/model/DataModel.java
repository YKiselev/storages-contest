package org.uze.serialization.storage.model;

import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Y.Kiselev on 01.10.2015.
 */
public abstract class DataModel<K, V> implements Serializable {

    private static final long serialVersionUID = -7342486182098713211L;

    private final Class<K> keyClass;

    private final Class<V> valueClass;

    public Class<K> getKeyClass() {
        return keyClass;
    }

    public Class<V> getValueClass() {
        return valueClass;
    }

    private static Class<?> getFromType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getFromType(((ParameterizedType) type).getRawType());
        }
        throw new IllegalStateException("Can't get class of " + type);
    }

    protected DataModel() {
        final ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        final Type[] actualTypeArguments = type.getActualTypeArguments();
        Preconditions.checkArgument(actualTypeArguments.length == 2, "Bad number of actual type arguments!");
        keyClass = (Class<K>) getFromType(actualTypeArguments[0]);
        valueClass = (Class<V>) getFromType(actualTypeArguments[1]);
    }
}
