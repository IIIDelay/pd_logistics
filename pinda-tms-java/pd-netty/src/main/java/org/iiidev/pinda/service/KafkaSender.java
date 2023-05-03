package org.iiidev.pinda.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
@Slf4j
public class KafkaSender {
    public final static String MSG_TOPIC = "ip_msg";
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static KafkaTemplate<String, String> template;

    @PostConstruct
    public void init() {
        KafkaSender.template = this.kafkaTemplate;
    }

    //发送消息到kafka队列
    public static boolean send(String topic, String message) {
        try {
            template.send(topic, message);
            log.info("消息发送成功：{} , {}", topic, message);
        } catch (Exception e) {
            log.error("消息发送失败：{} , {}", topic, message, e);
            return false;
        }
        return true;
    }

}