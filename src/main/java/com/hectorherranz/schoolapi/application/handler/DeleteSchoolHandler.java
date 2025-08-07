package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.command.DeleteSchoolCommand;
import com.hectorherranz.schoolapi.application.port.in.DeleteSchoolUseCase;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DeleteSchoolHandler implements DeleteSchoolUseCase {

  private final SchoolRepositoryPort schoolRepository;

  public DeleteSchoolHandler(SchoolRepositoryPort schoolRepository) {
    this.schoolRepository = schoolRepository;
  }

  @Override
  public void handle(DeleteSchoolCommand command) {
    // Check if school exists before deleting
    if (!schoolRepository.findById(command.schoolId()).isPresent()) {
      throw new NotFoundException("School", command.schoolId().toString());
    }

    // Delete the school
    schoolRepository.deleteById(command.schoolId());
  }
}
