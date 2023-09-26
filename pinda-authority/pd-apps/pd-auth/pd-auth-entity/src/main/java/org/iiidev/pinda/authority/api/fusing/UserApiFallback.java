package org.iiidev.pinda.authority.api.fusing;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.iiidev.pinda.authority.api.UserApi;
import org.iiidev.pinda.authority.entity.auth.User;
import org.iiidev.pinda.base.Result;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserApiFallback implements UserApi {
    public UserApiFallback() {
    }

    public Map<String, Object> getDataScopeById(Long id) {
        Map<String, Object> map = new HashMap(2);
        map.put("dsType", 5);
        map.put("orgIds", Collections.emptyList());
        return map;
    }

    public Result<List<Long>> findAllUserId() {
        return Result.timeout();
    }

    public Result<User> get(Long id) {
        return Result.timeout();
    }

    public Result<Page<User>> page(Long current, Long size, Long orgId, Long stationId, String name, String account, String mobile) {
        return Result.timeout();
    }

    public Result<List<User>> list(List<Long> ids, Long stationId, String name, Long orgId) {
        return Result.timeout();
    }
}
