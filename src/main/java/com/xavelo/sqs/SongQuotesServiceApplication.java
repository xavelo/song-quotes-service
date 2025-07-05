package com.xavelo.sqs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SongQuotesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SongQuotesServiceApplication.class, args);
	}

}
