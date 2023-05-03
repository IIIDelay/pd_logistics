package org.iiidev.pinda.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.iiidev.pinda.DTO.AppCourierQueryDTO;
import org.iiidev.pinda.DTO.TaskPickupDispatchDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface CourierMapper extends BaseMapper {
    IPage<TaskPickupDispatchDTO> findByPage(IPage<TaskPickupDispatchDTO> iPage, @Param("params") AppCourierQueryDTO dto);
}
