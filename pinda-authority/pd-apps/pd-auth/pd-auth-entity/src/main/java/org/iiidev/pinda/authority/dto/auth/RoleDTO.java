package org.iiidev.pinda.authority.dto.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@ApiModel(value = "RoleDTO", description = "角色")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleDTO {
    @ApiModelProperty("ID")
    private Long id;

    @Length(max = 30, message = "角色名称长度不能超过30")
    @TableField(value = "name", condition = "%s LIKE CONCAT('%%',#{%s},'%%')")
    @ApiModelProperty("角色名称")
    @NotEmpty(message = "角色名称不能为空")
    private String name;

    public static class RoleDTOBuilder {
        private Long id;
        private String name;

        RoleDTOBuilder() {
        }

        public RoleDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RoleDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RoleDTO build() {
            return new RoleDTO(this.id, this.name);
        }
    }
}
