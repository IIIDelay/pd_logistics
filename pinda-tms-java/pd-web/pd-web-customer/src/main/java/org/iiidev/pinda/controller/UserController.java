package org.iiidev.pinda.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.DTO.UserProfileDTO;
import org.iiidev.pinda.common.context.RequestContext;
import org.iiidev.pinda.common.utils.RespResult;
import org.iiidev.pinda.entity.Member;
import org.iiidev.pinda.feign.UserClient;
import org.iiidev.pinda.service.IMemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 运单表 前端控制器
 * </p>
 *
 * @author diesel
 * @since 2020-03-19
 */
@Slf4j
@Api(tags = "用户管理")
@Controller
@RequestMapping("user")
public class UserController {

    private final UserClient userClient;

    private final IMemberService memberService;

    public UserController(UserClient userClient, IMemberService memberService) {
        this.userClient = userClient;
        this.memberService = memberService;
    }

    @ApiOperation(value = "我的信息")
    @ResponseBody
    @GetMapping("profile")
    public RespResult profile() {

        // 并放入参数
        String userId = RequestContext.getUserId();

        Member member = memberService.detail(userId);
        if (member != null) {
            return RespResult.ok().put("data", UserProfileDTO.builder()
                    .id(userId)
                    .avatar(member.getAvatar())
                    .name(member.getName())
                    .phone(member.getPhone())
                    .build());
        } else {
            return RespResult.ok().put("data", UserProfileDTO.builder().build());
        }
    }
}
