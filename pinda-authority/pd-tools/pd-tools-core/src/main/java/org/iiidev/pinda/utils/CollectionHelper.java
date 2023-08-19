/*
 * Copyright (c) 2023. 版权归III_Delay所有
 */

package org.iiidev.pinda.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * CollectionHelper
 *
 * @Author IIIDelay
 * @Date 2023/6/10 16:18
 **/
public class CollectionHelper {
    /**
     * distinct 指定字段去重
     *
     * @param from      from
     * @param keyMapper keyMapper
     * @return List<T>
     */
    public static <IN, F> List<IN> distinct(Collection<IN> from, Function<IN, F> keyMapper) {
        if (CollectionUtils.isEmpty(from)) {
            return new ArrayList<>();
        }
        return distinct(from, keyMapper, (t1, t2) -> t1);
    }

    /**
     * distinct : 指定字段去重
     *
     * @param from      from
     * @param keyMapper keyMapper
     * @param cover     cover
     * @return List<T>
     */
    public static <IN, F> List<IN> distinct(Collection<IN> from, Function<IN, F> keyMapper, BinaryOperator<IN> cover) {
        if (CollectionUtils.isEmpty(from)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
    }

    /**
     * convertMap
     *
     * @param from          from
     * @param keyFunc       keyFunc
     * @param valueFunc     valueFunc
     * @param mergeFunction mergeFunction
     * @return Map<K, V>
     */
    public static <IN, K, V> Map<K, V> convertMap(Collection<IN> from, Function<IN, K> keyFunc, Function<IN, V> valueFunc, BinaryOperator<V> mergeFunction) {
        if (CollectionUtils.isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new);
    }

    /**
     * convertMap
     *
     * @param from          from
     * @param keyFunc       keyFunc
     * @param valueFunc     valueFunc
     * @param mergeFunction mergeFunction
     * @param supplier      supplier
     * @return Map<K, V>
     */
    public static <IN, K, V> Map<K, V> convertMap(Collection<IN> from, Function<IN, K> keyFunc, Function<IN, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
        if (CollectionUtils.isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction, supplier));
    }

    /**
     * toGroup: 分组
     *
     * @return Map<K, List < IN>>
     */
    public static <IN, K> Map<K, List<IN>> toGroup(Collection<IN> inList, Function<IN, K> keyFunc) {
        if (CollectionUtils.isEmpty(inList)) {
            return Collections.emptyMap();
        }

        return inList.stream()
            .filter(in -> checkNon(in, keyFunc))
            .collect(Collectors.groupingBy(keyFunc, Collectors.toList()));
    }

    /**
     * toFGroup : 分组
     *
     * @param inList       inList
     * @param keyFunc      keyFunc
     * @param vaLFiledFunc vaLFiledFunc
     * @return Map<K, List < F>>
     */
    public static <IN, K, F> Map<K, List<F>> toFGroup(List<IN> inList, Function<IN, K> keyFunc, Function<IN, F> vaLFiledFunc) {
        if (CollectionUtils.isEmpty(inList)) {
            return Collections.emptyMap();
        }
        return inList.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(keyFunc::apply, Collectors.mapping(vaLFiledFunc::apply, Collectors.toList())));
    }

    /**
     * getMaxValue
     *
     * @param from      from
     * @param valueFunc valueFunc
     * @return V
     */
    public static <T, V extends Comparable<? super V>> V getMaxValue(List<T> from, Function<T, V> valueFunc) {
        if (CollectionUtils.isEmpty(from)) {
            return null;
        }
        assert from.size() > 0; // 断言，避免告警
        T t = from.stream().max(Comparator.comparing(valueFunc)).get();
        return valueFunc.apply(t);
    }

    /**
     * getMinValue
     *
     * @param from      from
     * @param valueFunc valueFunc
     * @return V
     */
    public static <T, V extends Comparable<? super V>> V getMinValue(List<T> from, Function<T, V> valueFunc) {
        if (CollectionUtils.isEmpty(from)) {
            return null;
        }
        assert from.size() > 0; // 断言，避免告警
        T t = from.stream().min(Comparator.comparing(valueFunc)).get();
        return valueFunc.apply(t);
    }

    /**
     * getSumValue
     *
     * @param from        from
     * @param valueFunc   valueFunc
     * @param accumulator accumulator
     * @return V
     */
    public static <T, V extends Comparable<? super V>> V getSumValue(List<T> from, Function<T, V> valueFunc, BinaryOperator<V> accumulator) {
        if (CollectionUtils.isEmpty(from)) {
            return null;
        }
        return from.stream().map(valueFunc).reduce(accumulator).get();
    }

    public static <T> void addIfNotNull(Collection<T> coll, T item) {
        if (item == null) {
            return;
        }
        coll.add(item);
    }

    /**
     * toList
     *
     * @param inList  inList
     * @param filter  filter
     * @param mapping mapping
     * @return List<OUT>
     */
    public static <IN, OUT> List<OUT> toList(List<IN> inList, Predicate<IN> filter, Function<IN, OUT> mapping) {
        if (CollectionUtils.isEmpty(inList)) {
            return Collections.emptyList();
        }
        return Optional.ofNullable(filter)
            .map(test -> inList.stream().filter(test).map(mapping).collect(Collectors.toList()))
            .orElse(inList.stream().map(mapping).collect(Collectors.toList()));
    }

    private static <IN, OUT> boolean checkNon(IN in, Function<IN, OUT> func) {
        if (in != null && func.apply(in) != null) {
            return true;
        }
        return false;
    }

}
