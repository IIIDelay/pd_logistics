package org.iiidev.pinda.authority.biz.dao.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.iiidev.pinda.authority.entity.auth.UserRole;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 角色分配
 * 账号角色绑定
 * </p>
 *
 */
@Repository
public interface UserRoleMapper extends BaseMapper<UserRole> {

}
