package org.iiidev.pinda.authority.biz.service.common.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.authority.biz.dao.common.OptLogMapper;
import org.iiidev.pinda.authority.biz.service.common.OptLogService;
import org.iiidev.pinda.authority.entity.common.OptLogDO;
import org.iiidev.pinda.log.entity.OptLogDTO;
import org.iiidev.pinda.utils.BeanHelper;
import org.springframework.stereotype.Service;

/**
 * 业务实现类
 * 操作日志
 */
@Slf4j
@Service
public class OptLogServiceImpl extends ServiceImpl<OptLogMapper, OptLogDO> implements OptLogService {

    @Override
    public boolean save(OptLogDTO optLogDTO) {
        OptLogDO optLogDO = BeanHelper.copyCopier(optLogDTO, new OptLogDO(), true);
        return save(optLogDO);
    }
}