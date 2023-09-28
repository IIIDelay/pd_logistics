package org.iiidev.pinda.controller;


import org.iiidev.pinda.DTO.base.GoodsTypeDto;
import org.iiidev.pinda.common.utils.RespResult;
import org.iiidev.pinda.feign.common.GoodsTypeFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <p>
 * 运单表 前端控制器
 * </p>
 *
 * @author diesel
 * @since 2020-03-24
 */
@Slf4j
@Api(tags = "物品类型")
@Controller
@RequestMapping("goodsType")
public class GoodsTypeController {

    private final GoodsTypeFeign goodsTypeFeign;

    public GoodsTypeController(GoodsTypeFeign goodsTypeFeign) {
        this.goodsTypeFeign = goodsTypeFeign;
    }

    @ApiOperation(value = "全部类型")
    @ResponseBody
    @GetMapping("all")
    public RespResult all() {

        List<GoodsTypeDto> goodsType = goodsTypeFeign.findAll();

        return RespResult.ok().put("data", goodsType);
    }
}
