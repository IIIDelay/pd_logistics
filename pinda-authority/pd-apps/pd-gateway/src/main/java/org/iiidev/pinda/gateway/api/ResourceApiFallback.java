package org.iiidev.pinda.gateway.api;

import org.iiidev.pinda.authority.dto.auth.ResourceQueryDTO;
import org.iiidev.pinda.authority.entity.auth.Resource;
import org.iiidev.pinda.base.Result;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 资源API熔断
 */
@Component
public class ResourceApiFallback implements ResourceApi {
    @Override
    public Result<List> list() {
        return null;
    }

    @Override
    public Result<List<Resource>> visible(ResourceQueryDTO resource) {
        return null;
    }
}
