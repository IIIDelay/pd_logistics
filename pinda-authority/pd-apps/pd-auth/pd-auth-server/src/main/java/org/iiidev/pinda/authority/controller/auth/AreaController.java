package org.iiidev.pinda.authority.controller.auth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.iiidev.pinda.authority.biz.service.area.AreaService;
import org.iiidev.pinda.authority.entity.common.Area;
import org.iiidev.pinda.base.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author : Lusic
 * @create : 2022/9/5 20:09
 */
@RestController
@RequestMapping("/area")
public class AreaController {

    @Autowired
    private AreaService areaService;

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
        QueryWrapper<Area> qw = new QueryWrapper<>();
        if (ObjectUtil.isNotNull(parentId)) {
            qw.eq("parent_id", parentId);
        }
        if (CollUtil.isNotEmpty(ids)) {
            String s = ids.get(0) + "000000";
            if (ids.size() == 1) {
                qw.eq("area_code", s);
            } else {
                qw
                    .eq("area_code", ids.get(0))
                    .or()
                    .eq("area_code", ids.get(1))
                    .or()
                    .eq("area_code", ids.get(2));
            }
            List<Area> array = this.areaService.list(qw);
            for (Area arr : array) {
                arr.setId(Long.valueOf(arr.getAreaCode()));
            }
            return Result.success(array);
        } else {
            return Result.success(this.areaService.list(qw));
        }
    }
}
