package org.iiidev.pinda.DTO;

import org.iiidev.pinda.entity.CacheLineDetailEntity;
import lombok.Data;

import java.util.List;

@Data
public class OrderLineSimpleDTO {

    private CacheLineDetailEntity cacheLineDetailEntity;

    private List<OrderClassifyGroupDTO> orderClassifyGroupDTOS;

    public OrderLineSimpleDTO(CacheLineDetailEntity cacheLineDetailEntity, List<OrderClassifyGroupDTO> orderClassifyGroupDTOS) {
        this.cacheLineDetailEntity = cacheLineDetailEntity;
        this.orderClassifyGroupDTOS = orderClassifyGroupDTOS;
    }
}
