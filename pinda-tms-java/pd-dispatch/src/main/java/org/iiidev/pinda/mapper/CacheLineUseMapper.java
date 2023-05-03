package org.iiidev.pinda.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.iiidev.pinda.entity.CacheLineUseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface CacheLineUseMapper extends BaseMapper<CacheLineUseEntity> {
}
