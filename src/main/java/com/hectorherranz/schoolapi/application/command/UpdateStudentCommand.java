package com.hectorherranz.schoolapi.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateStudentCommand(@NotNull UUID studentId, @NotBlank String name) {}
