package com.hectorherranz.schoolapi.application.query;

import org.springframework.data.domain.Pageable;

public record SearchSchoolsQuery(String query, Pageable pageable) {}
