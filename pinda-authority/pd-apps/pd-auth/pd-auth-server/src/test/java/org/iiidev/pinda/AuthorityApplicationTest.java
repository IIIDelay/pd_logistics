package org.iiidev.pinda;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

@Slf4j
class AuthorityApplicationTest {
    @Test
    public void String() {
        String[] strs = {"a"};
        String limit = "=";
        String join = StringUtils.join(strs);
        String limitResult = StringUtils.join(strs, limit);
        System.out.println("join = " + join);
        System.out.println("limitResult = " + limitResult);

    }

    @Test
    void name() {
        try {
            int resout = 1 / 0;
        } catch (RuntimeException e) {
            log.error("失败", e);
            throw new RuntimeException(e);
        }
    }
}