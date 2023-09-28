package org.iiidev.pinda.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.DTO.DriverJobDTO;
import org.iiidev.pinda.DTO.webManager.DriverJobQueryDTO;
import org.iiidev.pinda.authority.api.AreaApi;
import org.iiidev.pinda.authority.api.OrgApi;
import org.iiidev.pinda.authority.api.UserApi;
import org.iiidev.pinda.common.utils.PageResponse;
import org.iiidev.pinda.feign.OrderFeign;
import org.iiidev.pinda.feign.TransportOrderFeign;
import org.iiidev.pinda.feign.TransportTaskFeign;
import org.iiidev.pinda.feign.transportline.TransportTripsFeign;
import org.iiidev.pinda.feign.truck.TruckFeign;
import org.iiidev.pinda.feign.webManager.WebManagerFeign;
import org.iiidev.pinda.util.BeanUtil;
import org.iiidev.pinda.vo.work.DriverJobVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 司机作业单相关API
 *
 */
@RestController
@Slf4j
@Api(tags = "司机作业单相关API")
@RequestMapping("driver-job-manager")
@RequiredArgsConstructor
public class DriverJobController {
    private final WebManagerFeign webManagerFeign;
    private final TransportTaskFeign transportTaskFeign;
    private final TransportTripsFeign transportTripsFeign;
    private final OrgApi orgApi;
    private final UserApi userApi;
    private final TruckFeign truckFeign;
    private final TransportOrderFeign transportOrderFeign;
    private final OrderFeign orderFeign;
    private final AreaApi areaApi;

    @ApiOperation(value = "获取司机作业单分页数据")
    @PostMapping("/page")
    public PageResponse<DriverJobVo> findByPage(@RequestBody DriverJobVo vo) {
        DriverJobQueryDTO dto = new DriverJobQueryDTO();
        if (vo != null) {
            dto.setPage(vo.getPage());
            dto.setPageSize(vo.getPageSize());
            if (vo.getDriver() != null) {
                dto.setDriverName(vo.getDriver().getName());
            }
            if (vo.getTaskTransport() != null) {
                dto.setTaskTransportId(vo.getTaskTransport().getId());
            }
            dto.setStatus(vo.getStatus());
            dto.setId(vo.getId());
        }
        PageResponse<DriverJobDTO> dtoPageResponse = webManagerFeign.findDriverJobByPage(dto);
        List<DriverJobDTO> dtoList = dtoPageResponse.getItems();
        List<DriverJobVo> voList = dtoList.stream().map(driverJobDTO -> BeanUtil.parseDriverJobDTO2Vo(driverJobDTO, transportTripsFeign, orgApi, userApi, truckFeign, transportOrderFeign, orderFeign, areaApi, transportTaskFeign)).collect(Collectors.toList());
        return PageResponse.<DriverJobVo>builder().items(voList).pagesize(vo.getPageSize()).page(vo.getPage()).counts(dtoPageResponse.getCounts()).pages(dtoPageResponse.getPages()).build();
    }
}
