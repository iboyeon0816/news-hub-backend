package com.recommender.newshub.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"code", "message"})
public class ErrorResponse {

    private final String code;
    private final String message;

}
