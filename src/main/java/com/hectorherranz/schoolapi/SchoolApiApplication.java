package com.hectorherranz.schoolapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SchoolApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SchoolApiApplication.class, args);
  }
}
