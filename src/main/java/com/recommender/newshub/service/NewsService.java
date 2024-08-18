package com.recommender.newshub.service;

import com.recommender.newshub.domain.News;
import com.recommender.newshub.domain.enums.Category;
import com.recommender.newshub.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    private static final int DEFAULT_SIZE = 10;

    public List<News> getTopNews() {
        return newsRepository.findTopNews(PageRequest.of(0, DEFAULT_SIZE));
    }

    public Page<News> getSearchNews(String keyword, Integer page) {
        return newsRepository.findByTitleContaining(keyword, PageRequest.of(page, DEFAULT_SIZE));
    }

    public Page<News> getCategoryNews(Category category, Integer page) {
        return newsRepository.findByCategory(category, PageRequest.of(page, DEFAULT_SIZE));
    }
}
