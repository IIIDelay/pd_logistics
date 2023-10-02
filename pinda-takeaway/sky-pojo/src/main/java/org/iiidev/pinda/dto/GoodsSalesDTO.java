package org.iiidev.pinda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsSalesDTO implements Serializable {
    // 商品名称
    private String name;

    // 销量
    private Integer number;
}
