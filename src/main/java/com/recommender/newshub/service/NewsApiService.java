package com.recommender.newshub.service;

import com.recommender.newshub.converter.NewsConverter;
import com.recommender.newshub.domain.News;
import com.recommender.newshub.exception.ex.ApiFetchException;
import com.recommender.newshub.exception.ex.DatabaseException;
import com.recommender.newshub.repository.NewsRepository;
import com.recommender.newshub.web.dto.AdminResponseDto.AddNewsResultDto;
import com.recommender.newshub.web.dto.NewsApiDto.NewsItem;
import com.recommender.newshub.web.dto.NewsApiDto.SearchResultDto;
import com.recommender.newshub.web.dto.NewsApiDto.TopResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.recommender.newshub.service.newsapi.NewsApiConst.*;

@Slf4j
@Service
@Transactional
public class NewsApiService {

    private final NewsRepository newsRepository;
    private final WebClient webClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${news-api-key}")
    public String API_KEY;

    public NewsApiService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
        this.webClient = getWebClient();
    }

    public AddNewsResultDto fetchGeneralNews(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        SearchResultDto searchResultDto = callNewsApi(buildSearchNewsUri(startDateTime, endDateTime), SearchResultDto.class);

        int savedNewsNumber = saveNews(searchResultDto.getNews());
        return new AddNewsResultDto(searchResultDto.getAvailable(), savedNewsNumber);
    }

    public AddNewsResultDto fetchTopNews(LocalDate date) {
        TopResultDto topResultDto = callNewsApi(buildTopNewsUri(date), TopResultDto.class);

        List<NewsItem> allNews = topResultDto.getAllNews();
        allNews.forEach(NewsItem::setTopNews);

        int savedNewsNumber = saveNews(allNews);
        return new AddNewsResultDto(allNews.size(), savedNewsNumber);
    }

    public <T> T callNewsApi(String uri, Class<T> responseType) {
        return webClient.get()
                .uri(uri)
                .headers(headers -> headers.set("x-api-key", API_KEY))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleFetchError)
                .bodyToMono(responseType)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(throwable -> throwable instanceof WebClientResponseException.TooManyRequests))
                .block();
    }

    private int saveNews(List<NewsItem> newsItem) {
        try {
            List<News> newsList = newsItem.stream()
                    .filter(this::validateNewsItem)
                    .map(NewsConverter::toNews)
                    .toList();

            List<News> savedNewsList = newsRepository.saveAll(newsList);
            return savedNewsList.size();
        } catch (DataAccessException ex) {
            log.error("DB saving error: {}", ex.getMessage());
            throw new DatabaseException(ex.getMessage());
        }
    }

    private boolean validateNewsItem(NewsItem newsItem) {
        return newsItem.getId() != null
                && newsItem.getTitle() != null
                && newsItem.getUrl() != null;
    }

    private String buildSearchNewsUri(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return UriComponentsBuilder.fromPath(SEARCH_NEWS_PATH)
                .queryParam("language", ENGLISH)
                .queryParam("news-sources", String.join(",", NEWS_SOURCES))
                .queryParam("earliest-publish-date", startDateTime.format(formatter))
                .queryParam("latest-publish-date", endDateTime.format(formatter))
                .queryParam("categories", String.join(",", CATEGORIES))
                .queryParam("offset", 0)
                .queryParam("number", 100)
                .build()
                .toUriString();
    }

    private String buildTopNewsUri(LocalDate date) {
        return UriComponentsBuilder.fromPath(TOP_NEWS_PATH)
                .queryParam("source-country", USA)
                .queryParam("language", ENGLISH)
                .queryParam("date", date.toString())
                .queryParam("headlines-only", Boolean.FALSE.toString())
                .build()
                .toUriString();
    }

    private static WebClient getWebClient() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
                        .build())
                .baseUrl(BASE_URL)
                .build();
    }

    private Mono<Throwable> handleFetchError(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(responseBody -> {
                    log.error("News api fetch error: {}", responseBody);
                    return Mono.error(new ApiFetchException(responseBody));
                });
    }
}
