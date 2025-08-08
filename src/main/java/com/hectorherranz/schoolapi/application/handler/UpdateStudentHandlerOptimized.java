package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.adapters.out.jpa.service.StudentInfrastructureService;
import com.hectorherranz.schoolapi.application.command.UpdateStudentCommand;
import com.hectorherranz.schoolapi.application.port.in.UpdateStudentUseCase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Optimized handler that leverages Hibernate session for maximum performance. This approach trades
 * some DDD purity for significant performance gains.
 */
@Component
@Transactional
public class UpdateStudentHandlerOptimized implements UpdateStudentUseCase {

  private final StudentInfrastructureService infrastructureService;

  public UpdateStudentHandlerOptimized(StudentInfrastructureService infrastructureService) {
    this.infrastructureService = infrastructureService;
  }

  @Override
  public void handle(UpdateStudentCommand command) {
    // Delegate to infrastructure service that works directly with entities
    // This maximizes Hibernate optimizations while keeping business logic clean
    infrastructureService.updateStudentOptimized(
        command.studentId(), command.schoolId(), command.name());
  }
}
