package org.iiidev.pinda.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.iiidev.pinda.DTO.OrderCargoDto;
import org.iiidev.pinda.DTO.base.GoodsTypeDto;
import org.iiidev.pinda.common.utils.RespResult;
import org.iiidev.pinda.feign.CargoFeign;
import org.iiidev.pinda.feign.common.GoodsTypeFeign;
import org.iiidev.pinda.vo.base.businessHall.GoodsTypeVo;
import org.iiidev.pinda.vo.oms.OrderCargoVo;
import org.iiidev.pinda.vo.oms.OrderVo;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = "货品管理")
@RestController
@RequestMapping("order-manager/cargo")
@RequiredArgsConstructor
public class CargoController {
    private final CargoFeign cargoFeign;
    private final GoodsTypeFeign goodsTypeFeign;

    @ApiOperation(value = "添加货物")
    @PostMapping("")
    public OrderCargoVo saveOrderCargo(@RequestBody OrderCargoVo vo) {
        OrderCargoDto resultDto = cargoFeign.save(parseOderCargoVo2Dto(vo));
        BeanUtils.copyProperties(resultDto, vo);
        return vo;
    }

    @ApiOperation(value = "获取货物列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "orderId", value = "订单id", required = true, example = "0")
    })
    @GetMapping(value = "")
    public List<OrderCargoVo> findAll(@RequestParam(value = "orderId") String orderId) {
        List<OrderCargoDto> cargoDtoList = cargoFeign.findAll(null, orderId);
        return cargoDtoList.stream().map(orderCargoDto -> {
            OrderCargoVo vo = new OrderCargoVo();
            BeanUtils.copyProperties(orderCargoDto, vo);
            if (StringUtils.isNotEmpty(orderCargoDto.getGoodsTypeId())) {
                GoodsTypeDto goodTypeDto = goodsTypeFeign.fineById(orderCargoDto.getGoodsTypeId());
                if (goodTypeDto != null) {
                    GoodsTypeVo goodsTypeVo = new GoodsTypeVo();
                    BeanUtils.copyProperties(goodTypeDto, goodsTypeVo);
                    vo.setGoodsType(goodsTypeVo);
                }
            }
            if (StringUtils.isNotEmpty(orderCargoDto.getOrderId())) {
                OrderVo orderVo = new OrderVo();
                orderVo.setId(orderCargoDto.getOrderId());
                vo.setOrder(orderVo);
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @ApiOperation(value = "更新货物信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "货物id", required = true, example = "1", paramType = "{path}")
    })
    @PutMapping("/{id}")
    public OrderCargoVo updateOrderCargo(@PathVariable(name = "id") String id, @RequestBody OrderCargoVo vo) {
        vo.setId(id);
        OrderCargoDto resultDto = cargoFeign.update(id, parseOderCargoVo2Dto(vo));
        BeanUtils.copyProperties(resultDto, vo);
        return vo;
    }

    @ApiOperation(value = "删除货物")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "货物id", required = true, example = "1", paramType = "{path}")
    })
    @DeleteMapping("/{id}")
    public RespResult delete(@PathVariable(name = "id") String id) {
        cargoFeign.del(id);
        return RespResult.ok();
    }

    /**
     * 货物数据转换
     *
     * @param vo 货物数据
     * @return 货物数据
     */
    private OrderCargoDto parseOderCargoVo2Dto(OrderCargoVo vo) {
        OrderCargoDto dto = new OrderCargoDto();
        BeanUtils.copyProperties(vo, dto);
        if (vo.getGoodsType() != null) {
            dto.setGoodsTypeId(vo.getGoodsType().getId());
        }
        if (vo.getOrder() != null) {
            dto.setOrderId(vo.getOrder().getId());
        }
        return dto;
    }
}
