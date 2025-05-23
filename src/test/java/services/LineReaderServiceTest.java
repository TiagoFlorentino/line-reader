package services;

import com.lineReader.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.lineReader.exceptions.*;
import org.lineReader.service.implementation.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.lang.reflect.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class LineReaderServiceTest {

    @InjectMocks
    private LineReaderService lineReaderService;

    private static final String FILE_BASE_PATH = "src/test/resources/readLineTestFile";

    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        // Set all of the service variables manually given the test don't have a application associated to them
        Field lineReadFilePath = LineReaderService.class.getDeclaredField("lineReadFilePath");
        lineReadFilePath.setAccessible(true);
        lineReadFilePath.set(lineReaderService, FILE_BASE_PATH);

        Field txtLineReadFilePath = LineReaderService.class.getDeclaredField("txtLineReadFilePath");
        txtLineReadFilePath.setAccessible(true);
        txtLineReadFilePath.set(lineReaderService, FILE_BASE_PATH + ".txt");

        Field idxLineReadFilePath = LineReaderService.class.getDeclaredField("idxLineReadFilePath");
        idxLineReadFilePath.setAccessible(true);
        idxLineReadFilePath.set(lineReaderService, FILE_BASE_PATH + ".idx");

        Field finishedIndexingFile = LineReaderService.class.getDeclaredField("finishedIndexingFile");
        finishedIndexingFile.setAccessible(true);
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        finishedIndexingFile.set(lineReaderService, atomicBoolean);
    }

    @Test
    void shouldHandleIndexZero() {
        assertThrows(GenericServiceException.class, () -> this.lineReaderService.getLineFromFile(0));
    }

    @Test
    public void shouldHandleMissingLineFile() throws NoSuchFieldException, IllegalAccessException {
        String fileName = "something_not_found";
        Field lineReadFilePath = LineReaderService.class.getDeclaredField("lineReadFilePath");
        lineReadFilePath.setAccessible(true);
        lineReadFilePath.set(lineReaderService, fileName);

        Field txtLineReadFilePath = LineReaderService.class.getDeclaredField("txtLineReadFilePath");
        txtLineReadFilePath.setAccessible(true);
        txtLineReadFilePath.set(lineReaderService, fileName + ".txt");

        assertThrows(GenericServiceException.class, () -> this.lineReaderService.getLineFromFile(1));
    }

    @Test
    public void shouldHandleMissingIndexFile() throws NoSuchFieldException, IllegalAccessException {
        String fileName = "something_not_found";
        Field idxLineReadFilePath = LineReaderService.class.getDeclaredField("idxLineReadFilePath");
        idxLineReadFilePath.setAccessible(true);
        idxLineReadFilePath.set(lineReaderService, fileName + ".idx");

        assertThrows(GenericServiceException.class, () -> this.lineReaderService.getLineFromFile(1));
    }

    @Test
    public void shouldGetLineFromIndexFile() throws Exception {
        String expectedResult = "This is line 2";
        Line resultLine = lineReaderService.getLineFromFile(2);
        assertEquals(expectedResult, resultLine.getContent());
    }

    @Test
    public void shouldGetLineFromLineFile() throws Exception {
        Field finishedIndexingFile = LineReaderService.class.getDeclaredField("finishedIndexingFile");
        finishedIndexingFile.setAccessible(true);
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        finishedIndexingFile.set(lineReaderService, atomicBoolean);

        String expectedResult = "This is line 3";
        Line resultLine = lineReaderService.getLineFromFile(3);
        assertEquals(expectedResult, resultLine.getContent());
    }
}