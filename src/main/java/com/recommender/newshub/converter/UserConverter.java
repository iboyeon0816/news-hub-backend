package com.recommender.newshub.converter;

import com.recommender.newshub.domain.User;
import com.recommender.newshub.web.dto.UserRequestDto;
import com.recommender.newshub.web.dto.UserRequestDto.SignUpDto;

public class UserConverter {
    public static User toUser(SignUpDto signUpDto) {
        return new User(signUpDto.getLoginId(), signUpDto.getPassword());
    }
}
