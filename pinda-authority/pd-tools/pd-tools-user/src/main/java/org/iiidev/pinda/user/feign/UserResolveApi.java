package org.iiidev.pinda.user.feign;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.user.feign.fallback.UserResolveApiFallback;
import org.iiidev.pinda.user.model.SysUser;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
/**
 * 用户操作API
 */
@FeignClient(name = "${pinda.feign.authority-server:pinda-auth-server}", fallbackFactory = UserResolveApiFallback.class)
public interface UserResolveApi {
    /**
     * 根据id 查询用户详情
     */
    @PostMapping(value = "/user/anno/id/{id}")
    Result<SysUser> getById(@PathVariable("id") Long id, @RequestBody UserQuery userQuery);
}