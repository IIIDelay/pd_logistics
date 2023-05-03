package org.iiidev.pinda.controller;

import com.alibaba.fastjson.JSON;
import org.iiidev.pinda.common.utils.RespResult;
import org.iiidev.pinda.entity.LocationEntity;
import org.iiidev.pinda.service.KafkaSender;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "车辆轨迹服务")
@RequestMapping("netty")
@Slf4j
public class NettyController {
    @PostMapping(value = "/push")
    public RespResult push(@RequestBody LocationEntity locationEntity) {
        String message = JSON.toJSONString(locationEntity);
        log.info("HTTP 方式推送位置信息：{}", message);
        KafkaSender.send(KafkaSender.MSG_TOPIC, message);
        return RespResult.ok();
    }
}