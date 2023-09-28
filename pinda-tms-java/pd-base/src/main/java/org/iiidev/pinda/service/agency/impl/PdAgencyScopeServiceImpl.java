package org.iiidev.pinda.service.agency.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.iiidev.pinda.common.CustomIdGenerator;
import org.iiidev.pinda.entity.agency.PdAgencyScope;
import org.iiidev.pinda.mapper.agency.PdAgencyScopMapper;
import org.iiidev.pinda.service.agency.IPdAgencyScopeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 机构业务范围表 服务实现类
 * </p>
 *
 * @since 2019-12-20
 */
@Service
@RequiredArgsConstructor
public class PdAgencyScopeServiceImpl extends ServiceImpl<PdAgencyScopMapper, PdAgencyScope> implements IPdAgencyScopeService {

    private final CustomIdGenerator idGenerator;

    @Override
    public void batchSave(List<PdAgencyScope> scopeList) {
        scopeList.forEach(scope -> scope.setId(idGenerator.nextId(scope) + ""));
        saveBatch(scopeList);
    }

    @Override
    public void delete(String areaId, String agencyId) {
        LambdaQueryWrapper<PdAgencyScope> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        boolean canExecute = false;
        if (StringUtils.isNotEmpty(areaId)) {
            lambdaQueryWrapper.eq(PdAgencyScope::getAreaId, areaId);
            canExecute = true;
        }
        if (StringUtils.isNotEmpty(agencyId)) {
            lambdaQueryWrapper.eq(PdAgencyScope::getAgencyId, agencyId);
            canExecute = true;
        }
        if (canExecute) {
            baseMapper.delete(lambdaQueryWrapper);
        }
    }

    @Override
    public List<PdAgencyScope> findAll(String areaId, String agencyId, List<String> agencyIds, List<String> areaIds) {
        LambdaQueryWrapper<PdAgencyScope> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(areaId)) {
            lambdaQueryWrapper.eq(PdAgencyScope::getAreaId, areaId);
        }
        if (StringUtils.isNotEmpty(agencyId)) {
            lambdaQueryWrapper.eq(PdAgencyScope::getAgencyId, agencyId);
        }
        if (agencyIds != null && agencyIds.size() > 0) {
            lambdaQueryWrapper.in(PdAgencyScope::getAgencyId, agencyIds);
        }
        if (areaIds != null && areaIds.size() > 0) {
            lambdaQueryWrapper.in(PdAgencyScope::getAreaId, areaIds);
        }
        return baseMapper.selectList(lambdaQueryWrapper);
    }
}
