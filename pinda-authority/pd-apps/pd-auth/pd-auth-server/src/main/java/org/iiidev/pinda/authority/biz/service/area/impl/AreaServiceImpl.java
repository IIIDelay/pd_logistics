package org.iiidev.pinda.authority.biz.service.area.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.iiidev.pinda.authority.biz.dao.auth.AreaMapper;
import org.iiidev.pinda.authority.biz.service.area.AreaService;
import org.iiidev.pinda.authority.entity.common.Area;
import org.springframework.stereotype.Service;

@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {
}
