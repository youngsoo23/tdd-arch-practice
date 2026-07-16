package com.study.tddarchpractice.user.controller.response;

import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String email;
    private String nickname;
    private UserStatus status;
    private Long lastLoginAt;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setNickname(user.getNickname());
        response.setStatus(user.getStatus());
        response.setLastLoginAt(user.getLastLoginAt());
        return response;
    }
}
