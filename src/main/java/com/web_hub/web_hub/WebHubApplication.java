package com.web_hub.web_hub;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@EntityScan(basePackages = "com.web_hub.web_hub")
@EnableJpaRepositories(basePackages = "com.web_hub.web_hub")
public class WebHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebHubApplication.class, args);
	}

}
