package com.hectorherranz.schoolapi.domain.exception;

public class NotFoundException extends RuntimeException {
    
    public NotFoundException(String entityName) {
        super(entityName + " not found");
    }
    
    public NotFoundException(String entityName, String identifier) {
        super(entityName + " not found with identifier: " + identifier);
    }
}
