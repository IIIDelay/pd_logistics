package org.iiidev.pinda.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.constant.JwtClaimsConstant;
import org.iiidev.pinda.dto.UserLoginDTO;
import org.iiidev.pinda.entity.User;
import org.iiidev.pinda.properties.JwtProperties;
import org.iiidev.pinda.result.Result;
import org.iiidev.pinda.service.UserService;
import org.iiidev.pinda.utils.JwtUtil;
import org.iiidev.pinda.vo.UserLoginVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Api(tags = "C端用户相关接口")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final JwtProperties jwtProperties;

    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("微信用户登录: {}", userLoginDTO.getCode());

        // 微信登录
        User user = userService.wxLogin(userLoginDTO);

        // 为微信用户生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setToken(token);
        userLoginVO.setId(user.getId());
        userLoginVO.setOpenid(user.getOpenid());
        return Result.success(userLoginVO);
    }
}
