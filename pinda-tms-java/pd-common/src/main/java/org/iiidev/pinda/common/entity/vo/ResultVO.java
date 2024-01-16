package org.iiidev.pinda.common.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ResultVO
 *
 * @Author IIIDelay
 * @Date 2023/12/30 9:18
 **/
@Data
public class ResultVO<T> implements Serializable {
    private static final long serialVersionUID = 4102093558563359227L;

    private int code;

    private String standCode;

    private String message;

    private T data;
}