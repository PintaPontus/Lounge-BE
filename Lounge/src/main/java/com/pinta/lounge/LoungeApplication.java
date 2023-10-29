package com.pinta.lounge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication(scanBasePackages= "com.pinta.lounge")
public class LoungeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoungeApplication.class, args);
    }

}
