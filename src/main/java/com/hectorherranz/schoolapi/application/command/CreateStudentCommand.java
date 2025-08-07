package com.hectorherranz.schoolapi.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateStudentCommand(
    @NotBlank String name,
    @NotNull UUID schoolId
) {}
