package com.hectorherranz.schoolapi.adapters.in.rest.dto;

import java.util.UUID;

public record SchoolDetail(UUID id, String name, int capacity, int enrolledStudents) {}
