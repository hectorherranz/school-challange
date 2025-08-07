package com.hectorherranz.schoolapi.domain.repository;

import com.hectorherranz.schoolapi.domain.model.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface SchoolRepositoryPort {
    Optional<School> findById(UUID id);
    boolean existsByNameIgnoreCase(String name);
    Page<School> searchByName(String q, Pageable pageable);
    School save(School school);          // create & update
    void deleteById(UUID id);
}
