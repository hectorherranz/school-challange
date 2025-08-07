package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.port.in.GetSchoolByIdUseCase;
import com.hectorherranz.schoolapi.application.query.GetSchoolByIdQuery;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class GetSchoolByIdHandler implements GetSchoolByIdUseCase {

  private final SchoolRepositoryPort schoolRepository;

  public GetSchoolByIdHandler(SchoolRepositoryPort schoolRepository) {
    this.schoolRepository = schoolRepository;
  }

  @Override
  public School handle(GetSchoolByIdQuery query) {
    return schoolRepository
        .findById(query.schoolId())
        .orElseThrow(() -> new NotFoundException("School", query.schoolId().toString()));
  }
}
