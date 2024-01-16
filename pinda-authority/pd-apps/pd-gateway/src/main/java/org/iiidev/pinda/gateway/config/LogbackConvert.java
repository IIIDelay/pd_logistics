package org.iiidev.pinda.gateway.config;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.Data;

/**
 * LogbackConvert
 *
 * @Author IIIDelay
 * @Date 2024/1/16 21:35
 **/
@Data
public class LogbackConvert extends MessageConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return super.convert(event);
    }
}