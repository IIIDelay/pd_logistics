package org.iiidev.pinda.authority.dto.auth;

import org.iiidev.pinda.authority.entity.auth.Resource;
import org.iiidev.pinda.authority.entity.auth.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "RoleResourceDTO", description = "角色")
@Data
@Builder
public class RoleResourceDTO implements Serializable {
    private static final long serialVersionUID = 1;
    private Long id;
    @Length(max = 30, message = "角色名称长度不能超过30")
    @ApiModelProperty("角色名称")
    private String name;
    @ApiModelProperty("角色编码")
    private String code;
    private List<Resource> resources;

    public RoleResourceDTO(Long id, String name, String code, List<Resource> resources) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.resources = resources;
    }

    /* loaded from: pd-auth-entity-1.0.0.jar:org.iiidev/pinda/authority/dto/auth/RoleResourceDTO$RoleResourceDTOBuilder.class */
    public static class RoleResourceDTOBuilder {
        private Long id;
        private String name;
        private String code;
        private List<Resource> resources;

        RoleResourceDTOBuilder() {
        }

        public RoleResourceDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RoleResourceDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RoleResourceDTOBuilder code(String code) {
            this.code = code;
            return this;
        }

        public RoleResourceDTOBuilder resources(List<Resource> resources) {
            this.resources = resources;
            return this;
        }

        public RoleResourceDTO build() {
            return new RoleResourceDTO(this.id, this.name, this.code, this.resources);
        }

        public String toString() {
            return "RoleResourceDTO.RoleResourceDTOBuilder(id=" + this.id + ", name=" + this.name + ", code=" + this.code + ", resources=" + this.resources + ")";
        }
    }


    public static RoleResourceDTOBuilder builder() {
        return new RoleResourceDTOBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

    public List<Resource> getResources() {
        return this.resources;
    }

    public RoleResourceDTO() {
    }

    public RoleResourceDTO(Role role, List<Resource> resources) {
        this.id = (Long) role.getId();
        this.name = role.getName();
        this.code = role.getCode();
        this.resources = resources;
    }
}
