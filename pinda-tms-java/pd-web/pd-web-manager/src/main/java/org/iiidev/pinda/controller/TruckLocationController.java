package org.iiidev.pinda.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.DTO.TaskTransportDTO;
import org.iiidev.pinda.DTO.transportline.TransportTripsTruckDriverDto;
import org.iiidev.pinda.DTO.truck.TruckDto;
import org.iiidev.pinda.DTO.truck.TruckTypeDto;
import org.iiidev.pinda.authority.api.UserApi;
import org.iiidev.pinda.authority.entity.auth.User;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.enums.transporttask.TransportTaskStatus;
import org.iiidev.pinda.feign.TransportTaskFeign;
import org.iiidev.pinda.feign.transportline.TransportTripsFeign;
import org.iiidev.pinda.feign.truck.TruckFeign;
import org.iiidev.pinda.feign.truck.TruckTypeFeign;
import org.iiidev.pinda.vo.base.transforCenter.business.TruckLocationVo;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "位置查询")
@Slf4j
@RestController
@RequestMapping("transfor-center")
@RequiredArgsConstructor
public class TruckLocationController {
    private final UserApi userApi;
    private final TruckFeign truckFeign;
    private final TruckTypeFeign truckTypeFeign;
    private final TransportTaskFeign transportTaskFeign;
    private final TransportTripsFeign transportTripsFeign;


    @ApiOperation(value = "获取车辆位置详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "车辆id", required = true, example = "1", paramType = "{path}")
    })
    @GetMapping("truck-place-info/{id}")
    public TruckLocationVo findTruckById(@PathVariable(name = "id") String id) {
        TruckLocationVo truckLocationVo = new TruckLocationVo();
        TruckDto truck = truckFeign.fineById(id);
        if (ObjectUtils.isEmpty(truck)) {
            return truckLocationVo;
        }
        truckLocationVo.setLicensePlate(truck.getLicensePlate());

        TruckTypeDto truckTypeDto = truckTypeFeign.fineById(truck.getTruckTypeId());
        if (!ObjectUtils.isEmpty(truckTypeDto)) {
            truckLocationVo.setTruckTypeName(truckTypeDto.getName());
        }

        TaskTransportDTO taskTransportDto = new TaskTransportDTO();
        taskTransportDto.setTruckId(truck.getId());
        taskTransportDto.setStatus(TransportTaskStatus.PROCESSING.getCode());
        List<TaskTransportDTO> transportTaskDtos = transportTaskFeign.findAll(taskTransportDto);
        if (CollectionUtils.isEmpty(transportTaskDtos)) {
            return truckLocationVo;
        }

        taskTransportDto = transportTaskDtos.get(0);
        List<TransportTripsTruckDriverDto> transportTripsTruckDriverDtos = transportTripsFeign.findAllTruckDriverTransportTrips(taskTransportDto.getTransportTripsId(), truck.getId(), null);
        if (CollectionUtils.isEmpty(transportTripsTruckDriverDtos)) {
            return truckLocationVo;
        }

        TransportTripsTruckDriverDto transportTripsTruckDriverDto = transportTripsTruckDriverDtos.get(0);
        String userId = transportTripsTruckDriverDto.getUserId();
        Result<User> userResult = userApi.get(Long.valueOf(userId));
        User user = userResult.getData();
        truckLocationVo.setName(user.getName());
        truckLocationVo.setMobile(user.getMobile());
        truckLocationVo.setAvatar(user.getAvatar());

        return truckLocationVo;
    }
}
