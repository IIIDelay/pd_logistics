package org.iiidev.pinda.authority.biz.dao.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.iiidev.pinda.authority.dto.auth.ResourceQueryDTO;
import org.iiidev.pinda.authority.entity.auth.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 资源
 * </p>
 */
@Repository
public interface ResourceMapper extends BaseMapper<Resource> {
    /**
     * 查询用户拥有的资源
     */
    List<Resource> findVisibleResource(ResourceQueryDTO resource);

    List<Long> findMenuIdByResourceId(@Param("resourceIdList") List<Long> resourceIdList);
}
