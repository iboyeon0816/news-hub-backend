package com.recommender.newshub.web.controller;

import com.recommender.newshub.apipayload.ApiResponse;
import com.recommender.newshub.converter.NewsConverter;
import com.recommender.newshub.service.NewsService;
import com.recommender.newshub.web.dto.NewsResponseDto.GetNewsResultDto;
import com.recommender.newshub.web.dto.NewsResponseDto.NewsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Tag(name = "News", description = "뉴스 기사 조회")
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/top")
    @Operation(summary = "주요 뉴스 기사 조회 API")
    public ApiResponse<GetNewsResultDto> getTopNews() {
        List<NewsDto> newsDtos = newsService.getTopNews().stream()
                .map(NewsConverter::toNewsDto)
                .toList();

        return ApiResponse.onSuccess(HttpStatus.OK, new GetNewsResultDto(newsDtos.size(), newsDtos));
    }
}
