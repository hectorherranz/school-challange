package com.hectorherranz.schoolapi.application.query;

import org.springframework.data.domain.Pageable;
import java.util.UUID;

public record SearchStudentsQuery(UUID schoolId, String query, Pageable pageable) {}
