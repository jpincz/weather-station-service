package com.gamehouse.weather.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Test illegal argument");
        ResponseEntity<String> response = handler.handleIllegalArgumentException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Test illegal argument");
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");
        ResponseEntity<String> response = handler.handleEntityNotFoundException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Entity not found");
    }
}
