package controller;

import com.lineReader.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.lineReader.controller.*;
import org.lineReader.exceptions.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lineReader.messages.GenericMesssages.ERROR_MESSAGE_GENERIC;
import static org.lineReader.messages.GenericMesssages.ERROR_MESSAGE_OUT_BOUNDS;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private static final String ERROR_MESSAGE = "error_failure";

    @Test
    void shouldHandleGenericError() {
        GenericServiceException genericServiceException = new GenericServiceException(ERROR_MESSAGE);
        ResponseEntity<ErrorMessage> result = this.globalExceptionHandler.handleGenericError(genericServiceException);

        assertNotNull(result);
        // Assert Status Code
        assertEquals(HttpStatusCode.valueOf(500), result.getStatusCode());
        // Assert Body Message
        assertNotNull(result.getBody());
        assertEquals(ERROR_MESSAGE_GENERIC, result.getBody().getMessage());
    }

    @Test
    void shouldHandleOutOfBounds() {
        OutOfBoundsIndexException outOfBoundsIndexException = new OutOfBoundsIndexException(ERROR_MESSAGE);
        ResponseEntity<ErrorMessage> result = this.globalExceptionHandler.handleOutOfBounds(outOfBoundsIndexException);
        assertNotNull(result);
        // Assert Status Code
        assertEquals(HttpStatusCode.valueOf(413), result.getStatusCode());
        // Assert Body Message
        assertNotNull(result.getBody());
        assertEquals(ERROR_MESSAGE_OUT_BOUNDS, result.getBody().getMessage());

    }
}
