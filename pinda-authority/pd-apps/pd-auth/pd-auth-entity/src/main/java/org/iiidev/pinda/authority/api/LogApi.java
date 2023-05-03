

package org.iiidev.pinda.authority.api;

import org.iiidev.pinda.authority.api.hystrix.LogApiHystrix;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.log.entity.OptLogDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
    name = "${pinda.feign.authority-server:pd-auth-server}",
    fallback = LogApiHystrix.class
)
public interface LogApi {
    @RequestMapping(
        value = {"/optLog"},
        method = {RequestMethod.POST}
    )
    Result<OptLogDTO> save(@RequestBody OptLogDTO log);
}
