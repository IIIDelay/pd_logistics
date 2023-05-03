package org.iiidev.pinda.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.iiidev.pinda.DTO.AppDriverQueryDTO;
import org.iiidev.pinda.DTO.DriverJobDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface DriverMapper extends BaseMapper {
    IPage<DriverJobDTO> findByPage(IPage<DriverJobDTO> iPage, @Param("params") AppDriverQueryDTO dto);
}
