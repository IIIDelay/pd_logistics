package org.iiidev.pinda.authority.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * ApiClientService
 *
 * @Author IIIDelay
 * @Date 2023/9/26 23:45
 **/
@Component
@RequiredArgsConstructor
@Getter
public class ApiClientService {
    private final AreaApi areaApi;
    private final LogApi logApi;
    private final OrgApi orgApi;
    private final RoleApi roleApi;
    private final UserApi userApi;
}
