package com.recommender.newshub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NewsHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsHubApplication.class, args);
	}

}
