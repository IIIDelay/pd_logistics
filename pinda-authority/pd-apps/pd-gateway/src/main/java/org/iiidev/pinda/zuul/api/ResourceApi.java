package org.iiidev.pinda.zuul.api;

import org.iiidev.pinda.authority.dto.auth.ResourceQueryDTO;
import org.iiidev.pinda.authority.entity.auth.Resource;
import org.iiidev.pinda.base.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "${pinda.feign.authority-server:pd-auth-server}",
        fallback = ResourceApiFallback.class)
public interface ResourceApi {
    //获取所有需要鉴权的资源
    @GetMapping("/resource/list")
    public Result<List> list();

    //查询当前登录用户拥有的资源权限
    @GetMapping("/resource")
    public Result<List<Resource>> visible(ResourceQueryDTO resource);
}
