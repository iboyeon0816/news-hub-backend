package com.recommender.newshub.web.controller;

import com.recommender.newshub.domain.User;
import com.recommender.newshub.service.UserService;
import com.recommender.newshub.web.controller.login.SessionConst;
import com.recommender.newshub.web.dto.UserRequestDto.LoginDto;
import com.recommender.newshub.web.dto.UserRequestDto.SignUpDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "회원가입/로그인/로그아웃")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원가입 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 형식의 요청이거나 사용할 수 없는 아이디")
    })
    public void signup(@Valid @RequestBody
                           @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                   description = "ID: 필수 필드, 공백 불가, 20자 제한" +
                                           " 비밀번호: 필수 필드, 공백 불가, 20자 제한"
                           ) SignUpDto signUpDto) {
        userService.signUp(signUpDto);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 형식의 요청이거나 존재하지 않는 회원")
    })
    public void login(@Valid @RequestBody
                          @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                  description = "ID: 필수 필드, 공백 불가, 20자 제한" +
                                          " 비밀번호: 필수 필드, 공백 불가, 20자 제한"
                          )
                          LoginDto loginDto,
                      HttpServletRequest request) {
        User user = userService.login(loginDto);

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.USER, user);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
