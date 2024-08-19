package com.recommender.newshub.converter;

import com.recommender.newshub.domain.News;
import com.recommender.newshub.domain.enums.Category;
import com.recommender.newshub.web.dto.NewsApiDto.NewsItem;
import com.recommender.newshub.web.dto.NewsResponseDto.GetNewsResultDto;
import com.recommender.newshub.web.dto.NewsResponseDto.GetTopNewsResultDto;
import com.recommender.newshub.web.dto.NewsResponseDto.NewsDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import java.util.List;

public class NewsConverter {

    public static News toNews(NewsItem newsItem) {
        return News.builder()
                .id(newsItem.getId())
                .title(newsItem.getTitle())
                .summary(newsItem.getSummary())
                .url(newsItem.getUrl())
                .imageUrl(newsItem.getImage())
                .publishDate(newsItem.getPublishDate())
                .author(newsItem.getAuthor())
                .category(Category.valueOf(StringUtils.upperCase(newsItem.getCategory())))
                .build();
    }

    public static GetTopNewsResultDto toGetTopNewsResultDto(List<News> newsList) {
        List<NewsDto> newsDtoList = newsList.stream()
                .map(NewsConverter::toNewsDto)
                .toList();

        return new GetTopNewsResultDto(newsDtoList.size(), newsDtoList);
    }

    public static GetNewsResultDto toGetNewsResultDto(Page<News> newsPage) {
        List<NewsDto> newsDtoList = newsPage.getContent().stream()
                .map(NewsConverter::toNewsDto)
                .toList();

        return GetNewsResultDto.builder()
                .isFirst(newsPage.isFirst())
                .isLast(newsPage.isLast())
                .number(newsDtoList.size())
                .totalPages(newsPage.getTotalPages())
                .totalElements(newsPage.getTotalElements())
                .news(newsDtoList)
                .build();
    }

    public static NewsDto toNewsDto(News news) {
        return NewsDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .summary(news.getSummary())
                .url(news.getUrl())
                .imageUrl(news.getImageUrl())
                .publishDate(news.getPublishDate())
                .author(news.getAuthor())
                .category(news.getCategory().toString())
                .build();
    }
}
