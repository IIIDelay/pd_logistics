package org.iiidev.pinda.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.ImmutableList;
import org.iiidev.pinda.DTO.OrgJobTreeDTO;
import org.iiidev.pinda.DTO.ScheduleJobDTO;
import org.iiidev.pinda.authority.api.OrgApi;
import org.iiidev.pinda.authority.entity.core.Org;
import org.iiidev.pinda.authority.enumeration.core.OrgEnum;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.common.utils.RespResult;
import org.iiidev.pinda.entity.ScheduleJobEntity;
import org.iiidev.pinda.service.IScheduleJobService;
import org.iiidev.pinda.utils.IdUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 定时任务
 *
 * @author
 */
@RestController
@RequestMapping("/schedule")
@Api(tags = "定时任务")
public class ScheduleJobController {
    private static final List<Integer> ORG_TYPE = ImmutableList.of(OrgEnum.BUSINESS_HALL.getType(), OrgEnum.TOP_TRANSFER_CENTER.getType(), OrgEnum.TOP_TRANSFER_CENTER.getType()).asList();

    @Autowired
    private IScheduleJobService scheduleJobService;

    @Autowired
    private OrgApi orgApi;


    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "name", paramType = "query", dataType = "String")
    })
    public Result<List<OrgJobTreeDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params) {

        List<OrgJobTreeDTO> tree = scheduleJobService.page(params);

        return Result.success(tree);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    public ScheduleJobDTO info(@PathVariable("id") String id) {
        ScheduleJobDTO schedule = scheduleJobService.get(id);
        return schedule;
    }

    @GetMapping("dispatch/{id}")
    @ApiOperation("调度信息")
    public RespResult dispatchInfo(@PathVariable("id") String id) {

        LambdaQueryWrapper<ScheduleJobEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleJobEntity::getBusinessId, id);
        ScheduleJobEntity scheduleJobEntity = scheduleJobService.getOne(wrapper);
        if (scheduleJobEntity == null) {
            return RespResult.error(404, "机构没有任务信息");
        }

        ScheduleJobDTO schedule = new ScheduleJobDTO();
        BeanUtils.copyProperties(scheduleJobEntity, schedule);
        return RespResult.ok().put("data", schedule);
    }

    @PostMapping
    @ApiOperation("保存")
    public RespResult save(@RequestBody ScheduleJobDTO dto) {

        scheduleJobService.save(dto);

        return RespResult.ok();
    }

    @PostMapping("dispatch")
    @ApiOperation("保存或修改")
    public RespResult dispatch(@RequestBody ScheduleJobDTO dto) {

        String businessId = dto.getBusinessId();

        Result<Org> orgResult = orgApi.get(Long.valueOf(businessId));

        Integer orgType = orgResult.getData().getOrgType();
        if (!ORG_TYPE.contains(orgType)) {
            return RespResult.error(400, "无法给转运中心以上的机构增加调度任务");
        }

        if (StringUtils.isNotBlank(dto.getId())) {
            dto.setUpdateDate(new Date());
            scheduleJobService.update(dto);
            return RespResult.ok();
        } else {
            dto.setId(IdUtils.get());
            dto.setBeanName("dispatchTask");
            dto.setCreateDate(new Date());
            scheduleJobService.save(dto);

            return RespResult.ok();
        }
    }

    @PutMapping
    @ApiOperation("修改")
    public RespResult update(@RequestBody ScheduleJobDTO dto) {

        scheduleJobService.update(dto);

        return RespResult.ok();
    }

    @DeleteMapping
    @ApiOperation("删除")
    public RespResult delete(@RequestBody String[] ids) {
        scheduleJobService.deleteBatch(ids);

        return RespResult.ok();
    }

    @PutMapping("/run/{id}")
    @ApiOperation("立即执行")
    public RespResult run(@PathVariable String id) {
        scheduleJobService.run(new String[]{id});

        return RespResult.ok();
    }

    @PutMapping("/run")
    @ApiOperation("立即执行")
    public RespResult run(@RequestBody String[] ids) {
        scheduleJobService.run(ids);

        return RespResult.ok();
    }

    @PutMapping("/pause/{id}")
    @ApiOperation("暂停")
    public RespResult pause(@PathVariable String id) {
        scheduleJobService.pause(new String[]{id});

        return RespResult.ok();
    }

    @PutMapping("/pause")
    @ApiOperation("暂停")
    public RespResult pause(@RequestBody String[] ids) {
        scheduleJobService.pause(ids);

        return RespResult.ok();
    }

    @PutMapping("/resume/{id}")
    @ApiOperation("恢复")
    public RespResult resume(@PathVariable String id) {
        scheduleJobService.resume(new String[]{id});

        return RespResult.ok();
    }

    @PutMapping("/resume")
    @ApiOperation("恢复")
    public RespResult resume(@RequestBody String[] ids) {
        scheduleJobService.resume(ids);

        return RespResult.ok();
    }

}
