

package org.iiidev.pinda.authority.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.iiidev.pinda.authority.api.fusing.UserApiFallback;
import org.iiidev.pinda.authority.entity.auth.User;
import org.iiidev.pinda.base.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(
    name = "${pinda.feign.authority-server:pd-auth-server}",
    fallback = UserApiFallback.class,
    path = "/user"
)
public interface UserApi {
    @RequestMapping(
        value = {"/ds/{id}"},
        method = {RequestMethod.GET}
    )
    Map<String, Object> getDataScopeById(@PathVariable("id") Long id);

    @RequestMapping(
        value = {"/find"},
        method = {RequestMethod.GET}
    )
    Result<List<Long>> findAllUserId();

    @GetMapping({"/{id}"})
    Result<User> get(@PathVariable Long id);

    @GetMapping({"/page"})
    Result<Page<User>> page(@RequestParam("current") Long current, @RequestParam("size") Long size, @RequestParam(name = "orgId",required = false) Long orgId, @RequestParam(name = "stationId",required = false) Long stationId, @RequestParam(name = "name",required = false) String name, @RequestParam(name = "account",required = false) String account, @RequestParam(name = "mobile",required = false) String mobile);

    @GetMapping({""})
    Result<List<User>> list(@RequestParam(name = "ids",required = false) List<Long> ids, @RequestParam(name = "stationId",required = false) Long stationId, @RequestParam(name = "name",required = false) String name, @RequestParam(name = "orgId",required = false) Long orgId);
}
