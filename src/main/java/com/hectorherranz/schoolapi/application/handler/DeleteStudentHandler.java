package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.command.DeleteStudentCommand;
import com.hectorherranz.schoolapi.application.port.in.DeleteStudentUseCase;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DeleteStudentHandler implements DeleteStudentUseCase {

  private final SchoolRepositoryPort schoolRepository;

  public DeleteStudentHandler(SchoolRepositoryPort schoolRepository) {
    this.schoolRepository = schoolRepository;
  }

  @Override
  public void handle(DeleteStudentCommand command) {
    // Find the school
    var school =
        schoolRepository
            .findById(command.schoolId())
            .orElseThrow(() -> new NotFoundException("School", command.schoolId().toString()));

    // Remove student through the school aggregate
    school.removeStudent(command.studentId());

    // Save the updated school
    schoolRepository.save(school);
  }
}
