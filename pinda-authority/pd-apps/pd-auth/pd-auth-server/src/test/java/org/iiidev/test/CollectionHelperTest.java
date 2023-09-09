package org.iiidev.test;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.ToString;
import org.iiidev.pinda.authority.entity.auth.UserRole;
import org.iiidev.pinda.utils.CollectionHelper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * CollectionHelperTest
 *
 * @Author IIIDelay
 * @Date 2023/9/9 23:49
 **/
public class CollectionHelperTest {
    @Test
    public void testToMap() {
        UserRole userRole = new UserRole();
        userRole.setRoleId(1000L);
        userRole.setCreateUser(1001L);

        UserRole userRole1 = new UserRole();
        userRole1.setRoleId(2000L);
        userRole1.setCreateUser(2001L);

        UserRole userRole2 = new UserRole();
        userRole2.setRoleId(3000L);
        userRole2.setCreateUser(null);

        UserRole userRole3 = new UserRole();
        userRole3.setRoleId(4000L);
        userRole3.setCreateUser(4001L);

        List<UserRole> list = Lists.newArrayList(userRole, userRole1, userRole2, userRole3);

        Map<Long, Long> map = CollectionHelper.toMap(list, UserRole::getRoleId, null, UserRole::getCreateUser);

        System.out.println("map = " + map);

    }

    @Test
    public void name() {
        C c = new C();
        c.setName("xx");
        c.setAge(12);
        System.out.println("c = " + c);
    }
}

@Data
class U {
    private String name;
}

@Data
@ToString(callSuper = true)
class C extends U{
    private Integer age;
}
