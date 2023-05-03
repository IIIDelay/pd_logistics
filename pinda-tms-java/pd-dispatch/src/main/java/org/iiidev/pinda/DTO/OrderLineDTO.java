package org.iiidev.pinda.DTO;

import org.iiidev.pinda.entity.CacheLineDetailEntity;
import lombok.Data;

@Data
public class OrderLineDTO {

    private CacheLineDetailEntity cacheLineDetailEntity;

    private OrderClassifyGroupDTO orderClassifyGroupDTO;

    public OrderLineDTO(CacheLineDetailEntity cacheLineDetailEntity,OrderClassifyGroupDTO orderClassifyGroupDTO) {
        this.cacheLineDetailEntity = cacheLineDetailEntity;
        this.orderClassifyGroupDTO = orderClassifyGroupDTO;
    }
}
