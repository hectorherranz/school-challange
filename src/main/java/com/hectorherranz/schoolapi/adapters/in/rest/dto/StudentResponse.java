package com.hectorherranz.schoolapi.adapters.in.rest.dto;

import java.util.UUID;

public record StudentResponse(UUID id, String name, UUID schoolId) {}
