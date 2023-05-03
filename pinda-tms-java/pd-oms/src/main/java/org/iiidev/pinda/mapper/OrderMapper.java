package org.iiidev.pinda.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.iiidev.pinda.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 订单 Mapper 接口
 */
@Component
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
