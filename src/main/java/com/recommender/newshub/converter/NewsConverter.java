package com.recommender.newshub.converter;

import com.recommender.newshub.domain.News;
import com.recommender.newshub.domain.enums.Category;
import com.recommender.newshub.web.dto.NewsApiDto.NewsItem;
import com.recommender.newshub.web.dto.NewsResponseDto.NewsDto;
import org.apache.commons.lang3.StringUtils;

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

    public static NewsDto toNewsDto(News news) {
        return NewsDto.builder()
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
