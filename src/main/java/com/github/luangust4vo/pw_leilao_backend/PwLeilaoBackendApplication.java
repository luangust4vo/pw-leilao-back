package com.github.luangust4vo.pw_leilao_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PwLeilaoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PwLeilaoBackendApplication.class, args);
	}
}
