package com.hectorherranz.schoolapi.application.port.in;

import com.hectorherranz.schoolapi.application.query.GetStudentByIdQuery;
import com.hectorherranz.schoolapi.domain.model.Student;

public interface GetStudentByIdUseCase {
    Student handle(GetStudentByIdQuery query);
}
