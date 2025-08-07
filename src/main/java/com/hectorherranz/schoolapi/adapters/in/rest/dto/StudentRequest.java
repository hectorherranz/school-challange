package com.hectorherranz.schoolapi.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StudentRequest(
    @NotBlank(message = "Student name is required") String name,
    @NotNull(message = "School ID is required") UUID schoolId) {}
