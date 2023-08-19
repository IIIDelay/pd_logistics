package org.iiidev.pinda.authority.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.iiidev.pinda.utils.SpringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@Slf4j
public class RedisOpt {
    private final static StringRedisTemplate REDIS_TEMPLATE = SpringUtils.getBean(StringRedisTemplate.class);
    private final static ValueOperations<String, String> VALUE_OPERATIONS = REDIS_TEMPLATE.opsForValue();

    public static <IN> void save(String key, IN in) {
        VALUE_OPERATIONS.setIfAbsent(key, JSONObject.toJSONString(in));
    }

    public static String getValue(String key) {
        if (StringUtils.isEmpty(key)) {
            return "";
        }
        return VALUE_OPERATIONS.get(key);
    }

    public static void remove(String key) {
        String val = VALUE_OPERATIONS.get(key);
        if (StringUtils.isAllEmpty(key, val)) {
            REDIS_TEMPLATE.delete(key);
        }
    }
}
