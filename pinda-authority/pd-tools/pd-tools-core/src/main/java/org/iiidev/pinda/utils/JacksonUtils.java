package org.iiidev.pinda.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.FailableSupplier;
import org.iiidev.pinda.exception.BizException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * JSON 工具类
 */
@UtilityClass
@Slf4j
public class JacksonUtils {

    public static final String JSON_PARSE_ERR_TEMPLATE = "json parse err,json: %s";
    public static final String JSON_FORMAT_ERR_TEMPLATE = "json format err: %s";
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 解决 LocalDateTime 的序列化
        objectMapper.registerModules(new JavaTimeModule());
    }

    /**
     * 初始化 objectMapper 属性
     * <p>
     * 通过这样的方式，使用 Spring 创建的 ObjectMapper Bean
     *
     * @param objectMapper ObjectMapper 对象
     */
    public static void init(ObjectMapper objectMapper) {
        JacksonUtils.objectMapper = objectMapper;
    }

    public static String toJsonString(Object object) {
        return checkedRun(() -> objectMapper.writeValueAsString(object), JSON_FORMAT_ERR_TEMPLATE);
    }

    public static byte[] toJsonByte(Object object) {
        return checkedRun(() -> objectMapper.writeValueAsBytes(object), JSON_FORMAT_ERR_TEMPLATE);
    }

    public static String toJsonPrettyString(Object object) {
        return checkedRun(() -> objectMapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(object), JSON_FORMAT_ERR_TEMPLATE);
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        return checkedRun(() -> objectMapper.readValue(text, clazz), JSON_PARSE_ERR_TEMPLATE);
    }

    public static <T> T parseObject(String text, Type type) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        return checkedRun(() -> objectMapper.readValue(text, objectMapper.getTypeFactory()
            .constructType(type)), JSON_PARSE_ERR_TEMPLATE);
    }

    /**
     * 将字符串解析成指定类型的对象
     * 使用 {@link #parseObject(String, Class)} 时，在@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) 的场景下，
     * 如果 text 没有 class 属性，则会报错。此时，使用这个方法，可以解决。
     *
     * @param text  字符串
     * @param clazz 类型
     * @return 对象
     */
    public static <T> T parseObject2(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        return JSONUtil.toBean(text, clazz);
    }

    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        return checkedRun(() -> objectMapper.readValue(bytes, clazz), JSON_PARSE_ERR_TEMPLATE);
    }

    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        String errMsg = String.format(JSON_PARSE_ERR_TEMPLATE, text);
        return checkedRun(() -> objectMapper.readValue(text, typeReference), JSON_PARSE_ERR_TEMPLATE);
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        return checkedRun(() -> objectMapper.readValue(text, collectionType), "failed to parse json.");
    }

    public static JsonNode parseTree(String text) {
        return checkedRun(() -> objectMapper.readTree(text), "failed to parse json.");
    }

    public static JsonNode parseTree(byte[] text) {
        return checkedRun(() -> objectMapper.readTree(text), "failed to parse json.");
    }

    public static boolean isJson(String text) {
        return JSONUtil.isTypeJSON(text);
    }

    private static <OUT, EX extends Throwable> OUT checkedRun(FailableSupplier<OUT, EX> supplier, String errMsg) {
        try {
            return supplier.get();
        } catch (Throwable throwable) {
            throw new BizException(errMsg, throwable);
        }
    }

}
