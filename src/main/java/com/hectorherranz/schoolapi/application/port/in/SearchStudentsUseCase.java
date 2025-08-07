package com.hectorherranz.schoolapi.application.port.in;

import com.hectorherranz.schoolapi.application.query.SearchStudentsQuery;
import com.hectorherranz.schoolapi.application.response.PagedResponse;
import com.hectorherranz.schoolapi.domain.model.Student;

public interface SearchStudentsUseCase {
  PagedResponse<Student> handle(SearchStudentsQuery query);
}
