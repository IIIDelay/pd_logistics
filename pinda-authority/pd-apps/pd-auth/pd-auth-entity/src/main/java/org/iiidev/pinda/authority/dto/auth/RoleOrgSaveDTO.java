package org.iiidev.pinda.authority.dto.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel(value = "RoleOrgSaveDTO", description = "角色组织关系")
@NoArgsConstructor
@AllArgsConstructor
public class RoleOrgSaveDTO implements Serializable {

    private static final long serialVersionUID = 1;
    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("组织ID")
    private Long orgId;

    public static class RoleOrgSaveDTOBuilder {
        private Long roleId;
        private Long orgId;

        RoleOrgSaveDTOBuilder() {
        }

        public RoleOrgSaveDTOBuilder roleId(Long roleId) {
            this.roleId = roleId;
            return this;
        }

        public RoleOrgSaveDTOBuilder orgId(Long orgId) {
            this.orgId = orgId;
            return this;
        }

        public RoleOrgSaveDTO build() {
            return new RoleOrgSaveDTO(this.roleId, this.orgId);
        }
    }

}
