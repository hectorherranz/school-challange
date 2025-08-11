package com.hectorherranz.schoolapi.config;

import com.hectorherranz.schoolapi.adapters.out.jpa.repository.SchoolJpaRepository;
import com.hectorherranz.schoolapi.adapters.out.jpa.repository.SpringDataSchoolRepository;
import com.hectorherranz.schoolapi.adapters.out.jpa.repository.SpringDataStudentRepository;
import com.hectorherranz.schoolapi.adapters.out.jpa.repository.StudentJpaRepository;
import com.hectorherranz.schoolapi.application.port.out.SchoolRepositoryPort;
import com.hectorherranz.schoolapi.application.port.out.StudentRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class HexagonSpringConfig {

  @Bean
  public SchoolRepositoryPort schoolRepositoryPort(
      SpringDataSchoolRepository springDataSchoolRepository) {
    return new SchoolJpaRepository(springDataSchoolRepository);
  }

  @Bean
  public StudentRepositoryPort studentRepositoryPort(
      SpringDataStudentRepository springDataStudentRepository) {
    return new StudentJpaRepository(springDataStudentRepository);
  }
}
