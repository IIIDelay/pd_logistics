package org.iiidev.pinda.authority.controller.common;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.authority.biz.service.common.LoginLogService;
import org.iiidev.pinda.authority.vo.LogRecordVO;
import org.iiidev.pinda.base.BaseController;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.base.id.IdGenerate;
import org.iiidev.pinda.user.annotation.LoginUser;
import org.iiidev.pinda.user.model.SysUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 前端控制器
 * 首页
 */
@Api(value = "dashboard", tags = "首页")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class DashboardController extends BaseController {
    private final LoginLogService loginLogService;
    private final IdGenerate<Long> idGenerate;

    /**
     * 最近10天访问记录
     */
    @GetMapping("/dashboard/visit")
    public Result<LogRecordVO> index(@ApiIgnore @LoginUser SysUser user) {
        LogRecordVO recordVO = LogRecordVO.builder()
            .totalVisitCount(loginLogService.findTotalVisitCount())
            .todayVisitCount(loginLogService.findTodayVisitCount())
            .todayIp(loginLogService.findTodayIp())
            .lastTenVisitCount(loginLogService.findLastTenDaysVisitCount(null))
            .lastTenUserVisitCount(loginLogService.findLastTenDaysVisitCount(user.getAccount()))
            .browserCount(loginLogService.findByBrowser())
            .operatingSystemCount(loginLogService.findByOperatingSystem())
            .build();
        // 获取系统访问记录
        return success(recordVO);
    }

    /**
     * 生成id
     */
    @GetMapping("/common/generateId")
    public Result<Long> generate() {
        return success(idGenerate.generate());
    }
}