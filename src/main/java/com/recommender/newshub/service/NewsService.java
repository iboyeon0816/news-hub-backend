package com.recommender.newshub.service;

import com.recommender.newshub.domain.News;
import com.recommender.newshub.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public List<News> getTopNews() {
        return newsRepository.findTopNews(PageRequest.of(0, 10));
    }
}
