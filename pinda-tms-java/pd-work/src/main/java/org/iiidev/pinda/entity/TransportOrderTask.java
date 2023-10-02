package org.iiidev.pinda.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 运单与运输任务关联表
 * </p>
 */
@Data
@TableName("pd_transport_order_task")
public class TransportOrderTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 运单Id
     */
    private String transportOrderId;

    /**
     * 运输任务Id
     */
    private String transportTaskId;
}
