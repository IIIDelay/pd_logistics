package org.iiidev.pinda.authority.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * LogRecordVO
 *
 * @Author IIIDelay
 * @Date 2023/10/2 16:17
 **/
@Data
@Builder
public class LogRecordVO implements Serializable {
    private static final long serialVersionUID = -1256230163372139071L;

    private Long totalVisitCount;
    private Long todayVisitCount;
    private Long todayIp;
    private List<Map<String, Object>> lastTenVisitCount;
    private List<Map<String, Object>> lastTenUserVisitCount;
    private List<Map<String, Object>> browserCount;
    private List<Map<String, Object>> operatingSystemCount;
}