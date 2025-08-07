package com.hectorherranz.schoolapi.application.command;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record DeleteStudentCommand(@NotNull UUID studentId) {}
