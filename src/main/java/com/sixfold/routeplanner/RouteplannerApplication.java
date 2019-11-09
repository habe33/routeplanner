package com.sixfold.routeplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RouteplannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RouteplannerApplication.class, args);
	}

}
