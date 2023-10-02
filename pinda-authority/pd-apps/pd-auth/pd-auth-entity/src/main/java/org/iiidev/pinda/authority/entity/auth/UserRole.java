package org.iiidev.pinda.authority.entity.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iiidev.pinda.base.entity.SuperEntity;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 实体类
 * 角色分配
 * 账号角色绑定
 * </p>
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("pd_auth_user_role")
@ApiModel(value = "UserRole", description = "角色分配")
public class UserRole extends SuperEntity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     * #pd_auth_role
     */
    @ApiModelProperty(value = "角色ID")
    @NotNull(message = "角色ID不能为空")
    @TableField("role_id")
    private Long roleId;

    /**
     * 用户ID
     * #c_core_accou
     */
    @ApiModelProperty(value = "用户ID")
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

}
