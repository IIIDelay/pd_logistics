/*
 * Copyright (c) 2023. 版权归III_Delay所有
 */

package org.iiidev.pinda.utils;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.iiidev.pinda.constant.MatchType;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
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
     * toMap
     *
     * @param inCollection inCollection
     * @param keyFunc      keyFunc
     * @return Map<K, IN>
     */
    public static <IN, K> Map<K, IN> toMap(Collection<IN> inCollection, Predicate<IN> filter, Function<IN, K> keyFunc) {
        return toMap(inCollection, filter, keyFunc, Function.identity(), HashMap::new, true);
    }

    /**
     * toMap
     *
     * @param inCollection inCollection
     * @param keyFunc      keyFunc
     * @return Map<K, IN>
     */
    public static <IN, K> Map<K, IN> toMap(Collection<IN> inCollection, Function<IN, K> keyFunc) {
        return toMap(inCollection, keyFunc, null, Function.identity());
    }

    public static <IN, K, OUT> Map<K, OUT> toMap(Collection<IN> inCollection, Function<IN, K> keyFunc, Predicate<IN> filter, Function<IN, OUT> valFunc) {
        return toMap(inCollection, filter, keyFunc, valFunc, HashMap::new, true);
    }

    /**
     * toMap
     *
     * @param inCollection inCollection
     * @param keyFunc      keyFunc
     * @param isCover      isCover
     * @return Map<K, IN>
     */
    public static <IN, K, OUT> Map<K, OUT> toMap(Collection<IN> inCollection, Predicate<IN> filter, Function<IN, K> keyFunc,
                                                 Function<IN, OUT> valFunc, Supplier<Map<K, OUT>> supMapType, boolean isCover) {
        if (CollectionUtils.isEmpty(inCollection)) {
            return Collections.emptyMap();
        }

        Map<K, OUT> outMap = Optional.ofNullable(supMapType).map(Supplier::get).orElse(Maps.newHashMap());
        inCollection.stream()
            .filter(in -> Optional.ofNullable(filter)
                .map(condition -> in != null && condition.test(in))
                .orElse(in != null))
            .forEach(in -> {
                if (isCover) {
                    outMap.put(keyFunc.apply(in), valFunc.apply(in));
                } else {
                    outMap.putIfAbsent(keyFunc.apply(in), valFunc.apply(in));
                }
            });
        return outMap;
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
            .map(condition -> inList.stream()
                .filter(in -> in != null && condition.test(in))
                .map(mapping)
                .collect(Collectors.toList()))
            .orElse(inList.stream().filter(Objects::nonNull).map(mapping).collect(Collectors.toList()));
    }

    public static <IN, OUT> List<OUT> toList(List<IN> inList, Function<IN, OUT> mapping) {
        if (CollectionUtils.isEmpty(inList)) {
            return Collections.emptyList();
        }
        return inList.stream()
            .filter(Objects::nonNull)
            .map(mapping)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * matchAny 集合任意元素匹配
     *
     * @param inCollection inCollection
     * @param anyEle       anyEle
     * @param matchType    matchType
     * @return boolean
     */
    public static <IN> boolean matchAny(Collection<IN> inCollection, IN anyEle, MatchType matchType) {
        if (CollectionUtils.isEmpty(inCollection) || anyEle == null) {
            return false;
        }
        boolean resultBool = false;
        switch (matchType) {
            case FULL: {
                resultBool = inCollection.stream().anyMatch(in -> Objects.equals(in, anyEle));
                break;
            }
            case PREFIX: {
                resultBool = inCollection.stream().anyMatch(in -> {
                    if (in instanceof String) {
                        return StringUtils.startsWith((CharSequence) in, (CharSequence) anyEle);
                    } else {
                        return StringUtils.startsWith(String.valueOf(in), String.valueOf(anyEle));
                    }
                });
                break;
            }
            case SUBFIX: {
                resultBool = inCollection.stream().anyMatch(in -> {
                    if (in instanceof String) {
                        return StringUtils.endsWith((CharSequence) in, (CharSequence) anyEle);
                    } else {
                        return StringUtils.endsWith(String.valueOf(in), String.valueOf(anyEle));
                    }
                });
                break;
            }
            case ANY: {
                resultBool = inCollection.stream().anyMatch(in -> {
                    if (in instanceof String) {
                        return StringUtils.contains((CharSequence) in, (CharSequence) anyEle);
                    } else {
                        return StringUtils.contains(String.valueOf(in), String.valueOf(anyEle));
                    }
                });
                break;
            }
        }
        return resultBool;
    }

    public static boolean pathMatch(List<String> ins, String anyPath) {
        if (CollectionUtils.isEmpty(ins) || anyPath == null) {
            return false;
        }
        // 增强uri支持/{id}
        CollectionUtils.addAll(ins, toList(ins, Objects::nonNull, in -> StringUtils.join(in, "/{id}")));

        AntPathMatcher pathMatcher = new AntPathMatcher();
        return ins.stream()
            .filter(Objects::nonNull)
            .anyMatch(in -> pathMatcher.match(in, anyPath));
    }

    private static <IN, OUT> boolean checkNon(IN in, Function<IN, OUT> func) {
        if (in != null && func.apply(in) != null) {
            return true;
        }
        return false;
    }

    private static <IN> BinaryOperator<IN> isCover(boolean flag) {
        if (flag) {
            return (k1, k2) -> k2;
        }
        return (k1, k2) -> k1;
    }

    /**
     * getIf : 获取满足条件的第一个值
     *
     * @param ins        ins
     * @param test       test
     * @param defaultVal defaultVal
     * @return T
     */
    public static <T> T findFirst(Collection<T> ins, Predicate<T> test, T defaultVal) {
        if (CollectionUtils.isEmpty(ins)) {
            return defaultVal;
        }

        return ins.stream().filter(test).findFirst().orElse(defaultVal);
    }

    public static <IN> Consumer<IN> action(BiConsumer<Integer, IN> consumer) {
        AtomicInteger count = new AtomicInteger(0);
        return in -> consumer.accept(count.getAndIncrement(), in);
    }

    public static <IN, OUT> Function<IN, OUT> mapper(BiFunction<Integer, IN, OUT> function) {
        AtomicInteger count = new AtomicInteger(0);
        return in -> function.apply(count.getAndIncrement(), in);
    }

    public static <T> T findFirst(Collection<T> ins, Predicate<T> test) {
        return findFirst(ins, test, null);
    }
}
