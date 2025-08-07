package com.hectorherranz.schoolapi.application.port.in;

import com.hectorherranz.schoolapi.application.command.DeleteSchoolCommand;

public interface DeleteSchoolUseCase {
  void handle(DeleteSchoolCommand command);
}
