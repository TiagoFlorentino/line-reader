package services;

import com.lineReader.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.lineReader.exceptions.*;
import org.lineReader.service.implementation.*;
import org.mockito.junit.jupiter.*;
import org.springframework.test.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Allows the setup and destroy to be static so I can use BeforeAll and AfterAll
class LineReaderServiceTest {

    private LineReaderService lineReaderService;

    private Path testFile;

    @BeforeAll
    void setUp() throws IOException {
        lineReaderService = new LineReaderService();
        List<String> fileComposition = List.of(
                "Something on line 1",
                "Some other thing on line 2"
        );
        // Generate a temp file
        this.testFile = Files.createTempFile("some-multi-line", ".txt");
        // Write to it
        Files.write(this.testFile, fileComposition); // \n are added at the end of every line
        // Mock the file name
        ReflectionTestUtils.setField(this.lineReaderService, "lineReadFilePath", testFile.toString());
    }

    @AfterAll
    void destroyAfterAll() throws IOException {
        // Delete the file at end of execution
        Files.deleteIfExists(this.testFile);
    }

    @ParameterizedTest
    @MethodSource("provideSuccessTestingParameters")
    void shouldGetLineFromFile(Integer lineIndex, String expectedResultString) {
        Line result = this.lineReaderService.getLineFromFile(lineIndex);
        // Assert Body Results
        assertNotNull(result);
        assertEquals(expectedResultString, result.getContent());
    }

    @Test
    void shouldHandleIndexZero() {
        assertThrows(GenericServiceException.class, () -> this.lineReaderService.getLineFromFile(0));
    }

    @Test
    void shouldHandleFileDoesNotExist() {
        ReflectionTestUtils.setField(this.lineReaderService, "lineReadFilePath", "something.txt");
        assertThrows(GenericServiceException.class, () -> this.lineReaderService.getLineFromFile(0));
        ReflectionTestUtils.setField(this.lineReaderService, "lineReadFilePath", testFile.toString());
    }

    @Test
    void shouldHandleIndexTooHigh() {
        assertThrows(OutOfBoundsIndexException.class, () -> this.lineReaderService.getLineFromFile(100));
    }

    static Stream<Arguments> provideSuccessTestingParameters() {
        return Stream.of(
                Arguments.of(1, "Something on line 1"),
                Arguments.of(2, "Some other thing on line 2")
        );
    }
}
