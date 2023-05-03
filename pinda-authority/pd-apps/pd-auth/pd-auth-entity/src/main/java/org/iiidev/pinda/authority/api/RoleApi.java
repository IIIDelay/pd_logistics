

package org.iiidev.pinda.authority.api;

import org.iiidev.pinda.authority.api.hystrix.RoleApiFallback;
import org.iiidev.pinda.authority.dto.auth.RoleDTO;
import org.iiidev.pinda.authority.dto.auth.RoleResourceDTO;
import org.iiidev.pinda.base.Result;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "${pinda.feign.authority-server:pd-auth-server}",
    path = "/role",
    fallback = RoleApiFallback.class
)
public interface RoleApi {
    @GetMapping({"/codes"})
    Result<List<Long>> findUserIdByCode(@RequestParam("codes") String[] codes);

    @RequestMapping(
        value = {"/findRoleByUserId/{id}"},
        method = {RequestMethod.GET}
    )
    Result<List<Long>> findRoleByUserId(@PathVariable("id") Long id);

    @RequestMapping(
        value = {"/findAllRoles"},
        method = {RequestMethod.GET}
    )
    Result<List<RoleResourceDTO>> findAllRoles();

    @GetMapping
    Result<List<RoleDTO>> list(@RequestParam("userId") Long userId);
}
