package org.iiidev.pinda.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户
 */
@Data
@TableName("pd_member")
public class Member implements Serializable{

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 认证id
     */
    private String authId;
    /**
     * 身份证号
     */
    private String idCardNo;
    /**
     * 身份证号是否认证 1认证
     */
    private Integer idCardNoVerify;
    /**
     * 手机号
     */
    private String phone;

}