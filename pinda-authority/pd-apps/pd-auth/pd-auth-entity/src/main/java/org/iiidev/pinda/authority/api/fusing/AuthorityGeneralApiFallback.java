

package org.iiidev.pinda.authority.api.fusing;

import org.iiidev.pinda.authority.api.AuthorityGeneralApi;
import org.iiidev.pinda.base.Result;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthorityGeneralApiFallback implements AuthorityGeneralApi {
    public AuthorityGeneralApiFallback() {
    }

    public Result<Map<String, Map<String, String>>> enums() {
        return Result.timeout();
    }
}
