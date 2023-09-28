package org.iiidev.pinda.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.DTO.TaskTransportDTO;
import org.iiidev.pinda.DTO.webManager.TaskTransportQueryDTO;
import org.iiidev.pinda.authority.api.AreaApi;
import org.iiidev.pinda.authority.api.OrgApi;
import org.iiidev.pinda.authority.api.UserApi;
import org.iiidev.pinda.authority.entity.core.Org;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.common.utils.PageResponse;
import org.iiidev.pinda.feign.OrderFeign;
import org.iiidev.pinda.feign.TransportOrderFeign;
import org.iiidev.pinda.feign.TransportTaskFeign;
import org.iiidev.pinda.feign.transportline.TransportTripsFeign;
import org.iiidev.pinda.feign.truck.TruckFeign;
import org.iiidev.pinda.feign.webManager.WebManagerFeign;
import org.iiidev.pinda.util.BeanUtil;
import org.iiidev.pinda.vo.work.PointDTO;
import org.iiidev.pinda.vo.work.TaskTransportVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 * 运输任务表 前端控制器
 * </p>
 *
 * @author jpf
 * @since 2019-12-29
 */
@Slf4j
@RestController
@Api(tags = "运输任务API")
@RequestMapping("transport-task-manager")
@RequiredArgsConstructor
public class TransportTaskController {
    private final TransportTaskFeign transportTaskFeign;
    private final TransportTripsFeign transportTripsFeign;
    private final OrgApi orgApi;
    private final UserApi userApi;
    private final TruckFeign truckFeign;
    private final TransportOrderFeign transportOrderFeign;
    private final OrderFeign orderFeign;
    private final AreaApi areaApi;
    private final WebManagerFeign webManagerFeign;

    @ApiOperation(value = "获取运输任务分页数据")
    @PostMapping("/page")
    public PageResponse<TaskTransportVo> findByPage(@RequestBody TaskTransportVo vo) {
        TaskTransportQueryDTO dto = new TaskTransportQueryDTO();
        if (vo != null) {
            dto.setPage(vo.getPage());
            dto.setPageSize(vo.getPageSize());
            dto.setStatus(vo.getStatus());
            dto.setId(vo.getId());
            dto.setDriverName(vo.getDriverName());
        }
        PageResponse<TaskTransportDTO> dtoPageResponse = webManagerFeign.findTaskTransportByPage(dto);
        List<TaskTransportDTO> dtoList = dtoPageResponse.getItems();
        List<TaskTransportVo> voList = dtoList.stream()
            .map(taskTransportDTO -> BeanUtil.parseTaskTransportDTO2Vo(taskTransportDTO, transportTripsFeign, orgApi, userApi, truckFeign, transportOrderFeign, orderFeign, areaApi))
            .collect(Collectors.toList());
        return PageResponse.<TaskTransportVo>builder()
            .items(voList)
            .pagesize(vo.getPageSize())
            .page(vo.getPage())
            .counts(dtoPageResponse.getCounts())
            .pages(dtoPageResponse.getPages())
            .build();
    }

    @ApiOperation(value = "获取运输任务详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "运输任务id", required = true, example = "1", paramType = "{path}")
    })
    @GetMapping("/{id}")
    public TaskTransportVo findById(@PathVariable(name = "id") String id) {
        TaskTransportDTO dto = transportTaskFeign.findById(id);
        TaskTransportVo vo;
        // TODO: 2020/4/8 任务轨迹待实现
        if (dto != null) {
            vo = BeanUtil.parseTaskTransportDTO2Vo(dto, transportTripsFeign, orgApi, userApi, truckFeign, transportOrderFeign, orderFeign, areaApi);
        } else {
            vo = new TaskTransportVo();
            vo.setId(id);
        }
        return vo;
    }

    @ApiOperation(value = "获取运输任务坐标")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "运输任务id", required = true, example = "1", paramType = "{path}")
    })
    @GetMapping("point/{id}")
    public LinkedHashSet<PointDTO> findPointById(@PathVariable(name = "id") String id) {
        LinkedHashSet<PointDTO> pointDTOS = new LinkedHashSet<>();
        TaskTransportDTO dto = transportTaskFeign.findById(id);
        Result<Org> startOrgResult = orgApi.get(Long.parseLong(dto.getStartAgencyId()));
        Org startOrg = startOrgResult.getData();
        Result<Org> endOrgResult = orgApi.get(Long.parseLong(dto.getEndAgencyId()));
        Org endOrg = endOrgResult.getData();
        PointDTO pointDTO1 = new PointDTO();
        pointDTO1.setName(startOrg.getName());
        pointDTO1.setMarkerPoints(startOrg.getLongitude(), startOrg.getLatitude());
        pointDTOS.add(pointDTO1);
        PointDTO pointDTO2 = new PointDTO();
        pointDTO2.setName(endOrg.getName());
        pointDTO2.setMarkerPoints(endOrg.getLongitude(), endOrg.getLatitude());
        pointDTOS.add(pointDTO2);
        return pointDTOS;
    }

    @ApiOperation(value = "更新运输任务")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "运输任务id", required = true, example = "1", paramType = "{path}")
    })
    @PutMapping("/{id}")
    public TaskTransportVo update(@PathVariable(name = "id") String id, @RequestBody TaskTransportVo vo) {
        TaskTransportDTO dto = transportTaskFeign.updateById(id, BeanUtil.parseTaskTransportVo2DTO(vo));
        return BeanUtil.parseTaskTransportDTO2Vo(dto, transportTripsFeign, orgApi, userApi, truckFeign, transportOrderFeign, orderFeign, areaApi);
    }
}
