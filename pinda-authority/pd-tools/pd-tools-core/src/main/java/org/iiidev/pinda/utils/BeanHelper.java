package org.iiidev.pinda.utils;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.iiidev.pinda.exception.BizException;
import org.iiidev.pinda.exception.code.ExceptionCode;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class BeanHelper {

    /**
     * copyProperties
     *
     * @param source source
     * @param target target
     * @return OUT
     */
    public static <IN, OUT> OUT copyCopier(IN source, OUT target, boolean existAccessorsAnno) {
        if (existAccessorsAnno) {
            return copyProperties(source, target);
        }
        Assert.notNull(source, () -> BizException.unaryOf(ExceptionCode.ILLEGALA_ARGUMENT_EX, "input source param"));
        Assert.notNull(target, () -> BizException.unaryOf(ExceptionCode.ILLEGALA_ARGUMENT_EX, "input target param"));

        BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
        beanCopier.copy(source, target, null);
        return target;
    }

    public static <IN, OUT> OUT copyProperties(IN source, OUT target, String... ignoreProperties) {
        Assert.notNull(source, () -> BizException.unaryOf(ExceptionCode.ILLEGALA_ARGUMENT_EX, "input source param"));
        Assert.notNull(target, () -> BizException.unaryOf(ExceptionCode.ILLEGALA_ARGUMENT_EX, "input target param"));
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }

    /**
     * mapList
     *
     * @param source             source
     * @param target             target
     * @param existAccessorsAnno existAccessorsAnno
     * @return List<OUT>
     */
    public static <IN, OUT> List<OUT> mapList(List<IN> source, Class<OUT> target, boolean existAccessorsAnno) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        try {
            List<OUT> outList = new ArrayList<>();
            for (IN in : source) {
                OUT out = copyCopier(in, target.newInstance(), existAccessorsAnno);
                outList.add(out);
            }
            return outList;
        } catch (Exception ex) {
            log.error("实体类属性转换映射异常", ex);
            throw new BizException("实体类属性转换映射异常", ex);
        }
    }
}
