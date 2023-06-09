package org.iiidev.pinda.controller;

import org.iiidev.pinda.DTO.DriverJobDTO;
import org.iiidev.pinda.DTO.TaskPickupDispatchDTO;
import org.iiidev.pinda.DTO.TaskTransportDTO;
import org.iiidev.pinda.DTO.TransportOrderDTO;
import org.iiidev.pinda.DTO.webManager.DriverJobQueryDTO;
import org.iiidev.pinda.DTO.webManager.TaskPickupDispatchQueryDTO;
import org.iiidev.pinda.DTO.webManager.TaskTransportQueryDTO;
import org.iiidev.pinda.DTO.webManager.TransportOrderQueryDTO;
import org.iiidev.pinda.common.utils.PageResponse;
import org.iiidev.pinda.service.WebManagerService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "管理后台聚合平台")
@RestController
@RequestMapping("webManager")
public class WebManagerController {
    @Autowired
    private WebManagerService webManagerService;

    @PostMapping("driverJob/page")
    PageResponse<DriverJobDTO> findDriverJobByPage(@RequestBody DriverJobQueryDTO dto) {
        return webManagerService.findDriverJobByPage(dto);
    }

    @PostMapping("taskPickupDispatchJob/page")
    PageResponse<TaskPickupDispatchDTO> findTaskPickupDispatchJobByPage(@RequestBody TaskPickupDispatchQueryDTO dto) {
        return webManagerService.findTaskPickupDispatchJobByPage(dto);
    }

    @PostMapping("transportOrder/page")
    PageResponse<TransportOrderDTO> findTransportOrderByPage(@RequestBody TransportOrderQueryDTO dto) {
        return webManagerService.findTransportOrderByPage(dto);
    }

    @PostMapping("taskTransport/page")
    PageResponse<TaskTransportDTO> findTransportOrderByPage(@RequestBody TaskTransportQueryDTO dto) {
        return webManagerService.findTaskTransportByPage(dto);
    }
}
