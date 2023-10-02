package org.iiidev.pinda.authority.controller.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.authority.biz.service.auth.RoleAuthorityService;
import org.iiidev.pinda.authority.biz.service.auth.RoleOrgService;
import org.iiidev.pinda.authority.biz.service.auth.RoleService;
import org.iiidev.pinda.authority.biz.service.auth.UserRoleService;
import org.iiidev.pinda.authority.dto.auth.RoleAuthoritySaveDTO;
import org.iiidev.pinda.authority.dto.auth.RolePageDTO;
import org.iiidev.pinda.authority.dto.auth.RoleQueryDTO;
import org.iiidev.pinda.authority.dto.auth.RoleSaveDTO;
import org.iiidev.pinda.authority.dto.auth.RoleUpdateDTO;
import org.iiidev.pinda.authority.dto.auth.UserRoleSaveDTO;
import org.iiidev.pinda.authority.entity.auth.Role;
import org.iiidev.pinda.authority.entity.auth.RoleAuthority;
import org.iiidev.pinda.authority.entity.auth.UserRole;
import org.iiidev.pinda.authority.enumeration.auth.AuthorizeType;
import org.iiidev.pinda.base.BaseController;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.base.entity.SuperEntity;
import org.iiidev.pinda.database.mybatis.conditions.Wraps;
import org.iiidev.pinda.database.mybatis.conditions.query.LbqWrapper;
import org.iiidev.pinda.log.annotation.SysLog;
import org.iiidev.pinda.utils.BeanHelper;
import org.iiidev.pinda.utils.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 前端控制器
 * 角色
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/role")
@Api(value = "Role", tags = "角色")
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class RoleController extends BaseController {
    private final RoleService roleService;
    private final RoleAuthorityService roleAuthorityService;
    private final RoleOrgService roleOrgService;
    private final UserRoleService userRoleService;

    /**
     * 分页查询角色
     */
    @ApiOperation(value = "分页查询角色", notes = "分页查询角色")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页", dataType = "long", paramType = "query", defaultValue = "1"),
        @ApiImplicitParam(name = "size", value = "每页显示几条", dataType = "long", paramType = "query", defaultValue = "10"),
    })
    @GetMapping("/page")
    @SysLog("分页查询角色")
    public Result<IPage<Role>> page(RolePageDTO param) {
        IPage<Role> page = getPage();
        Role role = BeanHelper.copyCopier(param, new Role(), true);
        // 构建值不为null的查询条件
        LbqWrapper<Role> query = Wraps
            .lbQ(role)
            .geHeader(Role::getCreateTime, param.getStartCreateTime())
            .leFooter(Role::getCreateTime, param.getEndCreateTime())
            .orderByDesc(Role::getId);
        roleService.page(page, query);
        return success(page);
    }

    /**
     * 查询角色
     */
    @ApiOperation(value = "查询角色", notes = "查询角色")
    @GetMapping("/{id}")
    @SysLog("查询角色")
    public Result<RoleQueryDTO> get(@PathVariable Long id) {
        Role role = roleService.getById(id);
        RoleQueryDTO roleQueryDTO = BeanHelper.copyCopier(role, new RoleQueryDTO(), true);
        List<Long> orgList = roleOrgService.listOrgByRoleId(role.getId());
        roleQueryDTO.setOrgList(orgList);

        return success(roleQueryDTO);
    }

    @ApiOperation(value = "检测角色编码", notes = "检测角色编码")
    @GetMapping("/check/{code}")
    @SysLog("新增角色")
    public Result<Boolean> check(@PathVariable String code) {
        return success(roleService.check(code));
    }

    /**
     * 新增角色
     */
    @ApiOperation(value = "新增角色", notes = "新增角色不为空的字段")
    @PostMapping
    @SysLog("新增角色")
    public Result<RoleSaveDTO> save(@RequestBody @Validated RoleSaveDTO data) {
        roleService.saveRole(data, getUserId());
        return success(data);
    }

    /**
     * 修改角色
     */
    @ApiOperation(value = "修改角色", notes = "修改角色不为空的字段")
    @PutMapping
    @SysLog("修改角色")
    public Result<RoleUpdateDTO> update(@RequestBody @Validated(SuperEntity.Update.class) RoleUpdateDTO data) {
        roleService.updateRole(data, getUserId());
        return success(data);
    }

    /**
     * 删除角色
     */
    @ApiOperation(value = "删除角色", notes = "根据id物理删除角色")
    @DeleteMapping
    @SysLog("删除角色")
    public Result<Boolean> delete(@RequestParam("ids[]") List<Long> ids) {
        roleService.removeById(ids);
        return success(true);
    }

    /**
     * 给用户分配角色
     */
    @ApiOperation(value = "给用户分配角色", notes = "给用户分配角色")
    @PostMapping("/user")
    @SysLog("给角色分配用户")
    public Result<Boolean> saveUserRole(@RequestBody UserRoleSaveDTO userRole) {
        return success(roleAuthorityService.saveUserRole(userRole));
    }

    /**
     * 查询角色的用户
     */
    @ApiOperation(value = "查询角色的用户", notes = "查询角色的用户")
    @GetMapping("/user/{roleId}")
    @SysLog("查询角色的用户")
    public Result<List<String>> findUserIdByRoleId(@PathVariable Long roleId) {
        List<UserRole> userRoles = userRoleService.list(Wraps
            .<UserRole>lbQ()
            .eq(UserRole::getRoleId, roleId));
        List<String> date = CollectionHelper.toList(userRoles, Objects::nonNull, in -> String.valueOf(in.getUserId()));
        return success(date);
    }

    /**
     * 查询角色拥有的资源id
     */
    @ApiOperation(value = "查询角色拥有的资源id集合", notes = "查询角色拥有的资源id集合")
    @GetMapping("/authority/{roleId}")
    @SysLog("查询角色拥有的资源")
    public Result<RoleAuthoritySaveDTO> findAuthorityIdByRoleId(@PathVariable Long roleId) {
        List<RoleAuthority> list = roleAuthorityService.list(Wraps
            .<RoleAuthority>lbQ()
            .eq(RoleAuthority::getRoleId, roleId));
        List<Long> menuIdList = list
            .stream()
            .filter(item -> AuthorizeType.MENU.eq(item.getAuthorityType()))
            .mapToLong(RoleAuthority::getAuthorityId)
            .boxed()
            .collect(Collectors.toList());
        List<Long> resourceIdList = list
            .stream()
            .filter(item -> AuthorizeType.RESOURCE.eq(item.getAuthorityType()))
            .mapToLong(RoleAuthority::getAuthorityId)
            .boxed()
            .collect(Collectors.toList());
        RoleAuthoritySaveDTO roleAuthority = RoleAuthoritySaveDTO
            .builder()
            .menuIdList(menuIdList)
            .resourceIdList(resourceIdList)
            .build();
        return success(roleAuthority);
    }


    /**
     * 给角色配置权限
     */
    @ApiOperation(value = "给角色配置权限", notes = "给角色配置权限")
    @PostMapping("/authority")
    @SysLog("给角色配置权限")
    public Result<Boolean> saveRoleAuthority(@RequestBody RoleAuthoritySaveDTO roleAuthoritySaveDTO) {
        return success(roleAuthorityService.saveRoleAuthority(roleAuthoritySaveDTO));
    }


    /**
     * 根据角色编码查询用户ID
     */
    @ApiOperation(value = "根据角色编码查询用户ID", notes = "根据角色编码查询用户ID")
    @GetMapping("/codes")
    @SysLog("根据角色编码查询用户ID")
    public Result<List<Long>> findUserIdByCode(@RequestParam(value = "codes") String[] codes) {
        return success(roleService.findUserIdByCode(codes));
    }
}