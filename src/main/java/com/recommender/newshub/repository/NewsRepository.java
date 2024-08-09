package com.recommender.newshub.repository;

import com.recommender.newshub.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
}
