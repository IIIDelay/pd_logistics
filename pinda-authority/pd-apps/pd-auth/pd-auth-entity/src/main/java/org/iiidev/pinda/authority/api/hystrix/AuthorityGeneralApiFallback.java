

package org.iiidev.pinda.authority.api.hystrix;

import org.iiidev.pinda.authority.api.AuthorityGeneralApi;
import org.iiidev.pinda.base.Result;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class AuthorityGeneralApiFallback implements AuthorityGeneralApi {
    public AuthorityGeneralApiFallback() {
    }

    public Result<Map<String, Map<String, String>>> enums() {
        return Result.timeout();
    }
}
