package org.iiidev.pinda.authority.biz.service.common;

import com.baomidou.mybatisplus.extension.service.IService;
import org.iiidev.pinda.authority.entity.common.OptLog;
import org.iiidev.pinda.log.entity.OptLogDTO;

/**
 * 业务接口
 * 操作日志
 */
public interface OptLogService extends IService<OptLog> {
    /**
     * 保存日志
     */
    boolean save(OptLogDTO entity);
}
