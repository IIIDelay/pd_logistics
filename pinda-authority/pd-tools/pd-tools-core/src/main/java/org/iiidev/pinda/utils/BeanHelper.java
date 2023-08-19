package org.iiidev.pinda.utils;

import cn.hutool.core.lang.Assert;
import org.iiidev.pinda.exception.BizException;
import org.iiidev.pinda.exception.code.ExceptionCode;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;

public class BeanHelper {

    /**
     * copyProperties
     *
     * @param source source
     * @param target target
     * @return OUT
     */
    public static <IN, OUT> OUT copyCopier(IN source, OUT target, boolean existAccessorsAnno) {
        if (existAccessors) {
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
}
