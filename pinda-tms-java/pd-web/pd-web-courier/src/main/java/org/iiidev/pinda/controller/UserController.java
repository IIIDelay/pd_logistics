package org.iiidev.pinda.controller;


import org.iiidev.pinda.DTO.UserProfileDTO;
import org.iiidev.pinda.DTO.user.CourierScopeDto;
import org.iiidev.pinda.authority.api.AreaApi;
import org.iiidev.pinda.authority.api.OrgApi;
import org.iiidev.pinda.authority.api.UserApi;
import org.iiidev.pinda.authority.entity.auth.User;
import org.iiidev.pinda.authority.entity.common.Area;
import org.iiidev.pinda.authority.entity.core.Org;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.common.context.RequestContext;
import org.iiidev.pinda.common.utils.RespResult;
import org.iiidev.pinda.feign.user.CourierScopeFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 运单表 前端控制器
 * </p>
 *
 * @author diesel
 * @since 2020-03-19
 */
@Slf4j
@Api(tags = "用户管理")
@Controller
@RequestMapping("user")
public class UserController {

    private final UserApi userApi;

    private final OrgApi orgApi;

    private final CourierScopeFeign courierScopeFeign;

    private final AreaApi areaApi;

    public UserController(UserApi userApi, OrgApi orgApi, CourierScopeFeign courierScopeFeign, AreaApi areaApi) {
        this.userApi = userApi;
        this.orgApi = orgApi;
        this.courierScopeFeign = courierScopeFeign;
        this.areaApi = areaApi;
    }

    @ApiOperation(value = "我的信息")
    @ResponseBody
    @GetMapping("profile")
    public RespResult profile() {

        //  快递员id  并放入参数
        String courierId = RequestContext.getUserId();
        // 基本信息
        Result<User> userResult = userApi.get(Long.valueOf(courierId));
        User user = userResult.getData();
        // 所属机构
        Result<Org> orgResult = orgApi.get(user.getOrgId());
        Org org = orgResult.getData();

        //
        List<CourierScopeDto> courierScopeDtos = courierScopeFeign.findAllCourierScope(null, user.getId().toString());
        List<Long> areaIds = courierScopeDtos.stream().map(item -> Long.valueOf(item.getAreaId())).collect(Collectors.toList());
        Result<List<Area>> areaDtosResult = areaApi.findAll(null, areaIds);
        List<Area> areas = areaDtosResult.getData();
        return RespResult.ok().put("data", UserProfileDTO.builder()
                .id(user.getId().toString())
                .avatar(user.getAvatar())
                .name(user.getName())
                .phone(user.getMobile())
                .manager(org.getName())
                .areas(areas)
                .build());
    }
}
