package org.iiidev.pinda.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import org.iiidev.pinda.gateway.api.ResourceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 鉴权处理过滤器
 */
@Component
@Slf4j
public class AccessFilter extends BaseFilter {
    @Autowired
    private ResourceApi resourceApi;

    @Autowired
    private CacheChannel cacheChannel;
}
