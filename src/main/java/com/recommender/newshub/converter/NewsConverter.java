package com.recommender.newshub.converter;

import com.recommender.newshub.domain.News;
import com.recommender.newshub.domain.enums.Category;
import com.recommender.newshub.web.dto.NewsApiDto.FetchResultDto.NewsItem;
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
}
