package org.iiidev.pinda.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.iiidev.goods.entity.GoodsInfo;

import java.util.List;

/**
 * 商品信息表(PdGoodsInfo)表数据库访问层
 *
 * @author iiidev
 * @since 2023-10-03 12:49:16
 */
public interface PdGoodsInfoDao extends BaseMapper<GoodsInfo> {

    /**
     * 批量新增数据(MyBatis原生foreach方法)
     *
     * @param entities List<PdGoodsInfo> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<GoodsInfo> entities);

    /**
     * 批量新增或按主键更新数据(MyBatis原生foreach方法)
     *
     * @param entities List<PdGoodsInfo> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<GoodsInfo> entities);

}

