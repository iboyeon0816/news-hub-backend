package com.recommender.newshub.service.newsapi;

import java.util.Arrays;
import java.util.List;

public class NewsApiConst {
    public static final String API_KEY = "cb60b4e835f94a138c801ce93e1ac2e2";
    public static final String BASE_URL = "https://api.worldnewsapi.com";
    public static final String SEARCH_NEWS_PATH = "/search-news";
    public static final String LANGUAGE = "en";
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
}
