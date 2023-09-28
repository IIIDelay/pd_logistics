package org.iiidev.pinda.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.iiidev.pinda.DTO.DriverJobDTO;
import org.iiidev.pinda.DTO.TaskTransportDTO;
import org.iiidev.pinda.DTO.UserProfileDTO;
import org.iiidev.pinda.DTO.angency.FleetDto;
import org.iiidev.pinda.DTO.truck.TruckDto;
import org.iiidev.pinda.DTO.user.TruckDriverDto;
import org.iiidev.pinda.authority.api.OrgApi;
import org.iiidev.pinda.authority.api.UserApi;
import org.iiidev.pinda.authority.entity.auth.User;
import org.iiidev.pinda.authority.entity.core.Org;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.common.context.RequestContext;
import org.iiidev.pinda.common.utils.RespResult;
import org.iiidev.pinda.enums.driverjob.DriverJobStatus;
import org.iiidev.pinda.feign.DriverJobFeign;
import org.iiidev.pinda.feign.TransportTaskFeign;
import org.iiidev.pinda.feign.agency.FleetFeign;
import org.iiidev.pinda.feign.truck.TruckFeign;
import org.iiidev.pinda.feign.user.DriverFeign;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static org.iiidev.pinda.base.Result.success;

/**
 * <p>
 * 运单表 前端控制器
 * </p>
 *
 * @author diesel
 * @since 2020-03-19
 */
@Slf4j
@Api(tags = "用户管理")
@Controller
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserApi userApi;

    private final OrgApi orgApi;

    private final FleetFeign fleetFeign;

    private final DriverFeign driverFeign;

    private final DriverJobFeign driverJobFeign;

    private final TransportTaskFeign transportTaskFeign;

    private final TruckFeign truckFeign;


    @ApiOperation(value = "我的信息")
    @ResponseBody
    @GetMapping("profile")
    public RespResult profile() {

        //  获取司机id  并放入参数
        String driverId = RequestContext.getUserId();
        log.info("司机端-登录用户：{}", driverId);
        // 基本信息
        Result<User> userResult = userApi.get(Long.valueOf(driverId));
        User user = userResult.getData();
        log.info("司机端-登录用户：{}", user);
        // 司机信息
        TruckDriverDto truckDriverDto = driverFeign.findOneDriver(driverId);
        log.info("司机端-司机信息：{}", truckDriverDto);

        // 司机任务信息
        String truckId = null;
        String licensePlate = null;
        String transportTaskId = null;
        DriverJobDTO driverJobDto = new DriverJobDTO();
        driverJobDto.setStatus(DriverJobStatus.PROCESSING.getCode());
        driverJobDto.setDriverId(driverId);
        List<DriverJobDTO> driverJobDtos = driverJobFeign.findAll(driverJobDto);
        log.info("司机端-在途任务：{}", driverJobDtos);
        if (!CollectionUtils.isEmpty(driverJobDtos)) {
            driverJobDto = driverJobDtos.get(0);
            String taskTransportId = driverJobDto.getTaskTransportId();
            TaskTransportDTO transportTaskDto = transportTaskFeign.findById(taskTransportId);
            log.info("司机端-在途任务详情：{}", transportTaskDto);
            if (transportTaskDto != null) {
                transportTaskId = transportTaskDto.getId();
                truckId = transportTaskDto.getTruckId();
                TruckDto truckDto = truckFeign.fineById(truckId);
                log.info("司机端-车辆信息：{}", truckDto);
                licensePlate = truckDto.getLicensePlate();
            }
        }
        // 所属机构
        Result<Org> orgResult = orgApi.get(user.getOrgId());
        Org org = orgResult.getData();
        FleetDto fleetDto = null;
        Org fleetOrg = null;
        if (StringUtils.isNotEmpty(truckDriverDto.getFleetId())) {
            // 车队信息
            fleetDto = fleetFeign.fineById(truckDriverDto.getFleetId());
            log.info("司机端-车队信息：{}", fleetDto);
            // 运转中心
            if (StringUtils.isNotEmpty(fleetDto.getAgencyId())) {
                Result<Org> fleetOrgResult = orgApi.get(Long.valueOf(fleetDto.getAgencyId()));
                fleetOrg = fleetOrgResult.getData();
            }
        }

        return RespResult.ok().put("data", UserProfileDTO.builder()
            .id(user.getId().toString())
            .avatar(user.getAvatar())
            .name(user.getName())
            .phone(user.getMobile())
            .manager(org.getManager())
            .team(fleetDto != null ? fleetDto.getName() : "")
            .transport(org != null ? org.getName() : "")
            .truckId(truckId)
            .licensePlate(licensePlate)
            .transportTaskId(transportTaskId)
            .userNumber(driverId)
            .build());
    }

    // 查询用户集合
/*@GetMapping({""})
RespResult<List<User>> list(@RequestParam(name = "ids",required = false) List<Long> ids, @RequestParam(name = "stationId",required = false) Long stationId, @RequestParam(name = "name",required = false) String name, @RequestParam(name = "orgId",required = false) Long orgId);*/
    @GetMapping
    public Result<List<User>> list(@RequestParam(name = "ids", required = false) List<Long> ids, @RequestParam(name = "stationId", required = false) Long stationId, @RequestParam(name = "name", required = false) String name, @RequestParam(name = "orgId", required = false) Long orgId) {
        List<User> list = new ArrayList<>();
        for (Long id : ids) {
            Result<User> userResult = this.userApi.get(id);
            User user = userResult.getData();
            list.add(user);
        }
        return success(list);
    }
}
