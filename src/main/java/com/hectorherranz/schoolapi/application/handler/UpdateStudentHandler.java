package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.command.UpdateStudentCommand;
import com.hectorherranz.schoolapi.application.port.in.UpdateStudentUseCase;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class UpdateStudentHandler implements UpdateStudentUseCase {

  private final SchoolRepositoryPort schoolRepository;

  public UpdateStudentHandler(SchoolRepositoryPort schoolRepository) {
    this.schoolRepository = schoolRepository;
  }

  @Override
  public void handle(UpdateStudentCommand command) {
    // Find the school
    var school =
        schoolRepository
            .findById(command.schoolId())
            .orElseThrow(() -> new NotFoundException("School", command.schoolId().toString()));

    // Update student through the school aggregate
    school.updateStudent(command.studentId(), command.name());

    // Save the updated school
    schoolRepository.save(school);
  }
}
