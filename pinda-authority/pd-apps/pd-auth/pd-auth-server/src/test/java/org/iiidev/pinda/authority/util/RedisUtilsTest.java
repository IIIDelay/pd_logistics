package org.iiidev.pinda.authority.util;

import org.iiidev.pinda.authority.dto.auth.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RedisUtilsTest {

    private  static UserDTO dto;

    @Test
    void get() {
        /* UserDTO dto1 = new UserDTO();
        dto1.setAccount("xxx");
        RedisUtilsTest.dto = dto1;
        RedisUtilsTest redisUtilsTest = new RedisUtilsTest();
        UserDTO dto2 = RedisUtilsTest.dto;
        System.out.println("dto2 = " + dto2); */
        String name1 = RedisOpt.getValue("name");
        System.out.println("name1 = " + name1);
    }


}