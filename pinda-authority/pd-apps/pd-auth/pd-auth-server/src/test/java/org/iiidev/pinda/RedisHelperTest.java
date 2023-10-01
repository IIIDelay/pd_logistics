package org.iiidev.pinda;

import org.iiidev.pinda.common.constant.UniqueIDEnum;
import org.iiidev.pinda.common.utils.RedisHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * RedisHelperTest
 *
 * @Author IIIDelay
 * @Date 2023/10/1 17:02
 **/
@SpringBootTest
public class RedisHelperTest {

    @Test
    public void generateUniqueId() {
        String id = RedisHelper.generateUniqueId(UniqueIDEnum.TS_ORDER);
        System.out.println("id = " + id);
    }

    @Test
    public void threadTest() {

    }
}