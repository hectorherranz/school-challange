package com.hectorherranz.schoolapi.adapters.out.jpa.repository;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.SchoolEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface SpringDataSchoolRepository
        extends JpaRepository<SchoolEntity, UUID> {

    boolean existsByNameIgnoreCase(String name);

    Page<SchoolEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
