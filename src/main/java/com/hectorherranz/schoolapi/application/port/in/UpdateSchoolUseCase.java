package com.hectorherranz.schoolapi.application.port.in;

import com.hectorherranz.schoolapi.application.command.UpdateSchoolCommand;

public interface UpdateSchoolUseCase {
  void handle(UpdateSchoolCommand command);
}
