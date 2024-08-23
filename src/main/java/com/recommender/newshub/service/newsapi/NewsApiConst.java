package com.recommender.newshub.service.newsapi;

import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

public class NewsApiConst {
    public static final String BASE_URL = "https://api.worldnewsapi.com";
    public static final String SEARCH_NEWS_PATH = "/search-news";
    public static final String TOP_NEWS_PATH = "/top-news";
    public static final String ENGLISH = "en";
    public static final String USA = "us";
    public static final List<String> NEWS_SOURCES = Arrays.asList(
            "bbc.co.uk", "aljazeera.com", "nytimes.com",
            "theguardian.com", "independent.co.uk", "ft.com",
            "vox.com"
    );
    public static final List<String> CATEGORIES = Arrays.asList(
            "politics", "sports", "business", "technology",
            "entertainment", "health", "science", "lifestyle",
            "travel", "culture", "education", "environment"
    );
    public static final UriComponentsBuilder SEARCH_NEWS_URI_COMPONENT = UriComponentsBuilder.fromPath(SEARCH_NEWS_PATH)
            .queryParam("language", ENGLISH)
            .queryParam("news-sources", String.join(",", NEWS_SOURCES))
            .queryParam("categories", String.join(",", CATEGORIES))
            .queryParam("offset", 0)
            .queryParam("number", 100);
    public static final UriComponentsBuilder TOP_NEWS_URI_COMPONENT = UriComponentsBuilder.fromPath(TOP_NEWS_PATH)
            .queryParam("source-country", USA)
            .queryParam("language", ENGLISH)
            .queryParam("headlines-only", Boolean.FALSE.toString());
}
