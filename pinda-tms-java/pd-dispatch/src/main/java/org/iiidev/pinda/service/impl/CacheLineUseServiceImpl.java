/**
 * Copyright (c) 2019 联智合创 All rights reserved.
 * <p>
 * http://www.witlinked.com
 * <p>
 * 版权所有，侵权必究！
 */

package org.iiidev.pinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.iiidev.pinda.entity.CacheLineUseEntity;
import org.iiidev.pinda.mapper.CacheLineUseMapper;
import org.iiidev.pinda.service.ICacheLineUseService;
import org.springframework.stereotype.Service;

@Service
public class CacheLineUseServiceImpl extends ServiceImpl<CacheLineUseMapper, CacheLineUseEntity> implements ICacheLineUseService {

    @Override
    public CacheLineUseEntity getByOrderClassifyId(String id) {
        LambdaQueryWrapper<CacheLineUseEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CacheLineUseEntity::getOrderClassifyId, id);
        return this.baseMapper.selectOne(wrapper);
    }
}