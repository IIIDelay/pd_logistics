package org.iiidev.pinda.authority.dto.auth;

import org.iiidev.pinda.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel(value = "RoleOrgUpdateDTO", description = "角色组织关系")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleOrgUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1;

    @NotNull(message = "id不能为空", groups = {SuperEntity.Update.class})
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("组织ID")
    private Long orgId;

    public static class RoleOrgUpdateDTOBuilder {
        private Long id;

        private Long roleId;

        private Long orgId;

        RoleOrgUpdateDTOBuilder() {
        }

        public RoleOrgUpdateDTO build() {
            return new RoleOrgUpdateDTO(this.id, this.roleId, this.orgId);
        }
    }


    public static RoleOrgUpdateDTOBuilder builder() {
        return new RoleOrgUpdateDTOBuilder();
    }

}
