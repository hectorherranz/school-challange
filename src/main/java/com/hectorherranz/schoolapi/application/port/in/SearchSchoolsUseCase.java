package com.hectorherranz.schoolapi.application.port.in;

import com.hectorherranz.schoolapi.application.query.SearchSchoolsQuery;
import com.hectorherranz.schoolapi.application.response.PagedResponse;
import com.hectorherranz.schoolapi.domain.model.School;

public interface SearchSchoolsUseCase {
    PagedResponse<School> handle(SearchSchoolsQuery query);
}
