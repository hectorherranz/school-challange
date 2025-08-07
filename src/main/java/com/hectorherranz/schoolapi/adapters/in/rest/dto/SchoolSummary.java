package com.hectorherranz.schoolapi.adapters.in.rest.dto;

import java.util.UUID;

public record SchoolSummary(
    UUID id,
    String name,
    int capacity,
    int enrolledStudents
) {}
