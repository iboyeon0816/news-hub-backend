package com.recommender.newshub.repository;

import com.recommender.newshub.domain.News;
import com.recommender.newshub.domain.enums.NewsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("SELECT n FROM News n WHERE n.isTopNews = true ORDER BY n.publishDate DESC")
    List<News> findTopNews(Pageable pageable);

    @Query("SELECT n FROM News n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY n.publishDate DESC")
    Page<News> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.newsCategory = :category ORDER BY n.publishDate DESC" )
    Page<News> findByCategory(@Param("category") NewsCategory category, Pageable pageable);
}
