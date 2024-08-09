package com.recommender.newshub.exception.ex;

import com.recommender.newshub.exception.CustomException;
import org.springframework.http.HttpStatus;

public class DatabaseException extends CustomException {
    public DatabaseException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
