
package org.iiidev.pinda.authority.api.hystrix;

import org.iiidev.pinda.authority.api.RoleApi;
import org.iiidev.pinda.authority.dto.auth.RoleDTO;
import org.iiidev.pinda.authority.dto.auth.RoleResourceDTO;
import org.iiidev.pinda.base.Result;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class RoleApiFallback implements RoleApi {
    public RoleApiFallback() {
    }

    public Result<List<Long>> findUserIdByCode(String[] codes) {
        return Result.timeout();
    }

    public Result<List<Long>> findRoleByUserId(Long id) {
        return Result.timeout();
    }

    public Result<List<RoleResourceDTO>> findAllRoles() {
        return Result.timeout();
    }

    public Result<List<RoleDTO>> list(Long userId) {
        return Result.timeout();
    }
}
