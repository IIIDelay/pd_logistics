package org.iiidev.pinda.common.handler;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.exception.BizException;
import org.iiidev.pinda.exception.code.ExceptionCode;
import org.iiidev.pinda.utils.StrPool;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通过继承此类, 进行全局异常控制
 */
@Slf4j
public abstract class DefaultGlobalExceptionHandler {
    @ExceptionHandler(BizException.class)
    public Result<String> bizException(BizException ex, HttpServletRequest request) {
        log.error("BizException:", ex);
        return Result.result(ex.getCode(), StrPool.EMPTY, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result httpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.error("HttpMessageNotReadableException:", ex);
        String message = ex.getMessage();
        if (StringUtils.containsAny(message, "Could not read document:")) {
            String msg = String.format("无法正确的解析json类型的参数: %s", StrUtil.subBetween(message, "Could not read document:", " at "));
            return Result.result(ExceptionCode.PARAM_EX.getCode(), StrPool.EMPTY, msg, request.getRequestURI());
        }
        return Result.result(ExceptionCode.PARAM_EX.getCode(), StrPool.EMPTY, ExceptionCode.PARAM_EX.getMsg(), request.getRequestURI());
    }

    @ExceptionHandler(BindException.class)
    public Result bindException(BindException ex, HttpServletRequest request) {
        log.error("BindException:", ex);
        String msgs = ex.getBindingResult().getFieldError().getDefaultMessage();
        if (StrUtil.isNotEmpty(msgs)) {
            return Result.result(ExceptionCode.PARAM_EX.getCode(), StrPool.EMPTY, msgs, request.getRequestURI());
        }
        StringBuilder msg = new StringBuilder();
        List<FieldError> fieldErrors = ex.getFieldErrors();
        fieldErrors.forEach(oe ->
            msg.append("参数:[")
                .append(oe.getObjectName())
                .append(".")
                .append(oe.getField())
                .append("]的传入值:[")
                .append(oe.getRejectedValue())
                .append("]与预期的字段类型不匹配.")
        );
        return Result.result(ExceptionCode.PARAM_EX.getCode(), StrPool.EMPTY, msg.toString(), request.getRequestURI());
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.error("MethodArgumentTypeMismatchException:", ex);
        StringBuilder msg = new StringBuilder("参数: [")
            .append(ex.getName())
            .append("]的传入值: [").append(ex.getValue())
            .append("]与预期的字段类型: [")
            .append(ex.getRequiredType().getName())
            .append("]不匹配");
        return Result.result(ExceptionCode.PARAM_EX.getCode(), StrPool.EMPTY, msg.toString(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalStateException.class)
    public Result illegalStateException(IllegalStateException ex, HttpServletRequest request) {
        log.error("IllegalStateException:", ex);
        return Result.result(ExceptionCode.ILLEGALA_ARGUMENT_EX.getCode(), StrPool.EMPTY, ExceptionCode.ILLEGALA_ARGUMENT_EX.getMsg(), request.getRequestURI());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result missingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.error("MissingServletRequestParameterException:", ex);
        StringBuilder msg = new StringBuilder();
        msg.append("缺少必须的[")
            .append(ex.getParameterType())
            .append("]类型的参数[")
            .append(ex.getParameterName())
            .append("]");
        return Result.result(ExceptionCode.ILLEGALA_ARGUMENT_EX.getCode(), StrPool.EMPTY, msg.toString(), request.getRequestURI());
    }

    @ExceptionHandler(NullPointerException.class)
    public Result nullPointerException(NullPointerException ex, HttpServletRequest request) {
        log.error("NullPointerException:", ex);
        return Result.result(ExceptionCode.NULL_POINT_EX.getCode(), StrPool.EMPTY, ExceptionCode.NULL_POINT_EX.getMsg(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result illegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("IllegalArgumentException:", ex);
        return Result.result(ExceptionCode.ILLEGALA_ARGUMENT_EX.getCode(), StrPool.EMPTY, ExceptionCode.ILLEGALA_ARGUMENT_EX.getMsg(), request.getRequestURI());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.error("HttpMediaTypeNotSupportedException:", ex);
        MediaType contentType = ex.getContentType();
        if (contentType != null) {
            StringBuilder msg = new StringBuilder();
            msg.append("请求类型(Content-Type)[").append(contentType).append("] 与实际接口的请求类型不匹配");
            return Result.result(ExceptionCode.MEDIA_TYPE_EX.getCode(), StrPool.EMPTY, msg.toString(), request.getRequestURI());
        }
        return Result.result(ExceptionCode.MEDIA_TYPE_EX.getCode(), StrPool.EMPTY, "无效的Content-Type类型", request.getRequestURI());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public Result missingServletRequestPartException(MissingServletRequestPartException ex, HttpServletRequest request) {
        log.error("MissingServletRequestPartException:", ex);
        return Result.result(ExceptionCode.REQUIRED_FILE_PARAM_EX.getCode(), StrPool.EMPTY, ExceptionCode.REQUIRED_FILE_PARAM_EX.getMsg(), request.getRequestURI());
    }

    /**
     * servlet异常
     *
     * @param ex      ex
     * @param request request
     * @return Result
     */
    @ExceptionHandler(ServletException.class)
    public Result servletException(ServletException ex, HttpServletRequest request) {
        log.error("ServletException:", ex);
        String msg = "UT010016: Not a multi part request";
        if (msg.equalsIgnoreCase(ex.getMessage())) {
            return Result.result(ExceptionCode.REQUIRED_FILE_PARAM_EX.getCode(), StrPool.EMPTY, ExceptionCode.REQUIRED_FILE_PARAM_EX.getMsg());
        }
        return Result.result(ExceptionCode.SYSTEM_BUSY.getCode(), StrPool.EMPTY, ex.getMessage(), request.getRequestURI());
    }

    /**
     * 文件上传异常
     *
     * @param ex      ex
     * @param request request
     * @return Result
     */
    @ExceptionHandler(MultipartException.class)
    public Result multipartException(MultipartException ex, HttpServletRequest request) {
        log.error("MultipartException:", ex);
        return Result.result(ExceptionCode.REQUIRED_FILE_PARAM_EX.getCode(), StrPool.EMPTY, ExceptionCode.REQUIRED_FILE_PARAM_EX.getMsg(), request.getRequestURI());
    }

    /**
     * constraintViolationException jsr 规范中的验证异常
     *
     * @param ex      ex
     * @param request request
     * @return Result<String>
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> constraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        log.error("ConstraintViolationException:", ex);
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        return Result.result(ExceptionCode.BASE_VALID_PARAM.getCode(), StrPool.EMPTY, message, request.getRequestURI());
    }

    /**
     * methodArgumentNotValidException spring 封装的参数验证异常， 在controller中没有写result参数时，会进入
     *
     * @param ex      ex
     * @param request request
     * @return Object
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object methodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("MethodArgumentNotValidException:", ex);
        return Result.result(ExceptionCode.BASE_VALID_PARAM.getCode(), StrPool.EMPTY,
            ex.getBindingResult().getFieldError().getDefaultMessage(), request.getRequestURI());
    }

    /**
     * 其他异常
     *
     * @param ex      ex
     * @param request request
     * @return Result<String>
     */
    @ExceptionHandler(Exception.class)
    public Result<String> otherExceptionHandler(Exception ex, HttpServletRequest request) {
        log.error("Exception:", ex);
        if (ex.getCause() instanceof BizException) {
            return this.bizException((BizException) ex.getCause(), request);
        }
        return Result.result(ExceptionCode.SYSTEM_BUSY.getCode(), StrPool.EMPTY, ExceptionCode.SYSTEM_BUSY.getMsg(), request.getRequestURI());
    }


    /**
     * 返回状态码:405
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public Result<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.error("HttpRequestMethodNotSupportedException:", ex);
        return Result.result(ExceptionCode.METHOD_NOT_ALLOWED.getCode(), StrPool.EMPTY, ExceptionCode.METHOD_NOT_ALLOWED.getMsg(), request.getRequestURI());
    }


    @ExceptionHandler(PersistenceException.class)
    public Result<String> persistenceException(PersistenceException ex, HttpServletRequest request) {
        log.error("PersistenceException:", ex);
        if (ex.getCause() instanceof BizException) {
            BizException cause = (BizException) ex.getCause();
            return Result.result(cause.getCode(), StrPool.EMPTY, cause.getMessage());
        }
        return Result.result(ExceptionCode.SQL_EX.getCode(), StrPool.EMPTY, ExceptionCode.SQL_EX.getMsg(), request.getRequestURI());
    }

    @ExceptionHandler(MyBatisSystemException.class)
    public Result<String> myBatisSystemException(MyBatisSystemException ex, HttpServletRequest request) {
        log.error("PersistenceException:", ex);
        if (ex.getCause() instanceof PersistenceException) {
            return this.persistenceException((PersistenceException) ex.getCause(), request);
        }
        return Result.result(ExceptionCode.SQL_EX.getCode(), StrPool.EMPTY, ExceptionCode.SQL_EX.getMsg(), request.getRequestURI());
    }

    @ExceptionHandler(SQLException.class)
    public Result sqlException(SQLException ex, HttpServletRequest request) {
        log.error("SQLException:", ex);
        return Result.result(ExceptionCode.SQL_EX.getCode(), StrPool.EMPTY, ExceptionCode.SQL_EX.getMsg(), request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result dataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("DataIntegrityViolationException:", ex);
        return Result.result(ExceptionCode.SQL_EX.getCode(), StrPool.EMPTY, ExceptionCode.SQL_EX.getMsg(), request.getRequestURI());
    }

}
