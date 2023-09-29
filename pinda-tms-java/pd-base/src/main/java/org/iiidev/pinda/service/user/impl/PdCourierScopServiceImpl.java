package org.iiidev.pinda.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.iiidev.pinda.common.CustomIdGenerator;
import org.iiidev.pinda.mapper.user.PdCourierScopMapper;
import org.iiidev.pinda.entity.user.PdCourierScope;
import org.iiidev.pinda.service.user.IPdCourierScopeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 快递员业务范围表 服务实现类
 * </p>
 *
 * @since 2019-12-20
 */
@Service
public class PdCourierScopServiceImpl extends ServiceImpl<PdCourierScopMapper, PdCourierScope> implements IPdCourierScopeService {
    @Autowired
    private CustomIdGenerator idGenerator;

    @Override
    public void batchSave(List<PdCourierScope> scopeList) {
        scopeList.forEach(scope -> scope.setId(idGenerator.nextId(scope) + ""));
        saveBatch(scopeList);
    }

    @Override
    public void delete(String areaId, String userId) {
        LambdaQueryWrapper<PdCourierScope> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        boolean canExecute = false;
        if (StringUtils.isNotEmpty(areaId)) {
            lambdaQueryWrapper.eq(PdCourierScope::getAreaId, areaId);
            canExecute = true;
        }
        if (StringUtils.isNotEmpty(userId)) {
            lambdaQueryWrapper.eq(PdCourierScope::getUserId, userId);
            canExecute = true;
        }
        if (canExecute) {
            baseMapper.delete(lambdaQueryWrapper);
        }
    }

    @Override
    public List<PdCourierScope> findAll(String areaId, String userId) {
        LambdaQueryWrapper<PdCourierScope> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(areaId)) {
            lambdaQueryWrapper.eq(PdCourierScope::getAreaId, areaId);
        }
        if (StringUtils.isNotEmpty(userId)) {
            lambdaQueryWrapper.eq(PdCourierScope::getUserId, userId);
        }
        return baseMapper.selectList(lambdaQueryWrapper);
    }
}
