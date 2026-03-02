package com.manosgrigorakis.logisticsplatform.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class EntityChangeTracker {
    private EntityChangeTracker() {}

    /**
     * Compares a specific field between two entities,
     * and records it only if two fields are not equal.
     * @param changes The {@link Map} where the changes will be stored
     * @param key The field name used to
     * @param getter A function used to extract the value of the fields from the entity
     * @param oldEntity The original state of the entity
     * @param updatedEntity The updated state of the entity
     * @param <T> The entity type
     * @param <V> The field type
     */
    public static  <T, V> void trackFieldChange (
            Map<String, Object> changes, String key,
            Function<T, V> getter,
            T oldEntity, T updatedEntity
    ) {

        V oldValue = getter.apply(oldEntity);
        V updatedValue = getter.apply(updatedEntity);

        if(!Objects.equals(oldValue, updatedValue)) {
            Map<String, Object> diff = new HashMap<>();
            diff.put("old", oldValue);
            diff.put("updated", updatedValue);
            changes.put(key, diff);
        }
    }
}
