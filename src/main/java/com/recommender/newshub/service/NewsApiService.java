package com.recommender.newshub.service;

import com.recommender.newshub.converter.NewsConverter;
import com.recommender.newshub.domain.News;
import com.recommender.newshub.exception.ex.ApiFetchException;
import com.recommender.newshub.exception.ex.DatabaseException;
import com.recommender.newshub.repository.NewsRepository;
import com.recommender.newshub.web.dto.AdminResponseDto.AddNewsResultDto;
import com.recommender.newshub.web.dto.NewsApiDto.FetchResultDto;
import com.recommender.newshub.web.dto.NewsApiDto.FetchResultDto.NewsItem;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.recommender.newshub.service.newsapi.NewsApiConst.*;

@Slf4j
@Service
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

    @Transactional
    public AddNewsResultDto fetchAndSaveNews(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        FetchResultDto fetchResultDto = callNewsApi(startDateTime, endDateTime);
        int savedNewsNumber = saveNews(fetchResultDto);
        return new AddNewsResultDto(fetchResultDto.getAvailable(), savedNewsNumber);
    }

    private int saveNews(FetchResultDto fetchResultDto) {
        try {
            List<News> newsList = fetchResultDto.getNews().stream()
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
                && newsItem.getUrl() != null
                && newsItem.getCategory() != null;
    }

    private static WebClient getWebClient() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
                        .build())
                .baseUrl(BASE_URL)
                .build();
    }

    private String buildUri(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return UriComponentsBuilder.fromPath(SEARCH_NEWS_PATH)
                .queryParam("language", LANGUAGE)
                .queryParam("news-sources", String.join(",", NEWS_SOURCES))
                .queryParam("earliest-publish-date", startDateTime.format(formatter))
                .queryParam("latest-publish-date", endDateTime.format(formatter))
                .queryParam("categories", String.join(",", CATEGORIES))
                .queryParam("offset", 0)
                .queryParam("number", 100)
                .build()
                .toUriString();
    }

    private FetchResultDto callNewsApi(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return webClient.get()
                .uri(buildUri(startDateTime, endDateTime))
                .headers(headers -> headers.set("x-api-key", API_KEY))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleFetchError)
                .bodyToMono(FetchResultDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(throwable -> throwable instanceof WebClientResponseException.TooManyRequests))
                .block();
    }

    private Mono<Throwable> handleFetchError(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(responseBody -> {
                    log.error("News api fetch error: {}", responseBody);
                    return Mono.error(new ApiFetchException(responseBody));
                });
    }

}
