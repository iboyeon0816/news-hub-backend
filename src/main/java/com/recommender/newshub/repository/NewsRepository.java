package com.recommender.newshub.repository;

import com.recommender.newshub.domain.News;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("SELECT n FROM News n WHERE n.category = 'TOP_NEWS' ORDER BY n.publishDate DESC")
    List<News> findTopNews(Pageable pageable);
}
