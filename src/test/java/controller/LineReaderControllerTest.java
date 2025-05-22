package controller;

import com.lineReader.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.lineReader.controller.*;
import org.lineReader.service.implementation.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.http.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineReaderControllerTest {

    @InjectMocks
    private LineReaderController lineReaderController;

    @Mock
    private LineReaderService lineReaderService;

    @Test
    void shouldGetLineFromFile() {
        String expectedResultString = "test_result";
        Line lineResult = Line.builder().content(expectedResultString).build();
        when(this.lineReaderService.getLineFromFile(anyInt())).thenReturn(lineResult);
        Random random = new Random();
        // Random value between 0 and 9999
        Integer randomIndex = random.nextInt(10000);

        ResponseEntity<Line> result = this.lineReaderController.getLineFromFile(randomIndex);
        assertNotNull(result);
        // Assert Status Code
        assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
        // Assert Body Results
        assertNotNull(result.getBody());
        assertEquals(expectedResultString, result.getBody().getContent());

    }
}
