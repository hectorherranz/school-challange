package com.hectorherranz.schoolapi.adapters.in.rest.mapper;

import com.hectorherranz.schoolapi.adapters.in.rest.dto.SchoolDetail;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.SchoolRequest;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.SchoolSummary;
import com.hectorherranz.schoolapi.application.command.CreateSchoolCommand;
import com.hectorherranz.schoolapi.application.command.UpdateSchoolCommand;
import com.hectorherranz.schoolapi.domain.model.School;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SchoolDtoMapper {

  public CreateSchoolCommand toCreateCommand(SchoolRequest request) {
    return new CreateSchoolCommand(request.name(), request.capacity());
  }

  public UpdateSchoolCommand toUpdateCommand(java.util.UUID schoolId, SchoolRequest request) {
    return new UpdateSchoolCommand(
        schoolId, Optional.of(request.name()), Optional.of(request.capacity()));
  }

  public SchoolDetail toDetail(School school) {
    return new SchoolDetail(
        school.id(), school.name(), school.capacity().value(), school.enrolledCount());
  }

  public SchoolSummary toSummary(School school) {
    return new SchoolSummary(
        school.id(), school.name(), school.capacity().value(), school.enrolledCount());
  }
}
