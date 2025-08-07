package com.hectorherranz.schoolapi.application.port.in;

import com.hectorherranz.schoolapi.application.query.GetSchoolByIdQuery;
import com.hectorherranz.schoolapi.domain.model.School;

public interface GetSchoolByIdUseCase {
  School handle(GetSchoolByIdQuery query);
}
