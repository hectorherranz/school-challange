package com.hectorherranz.schoolapi.application.port.in;

import com.hectorherranz.schoolapi.application.command.DeleteStudentCommand;

public interface DeleteStudentUseCase {
  void handle(DeleteStudentCommand command);
}
