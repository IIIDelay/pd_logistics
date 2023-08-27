package org.iiidev.pinda.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.iiidev.pinda.exception.BizException;
import org.iiidev.pinda.exception.code.ExceptionCode;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * ReflectHelper
 *
 * @Author IIIDelay
 * @Date 2023/8/27 14:54
 **/
@Slf4j
public class ReflectHelper {
    /**
     * 针对泛型的处理赋值
     *
     * @param source       source
     * @param target       target
     * @param targetFields targetFields
     */
    public static  <IN,OUT>void sameFieldInvokeCopy(IN source, OUT target, Collection<Field> targetFields){
        try {
            if (CollectionUtils.isEmpty(targetFields)) {
                return;
            }
            BeanWrapper targetBeanWrapper = new BeanWrapperImpl(target);
            BeanWrapper sourceBeanWrapper = new BeanWrapperImpl(source);
            for (Field targetField : targetFields) {
                targetField.setAccessible(true);
                PropertyDescriptor sourcePD = sourceBeanWrapper.getPropertyDescriptor(targetField.getName());
                PropertyDescriptor targetPD = targetBeanWrapper.getPropertyDescriptor(targetField.getName());
                Method readMethod = sourcePD.getReadMethod();
                Method writeMethod = targetPD.getWriteMethod();
                writeMethod.invoke(target, readMethod.invoke(source));
            }
        } catch (Exception ex) {
            log.error("指定属性拷贝赋值异常",ex);
            throw BizException.wrap(ExceptionCode.REFLECT_EX, ex);
        }
    }

}
