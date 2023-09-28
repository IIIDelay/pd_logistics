package org.iiidev.pinda.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.authority.api.OrgApi;
import org.iiidev.pinda.authority.api.RoleApi;
import org.iiidev.pinda.authority.api.UserApi;
import org.iiidev.pinda.authority.entity.auth.User;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.common.utils.PageResponse;
import org.iiidev.pinda.util.BeanUtil;
import org.iiidev.pinda.vo.base.userCenter.MessageVo;
import org.iiidev.pinda.vo.base.userCenter.SysUserVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "个人中心")
@Slf4j
@RestController
@RequestMapping("userCenter")
@RequiredArgsConstructor
public class UserCenterController {
    private final OrgApi orgApi;
    private final UserApi userApi;
    private final RoleApi roleApi;

    /**
     * 获取个人信息
     *
     * @return 用户信息
     */
    @ApiOperation(value = "获取个人信息")
    @GetMapping("/info")
    public SysUserVo info() {
        // TODO: 2020/1/2 从token中获取用户id
        Long userId = 1L;
        SysUserVo vo = new SysUserVo();
        Result<User> result = userApi.get(userId);
        if (result.isSuccess() && result.getData() != null) {
            vo = BeanUtil.parseUser2Vo(result.getData(), roleApi, orgApi);
        }
        return vo;
    }

    @ApiOperation(value = "获取通知公告")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "页码", required = true, example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "页尺寸", required = true, example = "10"),
        @ApiImplicitParam(name = "messageType", value = "消息类型：notice为通知,bulletin为公告", example = "notice")
    })
    @GetMapping("/message")
    public PageResponse<MessageVo> info(@RequestParam(name = "page") Integer page, @RequestParam(name = "pageSize") Integer pageSize, @RequestParam(value = "messageType", required = false) String messageType) {
        // TODO: 2020/1/3 消息待实现 未读条数待实现
        List<MessageVo> messageVoList = new ArrayList<>();
        MessageVo messageVo = new MessageVo();
        messageVo.setId("1");
        messageVo.setContent("hahahaha");
        messageVo.setTitle("说点什么呢");
        messageVo.setStatus(1);
        messageVo.setMessageType("notice");
        messageVoList.add(messageVo);
        return PageResponse.<MessageVo>builder()
            .pages(1L)
            .counts(2L)
            .page(page)
            .pagesize(pageSize)
            .items(messageVoList)
            .build();
    }

    @ApiOperation(value = "打开未读消息")
    @PutMapping("/message/{id}")
    public MessageVo read(@PathVariable(value = "id") Long id) {
        // TODO: 2020/1/3 实现消息已读状态切换
        MessageVo messageVo = new MessageVo();
        messageVo.setId("1");
        messageVo.setContent("hahahaha");
        messageVo.setTitle("说点什么呢");
        messageVo.setStatus(0);
        messageVo.setMessageType("notice");
        return messageVo;
    }

}
