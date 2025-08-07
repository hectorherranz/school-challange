package com.hectorherranz.schoolapi.application.port.in;

import com.hectorherranz.schoolapi.application.command.CreateSchoolCommand;

import java.util.UUID;

public interface CreateSchoolUseCase {
    UUID handle(CreateSchoolCommand command);
}
