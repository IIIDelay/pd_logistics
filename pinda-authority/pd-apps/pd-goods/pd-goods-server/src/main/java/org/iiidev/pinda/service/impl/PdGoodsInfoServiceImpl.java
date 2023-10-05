package org.iiidev.pinda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.iiidev.goods.entity.GoodsInfo;
import org.iiidev.pinda.dao.PdGoodsInfoDao;
import org.iiidev.pinda.service.PdGoodsInfoService;
import org.springframework.stereotype.Service;

/**
 * 商品信息表(PdGoodsInfo)表服务实现类
 *
 * @author iiidev
 * @since 2023-10-03 12:49:17
 */
@Service
public class PdGoodsInfoServiceImpl extends ServiceImpl<PdGoodsInfoDao, GoodsInfo> implements PdGoodsInfoService {

}

