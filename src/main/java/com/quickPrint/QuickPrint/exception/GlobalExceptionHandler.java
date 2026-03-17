package com.quickPrint.QuickPrint.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	// 1. Specific handler for Cafe Duplicate
    @ExceptionHandler(CafeAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleCafeExists(CafeAlreadyExistsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Registration Failed");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT); // 409 Conflict
    }
	// 2. Handling Validation Errors for @Valid type
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e){
		Map<String, String> errors = new HashMap<>();
		e.getBindingResult().getAllErrors().forEach((error)->{
			String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
		});
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
	// 3. Handling Invalid file type 
	@ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<Object> handleInvalidFile(InvalidFileTypeException ex) {
        return new ResponseEntity<>(Map.of("error", "Bad Request", "message", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
	// 4. handling file size exception
	@ExceptionHandler(FileSizeExceededException.class)
	public ResponseEntity<Map<String, String>> handleFileSizeExceeded(FileSizeExceededException ex) {
	    Map<String, String> error = new HashMap<>();
	    error.put("error", "File Too Large");
	    error.put("message", ex.getMessage());
	    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	// 5. handling max upload size
	@ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
	public ResponseEntity<Map<String, String>> handleMaxUploadSize(org.springframework.web.multipart.MaxUploadSizeExceededException ex) {
	    Map<String, String> error = new HashMap<>();
	    error.put("error", "Payload Too Large");
	    error.put("message", "File size is strictly limited to 20MB. Check your file and try again.");
	    return new ResponseEntity<>(error, HttpStatus.PAYLOAD_TOO_LARGE); // 413 Status
	}
	
	@ExceptionHandler(MyFileNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleNotFound(MyFileNotFoundException ex) {
	    Map<String, String> error = new HashMap<>();
	    error.put("error", "File Missing");
	    error.put("details", ex.getMessage());
	    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // 404
	}
	// 7. Handling Custom Runtime Errors (Email already exists type)
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, String>> handleRuntimeExceptions(RuntimeException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
