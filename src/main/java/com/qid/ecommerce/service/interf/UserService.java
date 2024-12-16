package com.qid.ecommerce.service.interf;

import com.qid.ecommerce.dto.LoginRequest;
import com.qid.ecommerce.dto.Response;
import com.qid.ecommerce.dto.UserDto;
import com.qid.ecommerce.entity.User;

public interface UserService {
    Response registerUSer(UserDto registrationRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    User getLoginUser();
    Response getUserInfoAndOrderHistory();
}
