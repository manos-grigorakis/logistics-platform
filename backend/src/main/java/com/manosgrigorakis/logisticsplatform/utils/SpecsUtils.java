package com.manosgrigorakis.logisticsplatform.utils;

import org.springframework.data.jpa.domain.Specification;

import java.util.function.Function;

public class SpecsUtils {
    /**
     * Applies a specification filter if {@code value} is not null
     * @param spec The current specification
     * @param value The filter value to check
     * @param specFn Function that generate the {@link Specification}{@code <T>}
     * @return spec The updated specification
     * @param <T> The type of the entity
     * @param <V> The type of the filter value
     */
    public static <T, V> Specification<T> andIf(Specification<T> spec, V value, Function<V, Specification<T>> specFn) {
        if(value != null) {
            spec = spec.and(specFn.apply(value));
        }

        return spec;
    }
}
