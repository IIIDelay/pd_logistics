package org.iiidev.pinda.authority.biz.service.auth.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.iiidev.pinda.authority.biz.dao.auth.UserMapper;
import org.iiidev.pinda.authority.biz.service.auth.UserRoleService;
import org.iiidev.pinda.authority.biz.service.auth.UserService;
import org.iiidev.pinda.authority.dto.auth.UserUpdatePasswordDTO;
import org.iiidev.pinda.authority.entity.auth.User;
import org.iiidev.pinda.authority.entity.auth.UserRole;
import org.iiidev.pinda.authority.vo.UserVO;
import org.iiidev.pinda.database.mybatis.conditions.Wraps;
import org.iiidev.pinda.database.mybatis.conditions.query.LbqWrapper;
import org.iiidev.pinda.utils.BeanHelper;
import org.iiidev.pinda.utils.BizAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 业务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final UserRoleService userRoleService;

    @Override
    public IPage<UserVO> findPage(LbqWrapper<User> wrapper) {
        IPage<User> pageList = baseMapper.findPage(new Page<>(), wrapper);
        IPage<UserVO> convert = pageList.convert(user -> BeanHelper.copyCopier(user, new UserVO(), false));
        return convert;
    }

    @Override
    public int resetPassErrorNum(Long id) {
        return baseMapper.resetPassErrorNum(id);
    }

    @Override
    public Boolean updatePassword(UserUpdatePasswordDTO data) {
        BizAssert.equals(data.getConfirmPassword(), data.getPassword(), "密码与确认密码不一致");

        User user = getById(data.getId());
        BizAssert.notNull(user, "用户不存在");
        String oldPassword = DigestUtils.md5Hex(data.getOldPassword());
        BizAssert.equals(user.getPassword(), oldPassword, "旧密码错误");

        User updateUser = new User();
        updateUser.setPassword(data.getPassword());
        updateUser.setId(data.getId());

        this.updateUser(updateUser);
        return true;
    }

    @Override
    public User getByAccount(String account) {
        return super.getOne(Wraps
            .<User>lbQ()
            .eq(User::getAccount, account));
    }

    @Override
    public List<User> findUserByRoleId(Long roleId, String keyword) {
        return baseMapper.findUserByRoleId(roleId, keyword);
    }

    @Override
    public void updatePasswordErrorNumById(Long id) {
        baseMapper.incrPasswordErrorNumById(id);
    }

    @Override
    public void updateLoginTime(String account) {
        baseMapper.updateLastLoginTime(account, LocalDateTime.now());
    }

    @Override
    public User saveUser(User user) {
        // 永不过期
        user.setPasswordExpireTime(null);

        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        user.setPasswordErrorNum(0);
        super.save(user);
        return user;
    }

    @Override
    public boolean reset(List<Long> ids) {
        LocalDateTime passwordExpireTime = null;
        String defPassword = "cea87ef1cb2e47570020bf7c014e1074";// pinda123
        super.update(Wraps
            .<User>lbU()
            .set(User::getPassword, defPassword)
            .set(User::getPasswordErrorNum, 0L)
            .set(User::getPasswordErrorLastTime, null)
            .set(User::getPasswordExpireTime, passwordExpireTime)
            .in(User::getId, ids)
        );
        return true;
    }

    @Override
    public User updateUser(User user) {
        // 永不过期
        user.setPasswordExpireTime(null);

        if (StrUtil.isNotEmpty(user.getPassword())) {
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        }
        super.updateById(user);
        return user;
    }

    @Override
    public boolean remove(List<Long> ids) {
        userRoleService.remove(Wraps
            .<UserRole>lbQ()
            .in(UserRole::getUserId, ids)
        );
        return super.removeByIds(ids);
    }
}