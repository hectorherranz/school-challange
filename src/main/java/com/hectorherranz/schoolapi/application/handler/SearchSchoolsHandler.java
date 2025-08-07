package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.port.in.SearchSchoolsUseCase;
import com.hectorherranz.schoolapi.application.query.SearchSchoolsQuery;
import com.hectorherranz.schoolapi.application.response.PagedResponse;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class SearchSchoolsHandler implements SearchSchoolsUseCase {

  private final SchoolRepositoryPort schoolRepository;

  public SearchSchoolsHandler(SchoolRepositoryPort schoolRepository) {
    this.schoolRepository = schoolRepository;
  }

  @Override
  public PagedResponse<School> handle(SearchSchoolsQuery query) {
    var page = schoolRepository.searchByName(query.query(), query.pageable());
    return PagedResponse.from(page);
  }
}
