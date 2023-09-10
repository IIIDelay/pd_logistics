package org.iiidev.pinda.authority.controller.auth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.iiidev.pinda.authority.biz.service.area.AreaService;
import org.iiidev.pinda.authority.entity.common.Area;
import org.iiidev.pinda.base.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/area")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @GetMapping({"/{id}"})
    @ApiOperation(value = "根据ID查询区域信息", notes = "根据ID查询区域信息")
    Result<Area> get(@PathVariable Long id) {
        if (ObjectUtil.isNotNull(id)) {
            QueryWrapper<Area> qw = new QueryWrapper<>();
            qw.eq("area_code", id);
            Area one = this.areaService.getOne(qw);
            return Result.success(one);
        }
        return null;
    }

    @GetMapping({"/code/{code}"})
    Result<Area> getByCode(@PathVariable String code) {
        if (StrUtil.isNotBlank(code)) {
            QueryWrapper<Area> qw = new QueryWrapper<>();
            qw.eq("area_code", code);
            Area one = this.areaService.getOne(qw);
            return Result.success(one);
        }
        return null;
    }

    @GetMapping
    Result<List<Area>> findAll(@RequestParam(value = "parentId", required = false) Long parentId, @RequestParam(value = "ids", required = false) List<Long> ids) {
        LambdaQueryChainWrapper<Area> areaQW = areaService.lambdaQuery()
            .eq(null != parentId, Area::getParentId, parentId);

        if (CollUtil.isNotEmpty(ids)) {
            String firstCode = ids.stream().findFirst().map(id -> StringUtils.rightPad(String.valueOf(id), 6)).orElse("");
            if (ids.size() == 1) {
                areaQW.eq(Area::getAreaCode, firstCode);
            } else {
                areaQW.eq(Area::getAreaCode, ids.get(0))
                    .or()
                    .eq(Area::getAreaCode, ids.get(1))
                    .or()
                    .eq(Area::getAreaCode, ids.get(2));
            }
            List<Area> array = areaService.list(areaQW);
            for (Area arr : array) {
                arr.setId(Long.valueOf(arr.getAreaCode()));
            }
            return Result.success(array);
        } else {
            return Result.success(this.areaService.list(areaQW));
        }
    }
}
