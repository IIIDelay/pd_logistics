package org.iiidev.pinda.authority.dto.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.iiidev.pinda.auth.utils.Token;

import java.io.Serializable;
import java.util.List;

/**
 * 登录返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Builder
@ApiModel(value = "LoginDTO", description = "登录信息")
public class LoginDTO implements Serializable {
    private static final long serialVersionUID = -3124612657759050173L;

    @ApiModelProperty(value = "用户信息")
    private UserDTO user;

    @ApiModelProperty(value = "token")
    private Token token;

    @ApiModelProperty(value = "权限列表")
    private List<String> permissionsList;
}
