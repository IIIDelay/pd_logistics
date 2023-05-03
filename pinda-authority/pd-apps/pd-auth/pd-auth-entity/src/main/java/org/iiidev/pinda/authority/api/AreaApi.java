

package org.iiidev.pinda.authority.api;

import org.iiidev.pinda.authority.entity.common.Area;
import org.iiidev.pinda.base.Result;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "${pinda.feign.authority-server:pd-auth-server}",
    path = "/area"
)
public interface AreaApi {
    @GetMapping({"/{id}"})
    Result<Area> get(@PathVariable Long id);

    @GetMapping({"/code/{code}"})
    Result<Area> getByCode(@PathVariable String code);

    @GetMapping
    Result<List<Area>> findAll(@RequestParam(value = "parentId",required = false) Long parentId, @RequestParam(value = "ids",required = false) List<Long> ids);
}
