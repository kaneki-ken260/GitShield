package com.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.backend"})
public class CapStoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(CapStoneApplication.class, args);
	}

}
