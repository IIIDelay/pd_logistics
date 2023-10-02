package org.iiidev.pinda.authority.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.iiidev.pinda.authority.enumeration.auth.Sex;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * UserVO
 *
 * @Author IIIDelay
 * @Date 2023/10/2 18:39
 **/
@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 3126003621606481284L;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    @NotEmpty(message = "账号不能为空")
    private String account;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @NotEmpty(message = "姓名不能为空")
    private String name;

    /**
     * 组织ID
     * #pd_core_org
     */
    @ApiModelProperty(value = "组织ID")
    private Long orgId;

    /**
     * 岗位ID
     * #pd_core_station
     */
    @ApiModelProperty(value = "岗位ID")
    private Long stationId;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 手机
     */
    @ApiModelProperty(value = "手机")
    private String mobile;

    /**
     * 性别
     * #Sex{W:女;M:男;N:未知}
     */
    @ApiModelProperty(value = "性别")
    private Sex sex;

    /**
     * 启用状态 1启用 0禁用
     */
    @ApiModelProperty(value = "启用状态 1启用 0禁用")
    private Boolean status;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String avatar;

    /**
     * 工作描述
     * 比如:   市长、管理员、局长等等   用于登陆展示
     */
    @ApiModelProperty(value = "工作描述")
    private String workDescribe;

    /**
     * 最后一次输错密码时间
     */
    @ApiModelProperty(value = "最后一次输错密码时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime passwordErrorLastTime;

    /**
     * 密码错误次数
     */
    @ApiModelProperty(value = "密码错误次数")
    private Integer passwordErrorNum;

    /**
     * 密码过期时间
     */
    @ApiModelProperty(value = "密码过期时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime passwordExpireTime;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 最后登录时间
     */
    @ApiModelProperty(value = "最后登录时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty(value = "最后修改时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime updateTime;

    @ApiModelProperty(value = "最后修改人ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Long updateUser;

    @ApiModelProperty(value = "主键")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected Long id;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime createTime;

    @ApiModelProperty(value = "创建人ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected Long createUser;

}