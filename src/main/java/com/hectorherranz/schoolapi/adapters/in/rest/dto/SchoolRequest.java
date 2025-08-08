package com.hectorherranz.schoolapi.adapters.in.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SchoolRequest(
    @NotBlank(message = "School name is required") String name,
    @Min(value = 50, message = "School capacity must be at least 50")
        @Max(value = 2000, message = "School capacity cannot exceed 2000")
        int capacity) {}
