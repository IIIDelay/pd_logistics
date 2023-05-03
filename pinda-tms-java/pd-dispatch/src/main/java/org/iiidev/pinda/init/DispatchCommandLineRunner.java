package org.iiidev.pinda.init;

import org.iiidev.pinda.entity.ScheduleJobEntity;
import org.iiidev.pinda.mapper.ScheduleJobMapper;
import org.iiidev.pinda.utils.ScheduleUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 项目启动时进行定时任务初始化
 */
@Slf4j
@Component
public class DispatchCommandLineRunner implements CommandLineRunner {
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private ScheduleJobMapper scheduleJobMapper;


    @Override
    public void run(String... args) {
        log.info("定时任务初始化");
        //查询定时任务
        List<ScheduleJobEntity> scheduleJobList = scheduleJobMapper.selectList(null);
        for (ScheduleJobEntity scheduleJob : scheduleJobList) {
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getId());
            //如果不存在，则创建
            if (cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            } else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
    }
}