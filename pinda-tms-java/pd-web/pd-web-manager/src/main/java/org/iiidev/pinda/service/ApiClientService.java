package org.iiidev.pinda.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.iiidev.pinda.authority.api.AreaApi;
import org.iiidev.pinda.authority.api.LogApi;
import org.iiidev.pinda.authority.api.OrgApi;
import org.iiidev.pinda.authority.api.RoleApi;
import org.iiidev.pinda.authority.api.UserApi;
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