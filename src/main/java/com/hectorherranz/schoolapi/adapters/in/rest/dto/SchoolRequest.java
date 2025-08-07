package com.hectorherranz.schoolapi.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record SchoolRequest(
    @NotBlank(message = "School name is required")
    String name,
    
    @Positive(message = "School capacity must be positive")
    int capacity
) {}
