package com.hectorherranz.schoolapi.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hectorherranz.schoolapi.domain.exception.CapacityExceededException;
import com.hectorherranz.schoolapi.domain.exception.DuplicateNameException;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ApiExceptionHandlerProblemDetailTest {

  private ApiExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new ApiExceptionHandler();
  }

  @Test
  void handleNotFoundException_shouldReturnProblemDetailWith404() {
    // Arrange
    NotFoundException ex = new NotFoundException("School not found");

    // Act
    ResponseEntity<ProblemDetail> response = handler.handleNotFoundException(ex);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getHeaders().getContentType())
        .isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);

    ProblemDetail body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body).isInstanceOf(ProblemDetail.class);
  }

  @Test
  void handleCapacityExceededException_shouldReturnProblemDetailWith409() {
    // Arrange
    CapacityExceededException ex = new CapacityExceededException(java.util.UUID.randomUUID());

    // Act
    ResponseEntity<ProblemDetail> response = handler.handleCapacityExceededException(ex);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getHeaders().getContentType())
        .isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);

    ProblemDetail body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body).isInstanceOf(ProblemDetail.class);
  }

  @Test
  void handleDuplicateNameException_shouldReturnProblemDetailWith409() {
    // Arrange
    DuplicateNameException ex = new DuplicateNameException();

    // Act
    ResponseEntity<ProblemDetail> response = handler.handleDuplicateNameException(ex);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getHeaders().getContentType())
        .isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);

    ProblemDetail body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body).isInstanceOf(ProblemDetail.class);
  }

  @Test
  void handleValidationExceptions_shouldReturnProblemDetailWith400AndFieldErrors() {
    // Arrange
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError("school", "name", "Name cannot be empty");
    when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);

    // Act
    ResponseEntity<ProblemDetail> response = handler.handleValidationExceptions(ex);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getHeaders().getContentType())
        .isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);

    ProblemDetail body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body).isInstanceOf(ProblemDetail.class);
    assertThat(body.getProperties()).containsKey("errors");
  }

  @Test
  void handleOptimisticLockingFailureException_shouldReturnProblemDetailWith409() {
    // Arrange
    OptimisticLockingFailureException ex =
        new OptimisticLockingFailureException("Version conflict");

    // Act
    ResponseEntity<ProblemDetail> response = handler.handleOptimistic(ex);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getHeaders().getContentType())
        .isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);

    ProblemDetail body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body).isInstanceOf(ProblemDetail.class);
  }

  @Test
  void handleGenericException_shouldReturnProblemDetailWith500() {
    // Arrange
    Exception ex = new RuntimeException("Unexpected error");

    // Act
    ResponseEntity<ProblemDetail> response = handler.handleGenericException(ex);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getHeaders().getContentType())
        .isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);

    ProblemDetail body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body).isInstanceOf(ProblemDetail.class);
  }

  @Test
  void allHandlers_shouldReturnProblemDetailNotMap() {
    // This test ensures that all handlers return ProblemDetail objects, not raw maps
    // If any handler returns a Map instead of ProblemDetail, this test will fail

    NotFoundException notFoundEx = new NotFoundException("Not found");
    CapacityExceededException capacityEx =
        new CapacityExceededException(java.util.UUID.randomUUID());
    DuplicateNameException duplicateEx = new DuplicateNameException();
    OptimisticLockingFailureException optimisticEx =
        new OptimisticLockingFailureException("Conflict");
    Exception genericEx = new RuntimeException("Error");

    // Verify all responses are ProblemDetail objects
    assertThat(handler.handleNotFoundException(notFoundEx).getBody())
        .isInstanceOf(ProblemDetail.class);
    assertThat(handler.handleCapacityExceededException(capacityEx).getBody())
        .isInstanceOf(ProblemDetail.class);
    assertThat(handler.handleDuplicateNameException(duplicateEx).getBody())
        .isInstanceOf(ProblemDetail.class);
    assertThat(handler.handleOptimistic(optimisticEx).getBody()).isInstanceOf(ProblemDetail.class);
    assertThat(handler.handleGenericException(genericEx).getBody())
        .isInstanceOf(ProblemDetail.class);
  }
}
