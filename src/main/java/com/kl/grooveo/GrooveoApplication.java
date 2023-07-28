package com.kl.grooveo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GrooveoApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrooveoApplication.class, args);
	}

}