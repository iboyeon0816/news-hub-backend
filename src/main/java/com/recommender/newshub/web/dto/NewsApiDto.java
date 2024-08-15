package com.recommender.newshub.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewsApiDto {

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchResultDto {
        private int offset;
        private int number;
        private int available;
        private List<NewsItem> news;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TopResultDto {
        @JsonProperty("top_news")
        private List<TopNewsItem> topNews;

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class TopNewsItem {
            private List<NewsItem> news;
        }

        public List<NewsItem> getAllNews() {
            List<NewsItem> allNews = new ArrayList<>();
            topNews.forEach(topNewsItem -> allNews.addAll(topNewsItem.getNews()));
            return allNews;
        }
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NewsItem {
        private Long id;
        private String title;
        private String summary;
        private String url;
        private String image;
        @JsonProperty("publish_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime publishDate;
        private String author;
        @JsonProperty("catgory")
        private String category;

        public void setTopNews() {
            this.category = "TOP_NEWS";
        }
    }
}
