package org.iiidev.pinda.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaSender implements ApplicationContextAware {
    public final static String MSG_TOPIC = "ip_msg";

    private static KafkaTemplate<String, String> template;

    // 发送消息到kafka队列
    public static boolean send(String topic, String message) {
        try {
            template.send(topic, message);
            log.info("消息发送成功: {} , {}", topic, message);
        } catch (Exception e) {
            log.error("消息发送失败: {} , {}", topic, message, e);
            return false;
        }
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        KafkaTemplate<String, String> kafkaTemplate = applicationContext.getBean(KafkaTemplate.class);
        this.template = kafkaTemplate;
    }
}