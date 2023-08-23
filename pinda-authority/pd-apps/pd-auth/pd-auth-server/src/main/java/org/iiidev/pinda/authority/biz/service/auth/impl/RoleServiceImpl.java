package org.iiidev.pinda.authority.biz.service.auth.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.authority.biz.dao.auth.RoleMapper;
import org.iiidev.pinda.authority.biz.service.auth.RoleAuthorityService;
import org.iiidev.pinda.authority.biz.service.auth.RoleOrgService;
import org.iiidev.pinda.authority.biz.service.auth.RoleService;
import org.iiidev.pinda.authority.biz.service.auth.UserRoleService;
import org.iiidev.pinda.authority.biz.service.auth.UserService;
import org.iiidev.pinda.authority.dto.auth.RoleSaveDTO;
import org.iiidev.pinda.authority.dto.auth.RoleUpdateDTO;
import org.iiidev.pinda.authority.entity.auth.Role;
import org.iiidev.pinda.authority.entity.auth.RoleAuthority;
import org.iiidev.pinda.authority.entity.auth.RoleOrg;
import org.iiidev.pinda.authority.entity.auth.User;
import org.iiidev.pinda.authority.entity.auth.UserRole;
import org.iiidev.pinda.authority.util.RedisHelper;
import org.iiidev.pinda.base.id.CodeGenerate;
import org.iiidev.pinda.common.constant.CacheKey;
import org.iiidev.pinda.database.mybatis.conditions.Wraps;
import org.iiidev.pinda.utils.BeanHelper;
import org.iiidev.pinda.utils.StrHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务实现类
 * 角色
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleOrgService roleOrgService;

    private final RoleAuthorityService roleAuthorityService;

    private final UserRoleService userRoleService;

    private final UserService userService;

    private final CodeGenerate codeGenerate;

    @Override
    public boolean removeById(List<Long> ids) {
        if (ids.isEmpty()) {
            return true;
        }
        ids.forEach(roleId -> {
            List<User> userList = userService.findUserByRoleId(roleId, null);
            if (userList != null && userList.size() > 0) {
                userList.forEach(user ->
                    RedisHelper.remove(CacheKey.USER_RESOURCE, String.valueOf(user.getId())));
            }
        });

        // 删除主表pd_auth_role数据
        super.removeByIds(ids);
        // 删除pd_auth_role_org关系表数据
        roleOrgService.remove(Wraps
            .<RoleOrg>lbQ()
            .in(RoleOrg::getRoleId, ids));
        // 删除pd_auth_role_authority关系表数据
        roleAuthorityService.remove(Wraps
            .<RoleAuthority>lbQ()
            .in(RoleAuthority::getRoleId, ids));
        // 删除pd_auth_user_role关系表数据
        userRoleService.remove(Wraps
            .<UserRole>lbQ()
            .in(UserRole::getRoleId, ids));

        return true;
    }

    @Override
    public List<Role> findRoleByUserId(Long userId) {
        return baseMapper.findRoleByUserId(userId);
    }

    /**
     * 1，保存角色
     * 2，保存 与组织的关系
     */
    @Override
    public void saveRole(RoleSaveDTO data, Long userId) {
        Role role = BeanHelper.copyCopier(data, new Role(), true);
        role.setCode(StrHelper.getOrDef(data.getCode(), codeGenerate.next()));
        role.setReadonly(false);
        super.save(role);
        saveRoleOrg(userId, role, data.getOrgList());
    }

    @Override
    public void updateRole(RoleUpdateDTO data, Long userId) {
        Role role = BeanHelper.copyCopier(data, new Role(), true);
        super.updateById(role);

        roleOrgService.remove(Wraps
            .<RoleOrg>lbQ()
            .eq(RoleOrg::getRoleId, data.getId()));
        saveRoleOrg(userId, role, data.getOrgList());
    }

    private void saveRoleOrg(Long userId, Role role, List<Long> orgList) {
        if (orgList != null && !orgList.isEmpty()) {
            List<RoleOrg> list = orgList
                .stream()
                .map(orgId ->
                    RoleOrg.builder()
                        .orgId(orgId)
                        .roleId(role.getId())
                        .build()
                ).collect(Collectors.toList());
            roleOrgService.saveBatch(list);
        }
    }

    @Override
    public List<Long> findUserIdByCode(String[] codes) {
        return baseMapper.findUserIdByCode(codes);
    }

    @Override
    public Boolean check(String code) {
        return super.count(Wraps
            .<Role>lbQ()
            .eq(Role::getCode, code)) > 0;
    }
}
