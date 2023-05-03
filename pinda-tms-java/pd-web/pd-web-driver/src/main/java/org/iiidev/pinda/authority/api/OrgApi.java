package org.iiidev.pinda.authority.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.iiidev.pinda.authority.api.hystrix.OrgApiFallback;
import org.iiidev.pinda.authority.dto.core.OrgTreeDTO;
import org.iiidev.pinda.authority.entity.core.Org;
import org.iiidev.pinda.base.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@FeignClient(
        name = "${pinda.feign.authority-server:pd-auth-server}",
        fallback = OrgApiFallback.class,
        url = "http://localhost/8764",
        path = "/org"
)
public interface OrgApi {
    @GetMapping({"/{id}"})
    Result<Org> get(@PathVariable Long id);

    @GetMapping
    Result<List<Org>> list(@RequestParam(name = "orgType",required = false) Integer orgType, @RequestParam(name = "ids",required = false) List<Long> ids, @RequestParam(name = "countyId",required = false) Long countyId, @RequestParam(name = "pid",required = false) Long pid, @RequestParam(name = "pids",required = false) List<Long> pids);

    @GetMapping({"/tree"})
    Result<List<OrgTreeDTO>> tree(@RequestParam(value = "name",required = false) String name, @RequestParam(value = "status",required = false) Boolean status);

    @GetMapping({"/pageLike"})
    Result<Page> pageLike(@RequestParam(value = "size",required = false) Integer size, @RequestParam(value = "current",required = false) Integer current, @RequestParam(value = "keyword",required = false) String keyword, @RequestParam(value = "cityId",required = false) Long cityId, @RequestParam(value = "latitude",required = false) String latitude, @RequestParam(value = "longitude",required = false) String longitude);

    @GetMapping({"/listByCountyIds"})
    Result<List<Org>> listByCountyIds(@RequestParam(name = "orgType",required = false) Integer orgType, @RequestParam(name = "countyIds",required = false) List<Long> countyIds);
}
