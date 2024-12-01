package com.springsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(jsr250Enabled = true,securedEnabled = true)  //This is used to enable method level security on springboot
public class EazyBankSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(EazyBankSpringApplication.class, args);
	}

}
