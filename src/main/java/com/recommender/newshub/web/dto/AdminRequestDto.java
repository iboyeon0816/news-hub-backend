package com.recommender.newshub.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

public class AdminRequestDto {
    @Getter
    public static class AddNewsDto {
        @NotNull
        private LocalDateTime startDateTime;

        @NotNull
        private LocalDateTime endDateTime;
    }
}
