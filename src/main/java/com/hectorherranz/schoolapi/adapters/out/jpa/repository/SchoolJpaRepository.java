package com.hectorherranz.schoolapi.adapters.out.jpa.repository;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.SchoolEntity;
import com.hectorherranz.schoolapi.adapters.out.jpa.mapper.SchoolEntityMapper;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository         // Spring component
class SchoolJpaRepository implements SchoolRepositoryPort {

    private final SpringDataSchoolRepository repo;

    public SchoolJpaRepository(SpringDataSchoolRepository repo) {
        this.repo = repo;
    }

    /* ---------- Reads ---------- */

    @Override
    public Optional<School> findById(UUID id) {
        return repo.findById(id).map(SchoolEntityMapper::toDomain);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return repo.existsByNameIgnoreCase(name);
    }

    @Override
    public Page<School> searchByName(String q, Pageable pageable) {
        return repo.findByNameContainingIgnoreCase(q, pageable)
                   .map(SchoolEntityMapper::toDomain);
    }

    /* ---------- Writes ---------- */

    @Transactional
    @Override
    public School save(School school) {
        SchoolEntity saved = repo.save(SchoolEntityMapper.toEntity(school));
        return SchoolEntityMapper.toDomain(saved);
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}
