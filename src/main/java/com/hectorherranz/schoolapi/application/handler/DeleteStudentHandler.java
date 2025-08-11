package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.command.DeleteStudentCommand;
import com.hectorherranz.schoolapi.application.port.in.DeleteStudentUseCase;
import com.hectorherranz.schoolapi.application.port.out.StudentRepositoryPort;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DeleteStudentHandler implements DeleteStudentUseCase {

  private final StudentRepositoryPort studentRepository;

  public DeleteStudentHandler(StudentRepositoryPort studentRepository) {
    this.studentRepository = studentRepository;
  }

  @Override
  public void handle(DeleteStudentCommand command) {
    // Validate student exists and belongs to school using optimized query
    if (!studentRepository.existsByIdAndSchoolId(command.studentId(), command.schoolId())) {
      throw new NotFoundException(
          "Student",
          String.format(
              "Student %s not found in school %s", command.studentId(), command.schoolId()));
    }

    // Delete student directly (validation already done)
    studentRepository.deleteById(command.studentId());
  }
}
