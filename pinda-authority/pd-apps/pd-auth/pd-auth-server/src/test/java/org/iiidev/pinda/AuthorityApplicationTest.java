package org.iiidev.pinda;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class AuthorityApplicationTest {
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