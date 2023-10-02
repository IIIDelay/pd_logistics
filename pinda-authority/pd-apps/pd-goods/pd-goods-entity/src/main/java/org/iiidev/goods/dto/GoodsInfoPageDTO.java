package org.iiidev.goods.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iiidev.goods.entity.GoodsInfo;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsInfoPageDTO extends GoodsInfo {
    private static final long serialVersionUID = -8663227057290519238L;
    private LocalDateTime startCreateTime;
    private LocalDateTime endCreateTime;
}