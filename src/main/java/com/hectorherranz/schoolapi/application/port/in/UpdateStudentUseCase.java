package com.hectorherranz.schoolapi.application.port.in;

import com.hectorherranz.schoolapi.application.command.UpdateStudentCommand;

public interface UpdateStudentUseCase {
  void handle(UpdateStudentCommand command);
}
