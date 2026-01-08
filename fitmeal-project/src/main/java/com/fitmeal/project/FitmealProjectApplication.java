package com.fitmeal.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FitmealProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitmealProjectApplication.class, args);
	}

}
