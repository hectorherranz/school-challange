package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.port.in.SearchStudentsUseCase;
import com.hectorherranz.schoolapi.application.query.SearchStudentsQuery;
import com.hectorherranz.schoolapi.application.response.PagedResponse;
import com.hectorherranz.schoolapi.domain.model.Student;
import com.hectorherranz.schoolapi.domain.repository.StudentRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class SearchStudentsHandler implements SearchStudentsUseCase {

    private final StudentRepositoryPort studentRepository;

    public SearchStudentsHandler(StudentRepositoryPort studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public PagedResponse<Student> handle(SearchStudentsQuery query) {
        var page = studentRepository.search(query.schoolId(), query.query(), query.pageable());
        return PagedResponse.from(page);
    }
}
