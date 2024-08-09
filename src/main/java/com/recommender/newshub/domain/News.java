package com.recommender.newshub.domain;

import com.recommender.newshub.domain.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class News {

    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false)
    private String url;

    private String imageUrl;

    private LocalDateTime publishDate;

    private String author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
}
