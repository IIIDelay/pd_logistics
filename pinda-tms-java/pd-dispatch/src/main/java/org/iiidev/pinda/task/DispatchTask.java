package org.iiidev.pinda.task;
import org.iiidev.pinda.DTO.OrderClassifyGroupDTO;
import org.iiidev.pinda.service.ITaskOrderClassifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 智能调度组件
 */
@Slf4j
@Component("dispatchTask")
public class DispatchTask{
    @Autowired
    private ITaskOrderClassifyService taskOrderClassifyService;
    /**
     * 智能调度
     * @param businessId
     * @param params
     * @param jobId
     * @param logId
     */
    public void run(String businessId, String params, String jobId, String logId) {
        log.info("智能调度正在执行，参数为: {},{},{},{}", businessId, params, jobId, logId);
        List<OrderClassifyGroupDTO> classifyGroupDTOS = taskOrderClassifyService.execute(businessId, jobId, logId);
    }
}