package org.iiidev.pinda.common.utils;

import cn.hutool.core.text.StrPool;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.iiidev.pinda.common.constant.CacheKey;
import org.iiidev.pinda.common.constant.UniqueIDEnum;
import org.iiidev.pinda.utils.SpringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class RedisHelper {
    private final static StringRedisTemplate REDIS_TEMPLATE = SpringUtils.getBean(StringRedisTemplate.class);
    private final static ValueOperations<String, String> VALUE_OPERATIONS = REDIS_TEMPLATE.opsForValue();

    public static <IN> void save(IN input, String... key) {
        matchSetter(input,
            in -> VALUE_OPERATIONS.set(StringUtils.join(key, StrPool.C_COLON), String.valueOf(input)),
            in -> VALUE_OPERATIONS.set(StringUtils.join(key, StrPool.C_COLON), JSONObject.toJSONString(input)));
    }

    public static <IN> void save(IN input, Duration expir, String... key) {
        matchSetter(input,
            in -> VALUE_OPERATIONS.set(StringUtils.join(key, StrPool.C_COLON), String.valueOf(input), expir),
            in -> VALUE_OPERATIONS.set(StringUtils.join(key, StrPool.C_COLON), JSONObject.toJSONString(in), expir));
    }

    public static <IN> void saveIfAbsent(IN input, String... key) {
        matchSetter(input,
            in -> VALUE_OPERATIONS.set(StringUtils.join(key, StrPool.C_COLON), String.valueOf(input)),
            in -> VALUE_OPERATIONS.setIfAbsent(StringUtils.join(key, StrPool.C_COLON), JSONObject.toJSONString(in)));
    }

    public static String getValue(String... key) {
        if (ArrayUtils.isEmpty(key)) {
            return "";
        }
        return VALUE_OPERATIONS.get(StringUtils.join(key, StrPool.C_COLON));
    }

    public static void remove(String... key) {
        String value = VALUE_OPERATIONS.get(StringUtils.join(key, StrPool.C_COLON));
        if (value != null) {
            REDIS_TEMPLATE.delete(StringUtils.join(key, StrPool.C_COLON));
        }
    }

    private static <IN> void matchSetter(IN in, Consumer<IN> matchRun, Consumer<IN> noMatchRun) {
        if (in == null) {
            return;
        }

        if (ClassUtils.isPrimitiveOrWrapper(in.getClass()) || in instanceof String) {
            matchRun.accept(in);
        } else {
            noMatchRun.accept(in);
        }
    }

    //---------------------redis  incr/decr 相关----------------------------

    /**
     * 设置自增/自减初始值
     *
     * @param key
     * @param value
     * @param timeout
     * @param unit
     */
    private static void setAtomicValue(String key, int value, long timeout, TimeUnit unit) {
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, REDIS_TEMPLATE.getConnectionFactory(), value);
        redisAtomicLong.expire(timeout, unit);
    }

    /**
     * 在redis中自增并获取数据
     *
     * @param key 键
     * @return 自增后的值
     */
    private static long incr(String key) {
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, REDIS_TEMPLATE.getConnectionFactory());
        return redisAtomicLong.incrementAndGet();
    }

    /**
     * 在redis中自增并获取数据，并设置过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    过期时间单位
     * @return 自增后的值
     */
    private static long incr(String key, long timeout, TimeUnit unit) {
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, REDIS_TEMPLATE.getConnectionFactory());
        redisAtomicLong.expire(timeout, unit);
        return redisAtomicLong.incrementAndGet();
    }

    /**
     * 在redis中自增指定步长并获取数据
     *
     * @param key       键
     * @param increment 步长
     * @return 自增后的值
     */
    public long incr(String key, int increment) {
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, REDIS_TEMPLATE.getConnectionFactory());
        return redisAtomicLong.addAndGet(increment);
    }

    /**
     * 在redis中自增指定步长并获取数据，并设置过期时间
     *
     * @param key       键
     * @param increment 步长
     * @param timeout   过期时间
     * @param unit      过期时间单位
     * @return 自增后的值
     */
    public long incr(String key, int increment, long timeout, TimeUnit unit) {
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, REDIS_TEMPLATE.getConnectionFactory());
        redisAtomicLong.expire(timeout, unit);
        return redisAtomicLong.addAndGet(increment);
    }

    /**
     * 生成单号前缀：自定义前缀 + 一定格式的时间
     *
     * @param uniqueIdEnum 自定义的枚举
     * @return 单号前缀
     */
    public static String getFormNoPrefix(UniqueIDEnum uniqueIdEnum) {
        // 格式化时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(uniqueIdEnum.getDatePattern());
        StringBuffer sb = new StringBuffer();
        sb.append(uniqueIdEnum.getPrefix());
        sb.append(formatter.format(LocalDateTime.now()));
        return sb.toString();
    }

    /**
     * 构建流水号缓存Key
     *
     * @param serialPrefix 流水号前缀
     * @return 流水号缓存Key
     */
    public static String getCacheKey(String serialPrefix) {
        return CacheKey.SERIAL_CACHE_PREFIX.concat(serialPrefix);
    }

    /**
     * 补全流水号
     *
     * @param serialPrefix      单号前缀
     * @param incrementalSerial 当天自增流水号
     */
    public static String completionSerial(String serialPrefix, Long incrementalSerial, UniqueIDEnum uniqueIdEnum) {
        StringBuffer sb = new StringBuffer(serialPrefix);
        // 需要补0的长度=流水号长度 -当日自增计数长度
        int length = uniqueIdEnum.getSerialLength() - String.valueOf(incrementalSerial).length();
        // 补零
        for (int i = 0; i < length; i++) {
            sb.append("0");
        }
        // redis当日自增数
        sb.append(incrementalSerial);
        return sb.toString();
    }

    /**
     * 补全随机数
     *
     * @param serialWithPrefix 当前单号
     * @param uniqueIdEnum     单号生成枚举
     */
    public static String completionRandom(String serialWithPrefix, UniqueIDEnum uniqueIdEnum) {
        StringBuffer sb = new StringBuffer(serialWithPrefix);
        // 随机数长度
        int length = uniqueIdEnum.getRandomLength();
        if (length > 0) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < length; i++) {
                // 十以内随机数补全
                sb.append(random.nextInt(10));
            }
        }
        return sb.toString();
    }

    /**
     * 生成分布式Id的方法
     *
     * @param uniqueIdEnum uniqueIdEnum
     * @return String
     */
    public static String generateUniqueId(UniqueIDEnum uniqueIdEnum) {
        // 获得单号前缀 格式 固定前缀 +时间前缀 示例 ：YF20190101
        String prefix = getFormNoPrefix(uniqueIdEnum);
        // 获得缓存key
        String cacheKey = getCacheKey(prefix);
        // 获得当日自增数，并设置时间
        Long incrementalSerial = incr(cacheKey, CacheKey.DEFAULT_CACHE_DAYS, TimeUnit.DAYS);
        // 组合单号并补全流水号
        String serialWithPrefix = completionSerial(prefix, incrementalSerial, uniqueIdEnum);
        // 补全随机数
        return completionRandom(serialWithPrefix, uniqueIdEnum);
    }
}
