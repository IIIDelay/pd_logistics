

package org.iiidev.pinda.authority.api;

import org.iiidev.pinda.authority.api.fusing.AuthorityGeneralApiFallback;
import org.iiidev.pinda.base.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(
    name = "${pinda.feign.authority-server:pd-auth-server}",
    fallback = AuthorityGeneralApiFallback.class
)
public interface AuthorityGeneralApi {
    @GetMapping({"/enums"})
    Result<Map<String, Map<String, String>>> enums();
}
