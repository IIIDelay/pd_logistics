package org.iiidev.pinda.authority.dto.auth;

/**
 * 用户角色DTO
 *
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.iiidev.pinda.authority.entity.auth.User;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@ApiModel(value = "UserRoleDTO", description = "用户角色DTO")
public class UserRoleDTO implements Serializable {
    @ApiModelProperty(value = "用户id")
    private List<Long> idList;

    @ApiModelProperty(value = "用户信息")
    private List<User> userList;
}
