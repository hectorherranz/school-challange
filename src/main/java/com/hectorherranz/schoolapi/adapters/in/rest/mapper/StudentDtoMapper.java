package com.hectorherranz.schoolapi.adapters.in.rest.mapper;

import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentRequest;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentResponse;
import com.hectorherranz.schoolapi.application.command.CreateStudentCommand;
import com.hectorherranz.schoolapi.application.command.DeleteStudentCommand;
import com.hectorherranz.schoolapi.application.command.UpdateStudentCommand;
import com.hectorherranz.schoolapi.application.query.GetStudentByIdQuery;
import com.hectorherranz.schoolapi.application.query.SearchStudentsQuery;
import com.hectorherranz.schoolapi.domain.model.Student;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class StudentDtoMapper {

  public CreateStudentCommand toCreateCommand(StudentRequest request, UUID schoolId) {
    return new CreateStudentCommand(request.name(), schoolId);
  }

  public UpdateStudentCommand toUpdateCommand(
      StudentRequest request, UUID schoolId, UUID studentId) {
    return new UpdateStudentCommand(schoolId, studentId, request.name());
  }

  public DeleteStudentCommand toDeleteCommand(UUID schoolId, UUID studentId) {
    return new DeleteStudentCommand(schoolId, studentId);
  }

  public GetStudentByIdQuery toGetStudentQuery(UUID studentId, UUID schoolId) {
    return new GetStudentByIdQuery(studentId, schoolId);
  }

  public SearchStudentsQuery toSearchQuery(UUID schoolId, String q, Pageable pageable) {
    return new SearchStudentsQuery(schoolId, q, pageable);
  }

  public StudentResponse toResponse(Student student) {
    return new StudentResponse(student.id(), student.name(), student.schoolId());
  }
}
