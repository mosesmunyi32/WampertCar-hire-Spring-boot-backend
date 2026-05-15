package com.wampart.wampart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class WampartApplication {

	public static void main(String[] args) {
		SpringApplication.run(WampartApplication.class, args);
	}

}
