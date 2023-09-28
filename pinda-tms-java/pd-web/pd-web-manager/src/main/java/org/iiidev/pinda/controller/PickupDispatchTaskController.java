package org.iiidev.pinda.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.DTO.TaskPickupDispatchDTO;
import org.iiidev.pinda.DTO.webManager.TaskPickupDispatchQueryDTO;
import org.iiidev.pinda.authority.api.AreaApi;
import org.iiidev.pinda.authority.api.OrgApi;
import org.iiidev.pinda.authority.api.UserApi;
import org.iiidev.pinda.common.utils.PageResponse;
import org.iiidev.pinda.feign.OrderFeign;
import org.iiidev.pinda.feign.PickupDispatchTaskFeign;
import org.iiidev.pinda.feign.webManager.WebManagerFeign;
import org.iiidev.pinda.util.BeanUtil;
import org.iiidev.pinda.vo.work.TaskPickupDispatchVo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 取件、派件任务信息表 前端控制器
 * </p>
 *
 * @author jpf
 * @since 2019-12-29
 */
@Api(tags = "派件、取件任务相关API")
@Slf4j
@RestController
@RequestMapping("pickup-dispatch-task-manager")
@RequiredArgsConstructor
public class PickupDispatchTaskController {
    private final OrderFeign orderFeign;
    private final AreaApi areaApi;
    private final OrgApi orgApi;
    private final PickupDispatchTaskFeign pickupDispatchTaskFeign;
    private final UserApi userApi;
    private final WebManagerFeign webManagerFeign;

    @ApiOperation(value = "获取取派件分页数据")
    @PostMapping("/page")
    public PageResponse<TaskPickupDispatchVo> findByPage(@RequestBody TaskPickupDispatchVo vo) {
        TaskPickupDispatchQueryDTO dto = new TaskPickupDispatchQueryDTO();
        dto.setPage(vo.getPage());
        dto.setPageSize(vo.getPageSize());
        if (vo.getTransportOrder() != null) {
            dto.setTransportOrderId(vo.getTransportOrder().getId());
        }
        if (vo.getCourier() != null) {
            dto.setCourierName(vo.getCourier().getName());
        }
        dto.setTaskType(vo.getTaskType());
        dto.setStatus(vo.getStatus());
        if (vo.getOrder() != null) {
            dto.setSenderName(vo.getOrder().getSenderName());
            if (vo.getOrder().getSenderProvince() != null) {
                dto.setSenderProvinceId(vo.getOrder().getSenderProvince().getId());
            }
            if (vo.getOrder().getSenderCity() != null) {
                dto.setSenderCityId(vo.getOrder().getSenderCity().getId());
            }
            dto.setReceiverName(vo.getOrder().getReceiverName());
            if (vo.getOrder().getReceiverProvince() != null) {
                dto.setReceiverProvinceId(vo.getOrder().getReceiverProvince().getId());
            }
            if (vo.getOrder().getReceiverCity() != null) {
                dto.setReceiverCityId(vo.getOrder().getReceiverCity().getId());
            }
        }
        PageResponse<TaskPickupDispatchDTO> dtoPageResponse = webManagerFeign.findTaskPickupDispatchJobByPage(dto);
        List<TaskPickupDispatchDTO> dtoList = dtoPageResponse.getItems();
        List<TaskPickupDispatchVo> voList = dtoList.stream()
            .map(taskPickupDispatchDTO -> BeanUtil.parseTaskPickupDispatchDTO2Vo(taskPickupDispatchDTO, orderFeign, areaApi, orgApi, userApi))
            .collect(Collectors.toList());
        return PageResponse.<TaskPickupDispatchVo>builder()
            .items(voList)
            .pagesize(vo.getPageSize())
            .page(vo.getPage())
            .counts(dtoPageResponse.getCounts())
            .pages(dtoPageResponse.getPages())
            .build();
    }

    @ApiOperation(value = "更新取派件任务")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "取派件任务id", required = true, example = "1", paramType = "{path}")
    })
    @PutMapping("/{id}")
    public TaskPickupDispatchVo update(@PathVariable(name = "id") String id, @RequestBody TaskPickupDispatchVo vo) {
        TaskPickupDispatchDTO dto = pickupDispatchTaskFeign.updateById(id, BeanUtil.parseTaskPickupDispatchVo2DTO(vo));
        return BeanUtil.parseTaskPickupDispatchDTO2Vo(dto, orderFeign, areaApi, orgApi, userApi);
    }
}
