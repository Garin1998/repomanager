package com.repomanager;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Repomanager API", version = "1.0"))
public class RepomanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepomanagerApplication.class, args);
	}

}
