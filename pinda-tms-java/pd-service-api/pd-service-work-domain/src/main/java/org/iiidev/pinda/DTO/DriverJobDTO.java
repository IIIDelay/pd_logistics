package org.iiidev.pinda.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 司机作业单
 */
@Data
public class DriverJobDTO implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;

    /**
     * 起始机构id
     */
    private String startAgencyId;

    /**
     * 目的机构id
     */
    private String endAgencyId;

    /**
     * 作业状态，1为待执行（对应 待提货）、2为进行中（对应在途）、3为改派（对应 已交付）、4为已完成（对应 已交付）、5为已作废
     */
    private Integer status;

    /**
     * 司机id
     */
    private String driverId;

    /**
     * 运输任务id
     */
    private String taskTransportId;

    /**
     * 提货对接人
     */
    private String startHandover;

    /**
     * 交付对接人
     */
    private String finishHandover;

    /**
     * 计划发车时间
     */
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    private LocalDateTime planDepartureTime;

    /**
     * 实际发车时间
     */
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    private LocalDateTime actualDepartureTime;

    /**
     * 计划到达时间
     */
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    private LocalDateTime planArrivalTime;

    /**
     * 实际到达时间
     */
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    private LocalDateTime actualArrivalTime;

    /**
     * 创建时间
     */
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    private LocalDateTime createTime;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 页尺寸
     */
    private Integer pageSize;
}
