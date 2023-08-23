package org.iiidev.pinda.authority.util;

import cn.hutool.core.text.StrPool;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.iiidev.pinda.utils.SpringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
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
}
