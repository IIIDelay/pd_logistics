package org.iiidev.controller;

import org.iiidev.jobs.HelloJob;
import io.swagger.annotations.ApiOperation;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quartz")
public class QuartzController {
    //注入调度器对象
    @Autowired
    private Scheduler scheduler;

    /**
     * 添加定时任务
     * @param bizId
     * @return
     */
    @PostMapping
    @ApiOperation("添加定时任务")
    public String save(String bizId,String cronExpression) throws Exception{
        //构建job信息
        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class).withIdentity(bizId).build();
        jobDetail.getJobDataMap().put("bizId",bizId);

        //表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression)
                .withMisfireHandlingInstructionDoNothing();

        //按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(bizId).withSchedule(scheduleBuilder).build();

        //关联任务和触发器
        scheduler.scheduleJob(jobDetail,trigger);
        return "OK";
    }
    /**
     * 暂停定时任务
     * @param bizId
     * @return
     */
    @PutMapping("/pause/{bizId}")
    @ApiOperation("暂停定时任务")
    public String pause(@PathVariable String bizId) throws Exception{
        JobKey jobKey = JobKey.jobKey(bizId);
        scheduler.pauseJob(jobKey);
        return "OK";
    }

    /**
     * 恢复定时任务
     * @param bizId
     * @return
     */
    @PutMapping("/resume/{bizId}")
    @ApiOperation("恢复定时任务")
    public String resume(@PathVariable String bizId) throws Exception{
        JobKey jobKey = JobKey.jobKey(bizId);
        scheduler.resumeJob(jobKey);
        return "OK";
    }
    /**
     * 删除定时任务
     * @param bizId
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除定时任务")
    public String delete(String bizId) throws Exception{
        JobKey jobKey = JobKey.jobKey(bizId);
        scheduler.deleteJob(jobKey);
        return "OK";
    }
    /**
     * 立即执行定时任务
     * @param bizId
     * @return
     */
    @PutMapping("/run/{bizId}")
    @ApiOperation("立即执行定时任务")
    public String run(@PathVariable String bizId) throws Exception{
        JobKey jobKey = JobKey.jobKey(bizId);
        scheduler.triggerJob(jobKey);
        return "OK";
    }
    /**
     * 更新定时任务
     * @param bizId
     * @param cronExpression
     * @return
     * @throws Exception
     */
    @PutMapping
    @ApiOperation("更新定时任务")
    public String update(String bizId,String cronExpression) throws Exception{
        TriggerKey triggerKey = TriggerKey.triggerKey(bizId);

        //表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression)
                .withMisfireHandlingInstructionDoNothing();

        //获取触发器对象
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        //按新的cronExpression表达式重新构建trigger
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

        scheduler.rescheduleJob(triggerKey, trigger);
        return "OK";
    }
}