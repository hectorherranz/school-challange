package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.adapters.out.jpa.service.SchoolInfrastructureService;
import com.hectorherranz.schoolapi.application.command.UpdateSchoolCommand;
import com.hectorherranz.schoolapi.application.port.in.UpdateSchoolUseCase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Optimized handler that leverages Hibernate session for maximum performance. This approach trades
 * some DDD purity for significant performance gains.
 */
@Component
@Transactional
public class UpdateSchoolHandlerOptimized implements UpdateSchoolUseCase {

  private final SchoolInfrastructureService infrastructureService;

  public UpdateSchoolHandlerOptimized(SchoolInfrastructureService infrastructureService) {
    this.infrastructureService = infrastructureService;
  }

  @Override
  public void handle(UpdateSchoolCommand command) {
    // Delegate to infrastructure service that works directly with entities
    // This maximizes Hibernate optimizations while keeping business logic clean
    infrastructureService.updateSchoolOptimized(
        command.schoolId(), command.name(), command.capacity());
  }
}
