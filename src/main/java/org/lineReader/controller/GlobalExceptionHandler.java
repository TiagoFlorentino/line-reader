package org.lineReader.controller;

import com.lineReader.model.*;
import lombok.extern.slf4j.*;
import org.lineReader.exceptions.*;
import org.lineReader.messages.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static org.lineReader.messages.GenericMesssages.ERROR_MESSAGE_GENERIC;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GenericServiceException.class)
    public ResponseEntity<ErrorMessage> handleGenericError(GenericServiceException genericServiceException) {
        log.error(genericServiceException.getMessage());
        ErrorMessage errorMessage = ErrorMessage.builder().message(ERROR_MESSAGE_GENERIC).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }

    @ExceptionHandler(OutOfBoundsIndexException.class)
    public ResponseEntity<ErrorMessage> handleOutOfBounds(OutOfBoundsIndexException outOfBoundsIndexException) {
        log.error(outOfBoundsIndexException.getMessage());
        ErrorMessage errorMessage = ErrorMessage.builder().message(GenericMesssages.ERROR_MESSAGE_OUT_BOUNDS).build();
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorMessage);
    }
}
