package com.recommender.newshub.service;

import com.recommender.newshub.domain.User;
import com.recommender.newshub.exception.ex.ConflictException;
import com.recommender.newshub.exception.ex.UnauthenticatedException;
import com.recommender.newshub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void signUp(User user) {
        if (userRepository.existsByLoginId(user.getLoginId())) {
            throw new ConflictException("This ID is already taken");
        }

        userRepository.save(user);
    }

    public User login(User user) {
        return userRepository.findByLoginId(user.getLoginId())
                .filter(foundUser -> foundUser.getPassword().equals(user.getPassword()))
                .orElseThrow(() -> new UnauthenticatedException("Invalid ID or password"));
    }
}
