package org.iiidev.pinda.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.iiidev.pinda.authority.api.AreaApi;
import org.iiidev.pinda.common.utils.RespResult;
import org.iiidev.pinda.vo.AreaSimpleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("common")
@Api(tags = "公共信息")
@Slf4j
public class CommonController {
    @Autowired
    private AreaApi areaApi;

    @ApiOperation(value = "获取行政区域简要信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "父级id，无父级为0", required = true, example = "0")
    })
    @GetMapping(value = "area/simple")
    public RespResult areaSimple(@RequestParam(value = "parentId") String parentId) {
        return RespResult.ok().put("data", areaApi.findAll(StringUtils.isEmpty(parentId) ? null : Long.valueOf(parentId), null).getData().stream().map(area -> {
            AreaSimpleVo vo = new AreaSimpleVo();
            if (area != null && area.getId() != null) {
                BeanUtils.copyProperties(area, vo);
                vo.setId(String.valueOf(area.getId()));
            }
            return vo;
        }).collect(Collectors.toList()));
    }

}
