package com.hectorherranz.schoolapi.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record StudentRequest(@NotBlank(message = "Student name is required") String name) {}
