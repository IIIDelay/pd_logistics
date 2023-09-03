package org.iiidev.pinda.authority.entity.core;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.iiidev.pinda.base.entity.Entity;

import javax.validation.constraints.NotEmpty;

/**
 * <p>
 * 实体类
 * 组织
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("pd_core_org")
@ApiModel(value = "Org", description = "组织")
public class Org extends Entity<Long> {
    private static final long serialVersionUID = 1;

    @Length(max = 255, message = "名称长度不能超过255")
    @TableField(value = "name", condition = "%s LIKE CONCAT('%%',#{%s},'%%')")
    @ApiModelProperty("名称")
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Length(max = 255, message = "简称长度不能超过255")
    @TableField(value = "abbreviation", condition = "%s LIKE CONCAT('%%',#{%s},'%%')")
    @ApiModelProperty("简称")
    private String abbreviation;

    @TableField("parent_id")
    @ApiModelProperty("父级ID")
    private Long parentId;

    @TableField(exist = false)
    @ApiModelProperty("父级名称")
    private String parentName;

    @TableField(value = "org_type", exist = false)
    @ApiModelProperty("部门类型 1为分公司，2为一级转运中心 3为二级转运中心 4为网点")
    private Integer orgType;

    @TableField(value = "province_id", exist = false)
    @ApiModelProperty("所属省份id")
    private Long provinceId;

    @TableField(exist = false)
    @ApiModelProperty("所属省份名称")
    private String provinceName;

    @TableField(value = "city_id", exist = false)
    @ApiModelProperty("所属城市id")
    private Long cityId;

    @TableField(exist = false)
    @ApiModelProperty("所属城市名称")
    private String cityName;

    @TableField(value = "county_id", exist = false)
    @ApiModelProperty("所属区县id")
    private Long countyId;

    @TableField(exist = false)
    @ApiModelProperty("所属区县名称")
    private String countyName;

    @Length(max = 255, message = "详细地址长度不能超过255")
    @TableField(value = "address", condition = "%s LIKE CONCAT('%%',#{%s},'%%')", exist = false)
    @ApiModelProperty("详细地址")
    private String address;

    @Length(max = 255, message = "经度长度不能超过255")
    @TableField(value = "longitude", condition = "%s LIKE CONCAT('%%',#{%s},'%%')", exist = false)
    @ApiModelProperty("经度")
    private String longitude;

    @Length(max = 255, message = "纬度长度不能超过255")
    @TableField(value = "latitude", condition = "%s LIKE CONCAT('%%',#{%s},'%%')", exist = false)
    @ApiModelProperty("纬度")
    private String latitude;

    @Length(max = 255, message = "联系电话长度不能超过255")
    @TableField(value = "contract_number", condition = "%s LIKE CONCAT('%%',#{%s},'%%')", exist = false)
    @ApiModelProperty("联系电话")
    private String contractNumber;

    @TableField(value = "manager_id", exist = false)
    @ApiModelProperty("负责人id")
    private Long managerId;

    @TableField(exist = false)
    @ApiModelProperty("负责人名称")
    private String manager;

    @Length(max = 255, message = "营业时间长度不能超过255")
    @TableField(value = "business_hours", condition = "%s LIKE CONCAT('%%',#{%s},'%%')", exist = false)
    @ApiModelProperty("营业时间")
    private String businessHours;

    @Length(max = 255, message = "树结构长度不能超过255")
    @TableField(value = "tree_path", condition = "%s LIKE CONCAT('%%',#{%s},'%%')")
    @ApiModelProperty("树结构")
    private String treePath;

    @TableField("sort_value")
    @ApiModelProperty("排序")
    private Integer sortValue;

    @TableField("status")
    @ApiModelProperty("状态")
    private Boolean status;

    @Length(max = 255, message = "描述长度不能超过255")
    @TableField(value = "describe_", condition = "%s LIKE CONCAT('%%',#{%s},'%%')")
    @ApiModelProperty("描述")
    private String describe;
}
