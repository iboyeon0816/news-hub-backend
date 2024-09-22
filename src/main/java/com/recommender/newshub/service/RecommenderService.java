package com.recommender.newshub.service;

import com.recommender.newshub.domain.News;
import com.recommender.newshub.domain.User;
import com.recommender.newshub.exception.ex.ApiFetchException;
import com.recommender.newshub.exception.ex.UserClickHistoryNotFoundException;
import com.recommender.newshub.repository.NewsClickLogRepository;
import com.recommender.newshub.repository.NewsRepository;
import com.recommender.newshub.web.dto.RecommenderDto.RecommendedNewsDto;
import com.recommender.newshub.web.dto.enums.ControllerNewsCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

import static com.recommender.newshub.constants.RecommenderConst.BASE_URL;
import static com.recommender.newshub.constants.RecommenderConst.RECOMMENDED_NEWS_PATH;


@Slf4j
@Service
public class RecommenderService {

    private final NewsRepository newsRepository;
    private final NewsClickLogRepository newsClickLogRepository;
    private final WebClient webClient;

    public RecommenderService(NewsRepository newsRepository, NewsClickLogRepository newsClickLogRepository) {
        this.newsRepository = newsRepository;
        this.newsClickLogRepository = newsClickLogRepository;
        this.webClient = getWebClient();
    }

    public List<News> getRecommendedNews(ControllerNewsCategory category, User user) {
        validateUserClickHistory(user);

        String uri = buildRecommendedNewsUri(category.toString(), user.getId());
        RecommendedNewsDto recommendedNewsDto = callRecommenderApi(uri);

        return newsRepository.findByIdIn(recommendedNewsDto.getRecommendedNewsIds());
    }

    private RecommendedNewsDto callRecommenderApi(String uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleFetchError)
                .bodyToMono(RecommendedNewsDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(throwable -> throwable instanceof WebClientResponseException.TooManyRequests))
                .block();
    }

    private static WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    private static String buildRecommendedNewsUri(String category, Long userId) {
        return UriComponentsBuilder.fromPath(RECOMMENDED_NEWS_PATH)
                .queryParam("category", category)
                .queryParam("user_id", userId)
                .build().toUriString();
    }

    private Mono<? extends Throwable> handleFetchError(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(responseBody -> {
                    log.error("[Recommender] fetch error: {}", responseBody);
                    return Mono.error(new ApiFetchException(responseBody));
                });
    }

    private void validateUserClickHistory(User user) {
        Boolean exists = newsClickLogRepository.existsByUser(user);
        if (!exists) {
            throw new UserClickHistoryNotFoundException("No click history found for user");
        }
    }
}
