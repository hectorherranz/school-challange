package com.hectorherranz.schoolapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class HexagonSpringConfig {
    // TODO: bean wiring for ports ↔ adapters
}
