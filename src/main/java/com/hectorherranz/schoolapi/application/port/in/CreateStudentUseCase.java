package com.hectorherranz.schoolapi.application.port.in;

import com.hectorherranz.schoolapi.application.command.CreateStudentCommand;

import java.util.UUID;

public interface CreateStudentUseCase {
    UUID handle(CreateStudentCommand command);
}
