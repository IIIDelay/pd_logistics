package org.iiidev.pinda.authority.biz.service.core.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.authority.biz.dao.core.StationMapper;
import org.iiidev.pinda.authority.biz.service.core.StationService;
import org.iiidev.pinda.authority.dto.core.StationPageDTO;
import org.iiidev.pinda.authority.entity.core.Station;
import org.iiidev.pinda.database.mybatis.conditions.Wraps;
import org.iiidev.pinda.database.mybatis.conditions.query.LbqWrapper;
import org.iiidev.pinda.utils.BeanHelper;
import org.springframework.stereotype.Service;

/**
 * 业务实现类
 * 岗位
 */
@Slf4j
@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {

    @Override
    public IPage<Station> findStationPage(Page page, StationPageDTO data) {
        Station station = BeanHelper.copyCopier(data, new Station(), true);
        // Wraps.lbQ(station); 这种写法值 不能和  ${ew.customSqlSegment} 一起使用
        LbqWrapper<Station> wrapper = Wraps.lbQ();

        // ${ew.customSqlSegment} 语法一定要手动eq like 等
        wrapper
            .like(Station::getName, station.getName())
            .like(Station::getDescribe, station.getDescribe())
            .eq(Station::getOrgId, station.getOrgId())
            .eq(Station::getStatus, station.getStatus())
            .geHeader(Station::getCreateTime, data.getStartCreateTime())
            .leFooter(Station::getCreateTime, data.getEndCreateTime());
        wrapper.orderByDesc(Station::getId);
        return baseMapper.findStationPage(page, wrapper);
    }
}