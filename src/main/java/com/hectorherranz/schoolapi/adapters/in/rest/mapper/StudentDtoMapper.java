package com.hectorherranz.schoolapi.adapters.in.rest.mapper;

import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentRequest;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentResponse;
import com.hectorherranz.schoolapi.application.command.CreateStudentCommand;
import com.hectorherranz.schoolapi.application.command.UpdateStudentCommand;
import com.hectorherranz.schoolapi.domain.model.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentDtoMapper {

  public CreateStudentCommand toCreateCommand(StudentRequest request) {
    return new CreateStudentCommand(request.name(), request.schoolId());
  }

  public UpdateStudentCommand toUpdateCommand(java.util.UUID studentId, String name) {
    return new UpdateStudentCommand(studentId, name);
  }

  public StudentResponse toResponse(Student student) {
    return new StudentResponse(student.id(), student.name(), student.schoolId());
  }
}
