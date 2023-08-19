package org.iiidev.pinda.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 基础实体
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class Entity<T> extends SuperEntity<T> {

    public static final String UPDATE_TIME = "updateTime";
    public static final String UPDATE_USER = "updateUser";

    @ApiModelProperty(value = "最后修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    protected LocalDateTime updateTime;

    @ApiModelProperty(value = "最后修改人ID")
    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    protected T updateUser;
}
