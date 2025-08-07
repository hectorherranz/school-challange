package com.hectorherranz.schoolapi.application.query;

import java.util.UUID;
import org.springframework.data.domain.Pageable;

public record SearchStudentsQuery(UUID schoolId, String query, Pageable pageable) {}
