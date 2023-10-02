package org.iiidev.pinda;

import org.iiidev.pinda.common.constant.UniqueIDEnum;
import org.iiidev.pinda.common.utils.RedisHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

/**
 * RedisHelperTest
 *
 * @Author IIIDelay
 * @Date 2023/10/1 17:02
 **/
@SpringBootTest
public class RedisHelperTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void generateUniqueId() {
        String id = RedisHelper.generateUniqueId(UniqueIDEnum.TS_ORDER);
        System.out.println("id = " + id);
    }

    @Test
    public void threadTest() {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        stringValueOperations.set("namexx","zhangsanyy", Duration.ofSeconds(60));
    }
}