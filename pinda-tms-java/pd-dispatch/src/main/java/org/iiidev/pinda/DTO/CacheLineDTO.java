package org.iiidev.pinda.DTO;

import org.iiidev.pinda.entity.CacheLineEntity;
import lombok.Data;

import java.util.List;

@Data
public class CacheLineDTO extends CacheLineEntity {

    private List<CacheLineDetailDTO> cacheLineDetailDTOS;

}
