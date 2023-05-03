package org.iiidev.pinda.log.event;


import org.iiidev.pinda.log.entity.OptLogDTO;

import org.springframework.context.ApplicationEvent;

/**
 * 系统日志事件
 *
 */
public class SysLogEvent extends ApplicationEvent {

    public SysLogEvent(OptLogDTO source) {
        super(source);
    }
}
