package com.hectorherranz.schoolapi.config;

import com.hectorherranz.schoolapi.adapters.out.jpa.service.StudentInfrastructureService;
import com.hectorherranz.schoolapi.application.handler.UpdateStudentHandlerOptimized;
import com.hectorherranz.schoolapi.application.port.in.UpdateStudentUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for hybrid approach handlers. Uses optimized approach by default for maximum
 * performance.
 */
@Configuration
public class HandlerConfiguration {

  /**
   * Hybrid handler - Uses optimized approach for maximum performance. Leverages Hibernate
   * optimizations while maintaining clean architecture.
   */
  @Bean
  public UpdateStudentUseCase updateStudentUseCase(
      StudentInfrastructureService infrastructureService) {
    return new UpdateStudentHandlerOptimized(infrastructureService);
  }
}
