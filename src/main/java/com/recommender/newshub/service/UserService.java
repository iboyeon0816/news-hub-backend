package com.recommender.newshub.service;

import com.recommender.newshub.converter.UserConverter;
import com.recommender.newshub.domain.User;
import com.recommender.newshub.repository.UserRepository;
import com.recommender.newshub.web.dto.UserRequestDto;
import com.recommender.newshub.web.dto.UserRequestDto.LoginDto;
import com.recommender.newshub.web.dto.UserRequestDto.SignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void signUp(SignUpDto signUpDto) {
        if (userRepository.existsByLoginId(signUpDto.getLoginId())) {
            throw new IllegalArgumentException("This ID is already taken");
        }

        User user = UserConverter.toUser(signUpDto);
        userRepository.save(user);
    }

    public User login(LoginDto loginDto) {
        return userRepository.findByLoginId(loginDto.getLoginId())
                .filter(user -> user.getPassword().equals(loginDto.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID or password"));
    }
}
