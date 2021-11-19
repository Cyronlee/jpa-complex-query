package io.github.cyronlee.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final String message;
    private List<Error> errors;

    @Data
    static class Error {
        private String resource;
        private String field;
        private String code;
    }

    public ErrorResponse(String message) {
        this.message = message;
    }

    public static ResponseEntity<ErrorResponse> of(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus).body(
                new ErrorResponse(message)
        );
    }

    public static ResponseEntity<ErrorResponse> internalServerError() {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    public static ResponseEntity<ErrorResponse> badRequest(String message) {
        return of(HttpStatus.BAD_REQUEST, message);
    }

    public static ResponseEntity<ErrorResponse> notFound() {
        return of(HttpStatus.NOT_FOUND, "Not Found");
    }
}
