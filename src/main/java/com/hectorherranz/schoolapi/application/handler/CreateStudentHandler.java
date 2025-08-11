package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.adapters.out.jpa.service.StudentInfrastructureService;
import com.hectorherranz.schoolapi.application.command.CreateStudentCommand;
import com.hectorherranz.schoolapi.application.port.in.CreateStudentUseCase;
import com.hectorherranz.schoolapi.application.port.out.SchoolRepositoryPort;
import com.hectorherranz.schoolapi.domain.exception.CapacityExceededException;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.Student;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CreateStudentHandler implements CreateStudentUseCase {

  private final SchoolRepositoryPort schoolRepository;
  private final StudentInfrastructureService studentInfrastructureService;

  public CreateStudentHandler(
      SchoolRepositoryPort schoolRepository,
      StudentInfrastructureService studentInfrastructureService) {
    this.schoolRepository = schoolRepository;
    this.studentInfrastructureService = studentInfrastructureService;
  }

  @Override
  public UUID handle(CreateStudentCommand command) {
    // 1. Validate school exists and get basic data
    School school =
        schoolRepository
            .findByIdBasic(command.schoolId())
            .orElseThrow(() -> new NotFoundException("School", command.schoolId().toString()));

    // 2. Check capacity with optimized query
    int currentEnrollment = schoolRepository.countStudentsBySchoolId(command.schoolId());
    if (!school.capacity().canEnroll(currentEnrollment)) {
      throw new CapacityExceededException(command.schoolId());
    }

    // 3. Create student using infrastructure service
    Student student = new Student(UUID.randomUUID(), command.name(), command.schoolId());
    Student savedStudent = studentInfrastructureService.createStudent(student);

    return savedStudent.id();
  }
}
