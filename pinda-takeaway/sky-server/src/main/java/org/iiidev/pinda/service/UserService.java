package org.iiidev.pinda.service;

import org.iiidev.pinda.dto.UserLoginDTO;
import org.iiidev.pinda.entity.User;

public interface UserService {

    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
