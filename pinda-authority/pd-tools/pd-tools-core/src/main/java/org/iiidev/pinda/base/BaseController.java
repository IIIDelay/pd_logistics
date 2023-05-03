package org.iiidev.pinda.base;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.iiidev.pinda.context.BaseContextHandler;
import org.iiidev.pinda.exception.BizException;
import org.iiidev.pinda.exception.code.BaseExceptionCode;
import org.iiidev.pinda.utils.AntiSqlFilter;
import org.iiidev.pinda.utils.NumberHelper;

import cn.hutool.core.util.StrUtil;

import static org.iiidev.pinda.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * SuperController
 *
 */
public abstract class BaseController {
    @Resource
    protected HttpServletRequest request;
    @Resource
    protected HttpServletResponse response;

    /**
     * 当前页
     */
    protected static final String CURRENT = "current";
    /**
     * 每页显示条数
     */
    protected static final String SIZE = "size";
    /**
     * 排序字段 ASC
     */
    protected static final String PAGE_ASCS = "ascs";
    /**
     * 排序字段 DESC
     */
    protected static final String PAGE_DESCS = "descs";

    protected static final String START_CREATE_TIME = "startCreateTime";
    protected static final String END_CREATE_TIME = "endCreateTime";
    /**
     * 默认每页条目20,最大条目数100
     */
    int DEFAULT_LIMIT = 20;
    int MAX_LIMIT = 10000;

    /**
     * 成功返回
     *
     * @param data
     * @return
     */
    public <T> Result<T> success(T data) {
        return Result.success(data);
    }

    public Result<Boolean> success() {
        return Result.success();
    }

    /**
     * 失败返回
     *
     * @param msg
     * @return
     */
    public <T> Result<T> fail(String msg) {
        return Result.fail(msg);
    }

    public <T> Result<T> fail(String msg, Object... args) {
        return Result.fail(msg, args);
    }

    /**
     * 失败返回
     *
     * @param code
     * @param msg
     * @return
     */
    public <T> Result<T> fail(int code, String msg) {
        return Result.fail(code, msg);
    }

    public <T> Result<T> fail(BaseExceptionCode exceptionCode) {
        return Result.fail(exceptionCode);
    }

    public <T> Result<T> fail(BizException exception) {
        return Result.fail(exception);
    }

    public <T> Result<T> fail(Throwable throwable) {
        return Result.fail(throwable);
    }

    public <T> Result<T> validFail(String msg) {
        return Result.validFail(msg);
    }

    public <T> Result<T> validFail(String msg, Object... args) {
        return Result.validFail(msg, args);
    }

    public <T> Result<T> validFail(BaseExceptionCode exceptionCode) {
        return Result.validFail(exceptionCode);
    }

    /**
     * 获取当前用户id
     */
    protected Long getUserId() {
        return BaseContextHandler.getUserId();
    }

    protected String getAccount() {
        return BaseContextHandler.getAccount();
    }

    protected String getName() {
        return BaseContextHandler.getName();
    }

    /**
     * 获取分页对象
     *
     * @return
     */
    protected <T> Page<T> getPage() {
        return getPage(false);
    }

    protected Integer getPageNo() {
        return NumberHelper.intValueOf(request.getParameter(CURRENT), 1);
    }

    protected Integer getPageSize() {
        return NumberHelper.intValueOf(request.getParameter(SIZE), DEFAULT_LIMIT);
    }

    /**
     * 获取分页对象
     *
     * @param openSort
     * @return
     */
    protected <T> Page<T> getPage(boolean openSort) {
        // 页数
        Integer pageNo = getPageNo();
        // 分页大小
        Integer pageSize = getPageSize();
        // 是否查询分页
        return buildPage(openSort, pageNo, pageSize);
    }

    private <T> Page<T> buildPage(boolean openSort, long pageNo, long pageSize) {
        // 是否查询分页
        pageSize = pageSize > MAX_LIMIT ? MAX_LIMIT : pageSize;
        Page<T> page = new Page<>(pageNo, pageSize);
        if (openSort) {
            page.setAsc(getParameterSafeValues(PAGE_ASCS));
            page.setDesc(getParameterSafeValues(PAGE_DESCS));
        }
        return page;
    }

    /**
     * 获取安全参数(SQL ORDER BY 过滤)
     *
     * @param parameter
     * @return
     */
    protected String[] getParameterSafeValues(String parameter) {
        return AntiSqlFilter.getSafeValues(request.getParameterValues(parameter));
    }

    protected LocalDateTime getStartCreateTime() {
        return getLocalDateTime(START_CREATE_TIME);
    }

    protected LocalDateTime getEndCreateTime() {
        return getLocalDateTime(END_CREATE_TIME);
    }

    private LocalDateTime getLocalDateTime(String endCreateTime) {
        String startCreateTime = request.getParameter(endCreateTime);
        if (StrUtil.isBlank(startCreateTime)) {
            return null;
        }
        String safeValue = AntiSqlFilter.getSafeValue(startCreateTime);
        if (StrUtil.isBlank(safeValue)) {
            return null;
        }
        return LocalDateTime.parse(safeValue, DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
    }
}
