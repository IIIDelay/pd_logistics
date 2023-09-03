package org.iiidev.pinda.authority.dto.auth;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import org.iiidev.pinda.authority.entity.auth.Menu;
import org.iiidev.pinda.model.ITreeNode;

import java.util.List;

/**
 * 树形菜单 DTO
 *
 */
@ToString(callSuper = true)
@ApiModel(value = "ResourceTreeDTO", description = "资源树")
@Data
public class MenuTreeDTO extends Menu implements ITreeNode<MenuTreeDTO, Long> {
    private List<MenuTreeDTO> children;

    private String label;
}
