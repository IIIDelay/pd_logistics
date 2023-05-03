package org.iiidev.pinda.common.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据类型
 */
@ApiModel(value = "公用返回值", description = "公用返回值")
@ApiResponses({
        @ApiResponse(code = 0, message = "msg")
})
@Slf4j
public class RespResult extends HashMap<String, Object> implements Serializable {
    private static final long serialVersionUID = 1L;

    public RespResult() {
        put("code", 0);
        put("msg", "success");
    }

    public static RespResult error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
    }

    public static RespResult error(String msg) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public static RespResult error(int code, String msg) {
        RespResult respResult = new RespResult();
        respResult.put("code", code);
        respResult.put("msg", msg);
        return respResult;
    }

    public static RespResult ok(String msg) {
        RespResult respResult = new RespResult();
        respResult.put("msg", msg);
        return respResult;
    }

    public static RespResult ok(Map<String, Object> map) {
        RespResult respResult = new RespResult();
        respResult.putAll(map);
        return respResult;
    }

    public static RespResult ok() {
        return new RespResult();
    }

    @Override
    public RespResult put(String key, Object value) {
        super.put(key, value);
        log.info("key:{} value:{}", key, value);
        return this;
    }
}
