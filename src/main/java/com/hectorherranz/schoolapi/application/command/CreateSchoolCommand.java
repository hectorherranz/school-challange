package com.hectorherranz.schoolapi.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateSchoolCommand(@NotBlank String name, @Positive int capacity) {}
