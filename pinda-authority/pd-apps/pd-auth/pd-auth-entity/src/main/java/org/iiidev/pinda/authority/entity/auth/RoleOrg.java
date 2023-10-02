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

/**
 * <p>
 * 实体类
 * 角色组织关系
 * </p>
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("pd_auth_role_org")
@ApiModel(value = "RoleOrg", description = "角色组织关系")
public class RoleOrg extends SuperEntity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     * #pd_auth_role
     */
    @ApiModelProperty(value = "角色ID")
    @TableField("role_id")
    private Long roleId;

    /**
     * 部门ID
     * #pd_core_org
     */
    @ApiModelProperty(value = "部门ID")
    @TableField("org_id")
    private Long orgId;


}
