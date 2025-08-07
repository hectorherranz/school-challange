package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.port.in.GetStudentByIdUseCase;
import com.hectorherranz.schoolapi.application.query.GetStudentByIdQuery;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.Student;
import com.hectorherranz.schoolapi.domain.repository.StudentRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class GetStudentByIdHandler implements GetStudentByIdUseCase {

  private final StudentRepositoryPort studentRepository;

  public GetStudentByIdHandler(StudentRepositoryPort studentRepository) {
    this.studentRepository = studentRepository;
  }

  @Override
  public Student handle(GetStudentByIdQuery query) {
    return studentRepository
        .findById(query.studentId())
        .orElseThrow(() -> new NotFoundException("Student", query.studentId().toString()));
  }
}
