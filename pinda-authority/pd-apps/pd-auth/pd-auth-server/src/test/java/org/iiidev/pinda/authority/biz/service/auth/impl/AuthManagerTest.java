package org.iiidev.pinda.authority.biz.service.auth.impl;

import org.iiidev.pinda.authority.entity.auth.User;
import org.iiidev.pinda.utils.BeanHelper;
import org.junit.jupiter.api.Test;

/**
 * AuthManagerTest
 *
 * @Author IIIDelay
 * @Date 2023/8/20 1:12
 **/
public class AuthManagerTest {

    @Test
    public void login() {
        User user = new User();
        user.setEmail("qq.com");
        user.setPassword("99867");
        user.setAccount("tencent");
        User user1 = BeanHelper.copyCopier(user, new User(),true);
        System.out.println("user1.getEmail() = " + user1.getEmail());
        System.out.println("user1.getPassword() = " + user1.getPassword());
    }
}