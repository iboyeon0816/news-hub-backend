package com.recommender.newshub.web.controller;

import com.recommender.newshub.apipayload.ApiResponse;
import com.recommender.newshub.argument.PageCheck;
import com.recommender.newshub.converter.NewsConverter;
import com.recommender.newshub.domain.News;
import com.recommender.newshub.domain.enums.NewsCategory;
import com.recommender.newshub.service.NewsQueryService;
import com.recommender.newshub.web.dto.NewsResponseDto.GetNewsResultDto;
import com.recommender.newshub.web.dto.NewsResponseDto.GetTopNewsResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Tag(name = "News", description = "뉴스 기사 조회")
public class NewsController {

    private final NewsQueryService newsQueryService;

    @GetMapping("/top")
    @Operation(summary = "주요 뉴스 기사 조회 API")
    public ApiResponse<GetTopNewsResultDto> getTopNews() {
        List<News> topNewsList = newsQueryService.getTopNews();
        return ApiResponse.onSuccess(HttpStatus.OK, NewsConverter.toGetTopNewsResultDto(topNewsList));
    }

    @GetMapping("/search")
    @Operation(summary = "키워드 검색 뉴스 기사 조회 API")
    public ApiResponse<GetNewsResultDto> getSearchNews(@RequestParam String keyword,
                                                       @PageCheck Integer page) {
        Page<News> searchNews = newsQueryService.getSearchNews(keyword, page);
        return ApiResponse.onSuccess(HttpStatus.OK, NewsConverter.toGetNewsResultDto(searchNews));
    }

    @GetMapping
    @Operation(summary = "카테고리 뉴스 기사 조회 API")
    public ApiResponse<GetNewsResultDto> getCategoryNews(@RequestParam NewsCategory newsCategory,
                                                         @PageCheck Integer page) {
        Page<News> categoryNews = newsQueryService.getCategoryNews(newsCategory, page);
        return ApiResponse.onSuccess(HttpStatus.OK, NewsConverter.toGetNewsResultDto(categoryNews));
    }
}
