package com.example.migros;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

@EnableCaching
@EnableKafka
@SpringBootApplication
public class MigrosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MigrosApplication.class, args);
	}

}
