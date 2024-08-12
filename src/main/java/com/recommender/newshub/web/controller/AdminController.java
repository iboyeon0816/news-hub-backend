package com.recommender.newshub.web.controller;

import com.recommender.newshub.apipayload.ApiResponse;
import com.recommender.newshub.domain.User;
import com.recommender.newshub.domain.enums.Role;
import com.recommender.newshub.exception.ex.ForbiddenException;
import com.recommender.newshub.exception.ex.UnauthenticatedException;
import com.recommender.newshub.service.NewsApiService;
import com.recommender.newshub.web.controller.user.SessionConst;
import com.recommender.newshub.web.dto.AdminRequestDto.AddNewsDto;
import com.recommender.newshub.web.dto.AdminResponseDto.AddNewsResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자 권한")
public class AdminController {

    private final NewsApiService newsApiService;

    @PostMapping("/news")
    @Operation(summary = "뉴스 기사 수집 및 저장 API")
    public ApiResponse<AddNewsResultDto> saveNews(@RequestBody AddNewsDto addNewsDto,
                                HttpServletRequest request) {
        validateAdminAccount(request);

        AddNewsResultDto addNewsResultDto = newsApiService.fetchAndSaveNews(addNewsDto.getStartDateTime(), addNewsDto.getEndDateTime());
        return ApiResponse.onSuccess(HttpStatus.OK, addNewsResultDto);
    }

    private static void validateAdminAccount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new UnauthenticatedException("Session not found. Please login.");
        }

        User user = (User) session.getAttribute(SessionConst.USER);
        if (user == null) {
            throw new UnauthenticatedException("User not authenticated");
        }

        if (!Role.ROLE_ADMIN.equals(user.getRole())) {
            throw new ForbiddenException("Access denied");
        }
    }

}
