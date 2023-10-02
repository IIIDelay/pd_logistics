package org.iiidev.pinda.authority.controller.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.authority.biz.service.auth.RoleService;
import org.iiidev.pinda.authority.biz.service.auth.UserService;
import org.iiidev.pinda.authority.biz.service.core.OrgService;
import org.iiidev.pinda.authority.biz.service.core.StationService;
import org.iiidev.pinda.authority.dto.auth.UserPageDTO;
import org.iiidev.pinda.authority.dto.auth.UserRoleDTO;
import org.iiidev.pinda.authority.dto.auth.UserSaveDTO;
import org.iiidev.pinda.authority.dto.auth.UserUpdateAvatarDTO;
import org.iiidev.pinda.authority.dto.auth.UserUpdateDTO;
import org.iiidev.pinda.authority.dto.auth.UserUpdatePasswordDTO;
import org.iiidev.pinda.authority.entity.auth.Role;
import org.iiidev.pinda.authority.entity.auth.User;
import org.iiidev.pinda.authority.entity.core.Org;
import org.iiidev.pinda.authority.vo.UserVO;
import org.iiidev.pinda.base.BaseController;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.base.entity.SuperEntity;
import org.iiidev.pinda.database.mybatis.conditions.Wraps;
import org.iiidev.pinda.database.mybatis.conditions.query.LbqWrapper;
import org.iiidev.pinda.log.annotation.SysLog;
import org.iiidev.pinda.user.feign.UserQuery;
import org.iiidev.pinda.user.model.SysOrg;
import org.iiidev.pinda.user.model.SysRole;
import org.iiidev.pinda.user.model.SysStation;
import org.iiidev.pinda.user.model.SysUser;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 前端控制器
 * 用户
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/user")
@Api(value = "User", tags = "用户")
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class UserController extends BaseController {
    private final UserService userService;
    private final OrgService orgService;
    private final RoleService roleService;
    private final StationService stationService;

    /**
     * 分页查询用户
     */
    @ApiOperation(value = "分页查询用户", notes = "分页查询用户")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "页码", dataType = "long", paramType = "query", defaultValue = "1"),
        @ApiImplicitParam(name = "size", value = "分页条数", dataType = "long", paramType = "query", defaultValue = "10"),
    })
    @GetMapping("/page")
    @SysLog("分页查询用户")
    public Result<IPage<UserVO>> page(UserPageDTO userPage) {
        User user = BeanHelper.copyCopier(userPage, new User(), true);
        if (userPage.getOrgId() != null && userPage.getOrgId() >= 0) {
            user.setOrgId(null);
        }
        LbqWrapper<User> wrapper = Wraps.lbQ(user);
        if (userPage.getOrgId() != null && userPage.getOrgId() >= 0) {
            List<Org> children = orgService.findChildren(Arrays.asList(userPage.getOrgId()));
            wrapper.in(User::getOrgId, children
                .stream()
                .mapToLong(Org::getId)
                .boxed()
                .collect(Collectors.toList()));
        }
        wrapper
            .geHeader(User::getCreateTime, userPage.getStartCreateTime())
            .leFooter(User::getCreateTime, userPage.getEndCreateTime())
            .like(User::getName, userPage.getName())
            .like(User::getAccount, userPage.getAccount())
            .like(User::getEmail, userPage.getEmail())
            .like(User::getMobile, userPage.getMobile())
            .eq(User::getSex, userPage.getSex())
            .eq(User::getStatus, userPage.getStatus())
            .orderByDesc(User::getId);

        return success(userService.findPage(wrapper));
    }

    /**
     * 查询用户
     */
    @ApiOperation(value = "查询用户", notes = "查询用户")
    @GetMapping("/{id}")
    @SysLog("查询用户")
    public Result<User> get(@PathVariable Long id) {
        return success(userService.getById(id));
    }

    @ApiOperation(value = "查询所有用户", notes = "查询所有用户")
    @GetMapping("/find")
    @SysLog("查询所有用户")
    public Result<List<Long>> findAllUserId() {
        return success(userService
            .list()
            .stream()
            .mapToLong(User::getId)
            .boxed()
            .collect(Collectors.toList()));
    }

    /**
     * 新增用户
     */
    @ApiOperation(value = "新增用户", notes = "新增用户不为空的字段")
    @PostMapping
    @SysLog("新增用户")
    public Result<User> save(@RequestBody @Validated UserSaveDTO data) {
        User user = BeanHelper.copyCopier(data, new User(), true);
        userService.saveUser(user);
        return success(user);
    }

    /**
     * 修改用户
     */
    @ApiOperation(value = "修改用户", notes = "修改用户不为空的字段")
    @PutMapping
    @SysLog("修改用户")
    public Result<User> update(@RequestBody @Validated(SuperEntity.Update.class) UserUpdateDTO data) {
        User user = BeanHelper.copyCopier(data, new User(), true);
        userService.updateUser(user);
        return success(user);
    }

    @ApiOperation(value = "修改头像", notes = "修改头像")
    @PutMapping("/avatar")
    @SysLog("修改头像")
    public Result<User> avatar(@RequestBody @Validated(SuperEntity.Update.class) UserUpdateAvatarDTO data) {
        User user = BeanHelper.copyCopier(data, new User(), true);
        userService.updateUser(user);
        return success(user);
    }

    @ApiOperation(value = "修改密码", notes = "修改密码")
    @PutMapping("/password")
    @SysLog("修改密码")
    public Result<Boolean> updatePassword(@RequestBody UserUpdatePasswordDTO data) {
        return success(userService.updatePassword(data));
    }

    @ApiOperation(value = "重置密码", notes = "重置密码")
    @GetMapping("/reset")
    @SysLog("重置密码")
    public Result<Boolean> resetTx(@RequestParam("ids[]") List<Long> ids) {
        userService.reset(ids);
        return success();
    }

    /**
     * 删除用户
     */
    @ApiOperation(value = "删除用户", notes = "根据id物理删除用户")
    @DeleteMapping
    @SysLog("删除用户")
    public Result<Boolean> delete(@RequestParam("ids[]") List<Long> ids) {
        userService.remove(ids);
        return success(true);
    }


    /**
     * 单体查询用户
     */
    @ApiOperation(value = "查询用户详细", notes = "查询用户详细")
    @PostMapping(value = "/anno/id/{id}")
    public Result<SysUser> getById(@PathVariable Long id, @RequestBody UserQuery query) {
        User user = userService.getById(id);
        if (user == null) {
            return success(null);
        }
        SysUser sysUser = BeanHelper.copyCopier(user, new SysUser(), true);

        if (query.getFull() || query.getOrg()) {
            SysOrg sysOrg = BeanHelper.copyCopier(orgService.getById(user.getOrgId()), new SysOrg(), true);
            sysUser.setOrg(sysOrg);
        }

        if (query.getFull() || query.getStation()) {
            SysStation sysStation = BeanHelper.copyCopier(stationService.getById(user.getStationId()), new SysStation(), true);
            sysUser.setStation(sysStation);
        }

        if (query.getFull() || query.getRoles()) {
            List<Role> list = roleService.findRoleByUserId(id);
            sysUser.setRoles(BeanHelper.mapList(list, SysRole.class, true));
        }

        return success(sysUser);
    }

    /**
     * 查询角色的已关联用户
     *
     * @param roleId  角色id
     * @param keyword 账号account或名称name
     */
    @ApiOperation(value = "查询角色的已关联用户", notes = "查询角色的已关联用户")
    @GetMapping(value = "/role/{roleId}")
    public Result<UserRoleDTO> findUserByRoleId(@PathVariable("roleId") Long roleId, @RequestParam(value = "keyword", required = false) String keyword) {
        List<User> userList = userService.findUserByRoleId(roleId, keyword);
        List<Long> idList = CollectionHelper.toList(userList, User::getId);
        UserRoleDTO dto = new UserRoleDTO();
        dto.setIdList(idList);
        dto.setUserList(userList);
        return success(dto);
    }
}