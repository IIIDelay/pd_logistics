package org.iiidev.pinda.service;

import org.iiidev.pinda.DTO.OrderLineSimpleDTO;
import org.iiidev.pinda.DTO.OrderLineTripsTruckDriverDTO;

import java.util.List;

/**
 * 车辆调度
 */
public interface ITaskTripsSchedulingService {
    /**
     * 执行
     *
     * @param orderLineSimpleDTOS
     * @param businessId
     * @param jobId
     * @param logId
     * @return
     */
    List<OrderLineTripsTruckDriverDTO> execute(List<OrderLineSimpleDTO> orderLineSimpleDTOS, String businessId, String jobId, String logId, String agencyId);
}
