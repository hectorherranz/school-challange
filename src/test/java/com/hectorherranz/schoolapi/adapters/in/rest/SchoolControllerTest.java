package com.hectorherranz.schoolapi.adapters.in.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hectorherranz.schoolapi.adapters.in.rest.dto.SchoolDetail;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.SchoolRequest;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.SchoolSummary;
import com.hectorherranz.schoolapi.adapters.in.rest.mapper.SchoolDtoMapper;
import com.hectorherranz.schoolapi.application.command.CreateSchoolCommand;
import com.hectorherranz.schoolapi.application.command.UpdateSchoolCommand;
import com.hectorherranz.schoolapi.application.port.in.*;
import com.hectorherranz.schoolapi.application.response.PagedResponse;
import com.hectorherranz.schoolapi.domain.exception.CapacityExceededException;
import com.hectorherranz.schoolapi.domain.exception.DuplicateNameException;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.Student;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SchoolControllerTest {

  @Mock private CreateSchoolUseCase createSchoolUseCase;

  @Mock private GetSchoolByIdUseCase getSchoolByIdUseCase;

  @Mock private UpdateSchoolUseCase updateSchoolUseCase;

  @Mock private DeleteSchoolUseCase deleteSchoolUseCase;

  @Mock private SearchSchoolsUseCase searchSchoolsUseCase;

  @Mock private SchoolDtoMapper schoolDtoMapper;

  private SchoolController controller;

  @BeforeEach
  void setUp() {
    controller =
        new SchoolController(
            createSchoolUseCase,
            getSchoolByIdUseCase,
            updateSchoolUseCase,
            deleteSchoolUseCase,
            searchSchoolsUseCase,
            schoolDtoMapper);
  }

  @Test
  void givenValidRequest_whenCreateSchool_thenReturns201WithLocation() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    SchoolRequest request = new SchoolRequest("Hogwarts School", 500);
    CreateSchoolCommand expectedCommand = new CreateSchoolCommand("Hogwarts School", 500);
    School school = School.rehydrate(schoolId, "Hogwarts School", new Capacity(500), List.of());
    SchoolDetail expectedResponse = new SchoolDetail(schoolId, "Hogwarts School", 500, 0);

    when(schoolDtoMapper.toCreateCommand(request)).thenReturn(expectedCommand);
    when(createSchoolUseCase.handle(expectedCommand)).thenReturn(schoolId);
    when(getSchoolByIdUseCase.handle(any())).thenReturn(school);
    when(schoolDtoMapper.toDetail(school)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<SchoolDetail> response = controller.createSchool(request);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(schoolId, response.getBody().id());
    assertEquals("Hogwarts School", response.getBody().name());
    assertEquals(500, response.getBody().capacity());
    assertEquals(0, response.getBody().enrolledStudents());

    verify(schoolDtoMapper).toCreateCommand(request);
    verify(createSchoolUseCase).handle(expectedCommand);
    verify(getSchoolByIdUseCase).handle(any());
    verify(schoolDtoMapper).toDetail(school);
  }

  @Test
  void givenValidId_whenGetSchoolById_thenReturns200() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    School school = School.rehydrate(schoolId, "Hogwarts School", new Capacity(500), List.of());
    SchoolDetail expectedResponse = new SchoolDetail(schoolId, "Hogwarts School", 500, 0);

    when(getSchoolByIdUseCase.handle(any())).thenReturn(school);
    when(schoolDtoMapper.toDetail(school)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<SchoolDetail> response = controller.getSchoolById(schoolId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(schoolId, response.getBody().id());
    assertEquals("Hogwarts School", response.getBody().name());
    assertEquals(500, response.getBody().capacity());

    verify(getSchoolByIdUseCase).handle(any());
    verify(schoolDtoMapper).toDetail(school);
  }

  @Test
  void givenValidRequest_whenUpdateSchool_thenReturns200() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    SchoolRequest request = new SchoolRequest("Updated School Name", 600);
    UpdateSchoolCommand expectedCommand =
        new UpdateSchoolCommand(
            schoolId, java.util.Optional.of("Updated School Name"), java.util.Optional.of(600));
    School school = School.rehydrate(schoolId, "Updated School Name", new Capacity(600), List.of());
    SchoolDetail expectedResponse = new SchoolDetail(schoolId, "Updated School Name", 600, 0);

    when(schoolDtoMapper.toUpdateCommand(schoolId, request)).thenReturn(expectedCommand);
    doNothing().when(updateSchoolUseCase).handle(expectedCommand);
    when(getSchoolByIdUseCase.handle(any())).thenReturn(school);
    when(schoolDtoMapper.toDetail(school)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<SchoolDetail> response = controller.updateSchool(schoolId, request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(schoolId, response.getBody().id());
    assertEquals("Updated School Name", response.getBody().name());
    assertEquals(600, response.getBody().capacity());

    verify(schoolDtoMapper).toUpdateCommand(schoolId, request);
    verify(updateSchoolUseCase).handle(expectedCommand);
    verify(getSchoolByIdUseCase).handle(any());
    verify(schoolDtoMapper).toDetail(school);
  }

  @Test
  void givenValidId_whenDeleteSchool_thenReturns204() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    doNothing().when(deleteSchoolUseCase).handle(any());

    // Act
    ResponseEntity<Void> response = controller.deleteSchool(schoolId);

    // Assert
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());

    verify(deleteSchoolUseCase).handle(any());
  }

  @Test
  void givenValidQuery_whenSearchSchools_thenReturnsPagedResponse() {
    // Arrange
    UUID schoolId1 = UUID.randomUUID();
    UUID schoolId2 = UUID.randomUUID();

    School school1 = School.rehydrate(schoolId1, "Hogwarts School", new Capacity(500), List.of());
    School school2 =
        School.rehydrate(schoolId2, "Beauxbatons Academy", new Capacity(300), List.of());

    SchoolSummary summary1 = new SchoolSummary(schoolId1, "Hogwarts School", 500, 0);
    SchoolSummary summary2 = new SchoolSummary(schoolId2, "Beauxbatons Academy", 300, 0);

    PagedResponse<School> pagedResponse =
        new PagedResponse<>(List.of(school1, school2), 0, 20, 2, 1, false, false);

    when(searchSchoolsUseCase.handle(any())).thenReturn(pagedResponse);
    when(schoolDtoMapper.toSummary(school1)).thenReturn(summary1);
    when(schoolDtoMapper.toSummary(school2)).thenReturn(summary2);

    // Act
    ResponseEntity<PagedResponse<SchoolSummary>> response =
        controller.searchSchools("School", 0, 20);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().content().size());
    assertEquals(0, response.getBody().pageNumber());
    assertEquals(20, response.getBody().pageSize());
    assertEquals(2, response.getBody().totalElements());
    assertEquals(1, response.getBody().totalPages());
    assertFalse(response.getBody().hasNext());
    assertFalse(response.getBody().hasPrevious());

    verify(searchSchoolsUseCase).handle(any());
    verify(schoolDtoMapper, times(2)).toSummary(any());
  }

  @Test
  void givenEmptyQuery_whenSearchSchools_thenReturnsAllSchools() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    School school = School.rehydrate(schoolId, "Hogwarts School", new Capacity(500), List.of());
    SchoolSummary summary = new SchoolSummary(schoolId, "Hogwarts School", 500, 0);

    PagedResponse<School> pagedResponse =
        new PagedResponse<>(List.of(school), 0, 20, 1, 1, false, false);

    when(searchSchoolsUseCase.handle(any())).thenReturn(pagedResponse);
    when(schoolDtoMapper.toSummary(school)).thenReturn(summary);

    // Act
    ResponseEntity<PagedResponse<SchoolSummary>> response = controller.searchSchools("", 0, 20);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().content().size());
    assertFalse(response.getBody().hasNext());
    assertFalse(response.getBody().hasPrevious());

    verify(searchSchoolsUseCase).handle(any());
    verify(schoolDtoMapper).toSummary(school);
  }

  @Test
  void givenSchoolWithStudents_whenGetSchoolById_thenReturnsCorrectEnrolledCount() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    UUID studentId1 = UUID.randomUUID();
    UUID studentId2 = UUID.randomUUID();

    List<Student> students =
        List.of(
            new Student(studentId1, "Harry Potter", schoolId),
            new Student(studentId2, "Hermione Granger", schoolId));

    School school = School.rehydrate(schoolId, "Hogwarts School", new Capacity(500), students);
    SchoolDetail expectedResponse = new SchoolDetail(schoolId, "Hogwarts School", 500, 2);

    when(getSchoolByIdUseCase.handle(any())).thenReturn(school);
    when(schoolDtoMapper.toDetail(school)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<SchoolDetail> response = controller.getSchoolById(schoolId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(schoolId, response.getBody().id());
    assertEquals("Hogwarts School", response.getBody().name());
    assertEquals(500, response.getBody().capacity());
    assertEquals(2, response.getBody().enrolledStudents());

    verify(getSchoolByIdUseCase).handle(any());
    verify(schoolDtoMapper).toDetail(school);
  }

  @Test
  void givenPagedResults_whenSearchSchools_thenReturnsCorrectPagingFlags() {
    // Arrange
    UUID schoolId1 = UUID.randomUUID();
    UUID schoolId2 = UUID.randomUUID();

    School school1 = School.rehydrate(schoolId1, "Hogwarts School", new Capacity(500), List.of());
    School school2 =
        School.rehydrate(schoolId2, "Beauxbatons Academy", new Capacity(300), List.of());

    SchoolSummary summary1 = new SchoolSummary(schoolId1, "Hogwarts School", 500, 0);
    SchoolSummary summary2 = new SchoolSummary(schoolId2, "Beauxbatons Academy", 300, 0);

    // Simulate page 0 of 2 pages (hasNext = true, hasPrevious = false)
    PagedResponse<School> pagedResponse =
        new PagedResponse<>(List.of(school1, school2), 0, 20, 40, 2, true, false);

    when(searchSchoolsUseCase.handle(any())).thenReturn(pagedResponse);
    when(schoolDtoMapper.toSummary(school1)).thenReturn(summary1);
    when(schoolDtoMapper.toSummary(school2)).thenReturn(summary2);

    // Act
    ResponseEntity<PagedResponse<SchoolSummary>> response =
        controller.searchSchools("School", 0, 20);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().content().size());
    assertEquals(0, response.getBody().pageNumber());
    assertEquals(20, response.getBody().pageSize());
    assertEquals(40, response.getBody().totalElements());
    assertEquals(2, response.getBody().totalPages());
    assertTrue(response.getBody().hasNext());
    assertFalse(response.getBody().hasPrevious());

    verify(searchSchoolsUseCase).handle(any());
    verify(schoolDtoMapper, times(2)).toSummary(any());
  }

  // Negative path tests

  @Test
  void givenNonExistentId_whenGetSchoolById_thenThrowsNotFoundException() {
    // Arrange
    UUID nonExistentId = UUID.randomUUID();
    when(getSchoolByIdUseCase.handle(any())).thenThrow(new NotFoundException("School not found"));

    // Act & Assert
    assertThrows(
        NotFoundException.class,
        () -> {
          controller.getSchoolById(nonExistentId);
        });

    verify(getSchoolByIdUseCase).handle(any());
    verify(schoolDtoMapper, never()).toDetail(any());
  }

  @Test
  void givenDuplicateName_whenCreateSchool_thenThrowsDuplicateNameException() {
    // Arrange
    SchoolRequest request = new SchoolRequest("Hogwarts School", 500);
    CreateSchoolCommand expectedCommand = new CreateSchoolCommand("Hogwarts School", 500);

    when(schoolDtoMapper.toCreateCommand(request)).thenReturn(expectedCommand);
    when(createSchoolUseCase.handle(expectedCommand)).thenThrow(new DuplicateNameException());

    // Act & Assert
    assertThrows(
        DuplicateNameException.class,
        () -> {
          controller.createSchool(request);
        });

    verify(schoolDtoMapper).toCreateCommand(request);
    verify(createSchoolUseCase).handle(expectedCommand);
    verify(getSchoolByIdUseCase, never()).handle(any());
    verify(schoolDtoMapper, never()).toDetail(any());
  }

  @Test
  void givenOverCapacity_whenCreateSchool_thenThrowsCapacityExceededException() {
    // Arrange
    SchoolRequest request = new SchoolRequest("Hogwarts School", 500);
    CreateSchoolCommand expectedCommand = new CreateSchoolCommand("Hogwarts School", 500);

    when(schoolDtoMapper.toCreateCommand(request)).thenReturn(expectedCommand);
    when(createSchoolUseCase.handle(expectedCommand))
        .thenThrow(new CapacityExceededException(UUID.randomUUID()));

    // Act & Assert
    assertThrows(
        CapacityExceededException.class,
        () -> {
          controller.createSchool(request);
        });

    verify(schoolDtoMapper).toCreateCommand(request);
    verify(createSchoolUseCase).handle(expectedCommand);
    verify(getSchoolByIdUseCase, never()).handle(any());
    verify(schoolDtoMapper, never()).toDetail(any());
  }

  @Test
  void givenNonExistentId_whenUpdateSchool_thenThrowsNotFoundException() {
    // Arrange
    UUID nonExistentId = UUID.randomUUID();
    SchoolRequest request = new SchoolRequest("Updated School Name", 600);
    UpdateSchoolCommand expectedCommand =
        new UpdateSchoolCommand(
            nonExistentId,
            java.util.Optional.of("Updated School Name"),
            java.util.Optional.of(600));

    when(schoolDtoMapper.toUpdateCommand(nonExistentId, request)).thenReturn(expectedCommand);
    doThrow(new NotFoundException("School not found"))
        .when(updateSchoolUseCase)
        .handle(expectedCommand);

    // Act & Assert
    assertThrows(
        NotFoundException.class,
        () -> {
          controller.updateSchool(nonExistentId, request);
        });

    verify(schoolDtoMapper).toUpdateCommand(nonExistentId, request);
    verify(updateSchoolUseCase).handle(expectedCommand);
    verify(getSchoolByIdUseCase, never()).handle(any());
    verify(schoolDtoMapper, never()).toDetail(any());
  }

  @Test
  void givenNonExistentId_whenDeleteSchool_thenThrowsNotFoundException() {
    // Arrange
    UUID nonExistentId = UUID.randomUUID();
    doThrow(new NotFoundException("School not found")).when(deleteSchoolUseCase).handle(any());

    // Act & Assert
    assertThrows(
        NotFoundException.class,
        () -> {
          controller.deleteSchool(nonExistentId);
        });

    verify(deleteSchoolUseCase).handle(any());
  }
}
