package com.recommender.newshub.web.controller;

import com.recommender.newshub.apipayload.ApiResponse;
import com.recommender.newshub.domain.User;
import com.recommender.newshub.domain.enums.UserRole;
import com.recommender.newshub.exception.ex.ForbiddenException;
import com.recommender.newshub.exception.ex.UnauthenticatedException;
import com.recommender.newshub.service.NewsApiService;
import com.recommender.newshub.web.controller.user.SessionConst;
import com.recommender.newshub.web.dto.AdminRequestDto.FetchGeneralNewsDto;
import com.recommender.newshub.web.dto.AdminRequestDto.FetchTopNewsDto;
import com.recommender.newshub.web.dto.AdminResponseDto.AddNewsResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/news")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자 권한")
public class AdminController {

    private final NewsApiService newsApiService;

    @PostMapping
    @Operation(summary = "일반 뉴스 기사 수집 API")
    public ApiResponse<AddNewsResultDto> fetchGeneralNews(@Valid @RequestBody FetchGeneralNewsDto fetchGeneralNewsDto,
                                HttpServletRequest request) {
        validateAdminAccount(request);

        AddNewsResultDto addNewsResultDto = newsApiService.fetchGeneralNews(fetchGeneralNewsDto.getStartDateTime(), fetchGeneralNewsDto.getEndDateTime());
        return ApiResponse.onSuccess(HttpStatus.OK, addNewsResultDto);
    }

    @PostMapping("/top")
    @Operation(summary = "주요 뉴스 기사 수집 API")
    public ApiResponse<AddNewsResultDto> fetchTopNews(@Valid @RequestBody FetchTopNewsDto fetchTopNewsDto,
                                                  HttpServletRequest request) {
        validateAdminAccount(request);

        AddNewsResultDto addNewsResultDto = newsApiService.fetchTopNews(fetchTopNewsDto.getDate());
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

        if (!UserRole.ROLE_ADMIN.equals(user.getUserRole())) {
            throw new ForbiddenException("Access denied");
        }
    }

}
