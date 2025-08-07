package com.hectorherranz.schoolapi.domain.exception;

public class DuplicateNameException extends RuntimeException {
  public DuplicateNameException() {
    super("A school with this name already exists");
  }
}
