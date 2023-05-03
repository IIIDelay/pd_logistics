package org.iiidev.pinda.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.iiidev.pinda.entity.TaskPickupDispatch;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 取件、派件任务信息表 Mapper 接口
 * </p>
 *
 * @author jpf
 * @since 2019-12-30
 */
@Mapper
public interface TaskPickupDispatchMapper extends BaseMapper<TaskPickupDispatch> {
}
