package org.iiidev.pinda.authority.biz.dao.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.iiidev.pinda.authority.entity.auth.Menu;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 菜单
 * </p>
 */
@Repository
public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 查询用户可用菜单
     *
     * @param group
     * @param userId
     * @return
     */
    List<Menu> findVisibleMenu(@Param("group") String group, @Param("userId") Long userId);
}
