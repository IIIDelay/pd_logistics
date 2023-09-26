package org.iiidev.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 自定义Job
 */
@Slf4j
public class HelloJob extends QuartzJobBean {
    // 任务触发时执行此方法
    @Override
    protected void executeInternal(JobExecutionContext context) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = dateTimeFormatter.format(now);

        Object bizId = context.getJobDetail().getJobDataMap().get("bizId");
        log.info("自定义Job...bizId: {}, 执行时间: {}", bizId, time);
    }
}