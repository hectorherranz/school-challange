package com.hectorherranz.schoolapi.adapters.out.jpa.repository;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.SchoolEntity;
import com.hectorherranz.schoolapi.adapters.out.jpa.mapper.SchoolEntityMapper;
import com.hectorherranz.schoolapi.application.port.out.SchoolRepositoryPort;
import com.hectorherranz.schoolapi.domain.model.School;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public class SchoolJpaRepository implements SchoolRepositoryPort {

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
    return repo.findByNameContainingIgnoreCase(q, pageable).map(SchoolEntityMapper::toDomain);
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

  @Override
  public Optional<School> findByIdForStudentUpdate(UUID id) {
    return repo.findByIdWithStudentsOnly(id).map(SchoolEntityMapper::toDomain);
  }

  @Override
  public int countStudentsBySchoolId(UUID schoolId) {
    return repo.countStudentsBySchoolId(schoolId);
  }

  @Override
  public Optional<School> findByIdBasic(UUID id) {
    return repo.findByIdBasic(id).map(SchoolEntityMapper::toDomain);
  }

  @Override
  public Optional<School> findByIdBasicForUpdate(UUID id) {
    return repo.findByIdBasicForUpdate(id).map(SchoolEntityMapper::toDomain);
  }
}
