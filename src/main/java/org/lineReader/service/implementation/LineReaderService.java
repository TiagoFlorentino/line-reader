package org.lineReader.service.implementation;

import com.lineReader.model.*;
import lombok.extern.slf4j.*;
import org.lineReader.exceptions.*;
import org.lineReader.service.interfaces.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.atomic.*;

import static org.lineReader.messages.GenericMesssages.*;

@Service
@Slf4j
public class LineReaderService implements LineReaderServiceInterface {


    @Value("${line-read-path}")
    private String lineReadFilePath;

    public void validateRequirements(Integer lineIndex, Path lineReaderPath) {
        // Validate that all the requirements are met in order to correctly return the result
        if (lineIndex < 1) {
            // File index needs to be positive
            log.error(ERROR_MESSAGE_INVALID_INDEX);
            throw new GenericServiceException(ERROR_MESSAGE_INVALID_INDEX);
        }

        if (!Files.exists(lineReaderPath)) {
            // File needs to be present
            log.error(ERROR_MESSAGE_FILE_MISSING);
            throw new GenericServiceException(ERROR_MESSAGE_FILE_MISSING);
        }
    }

    @Override
    @Cacheable(cacheNames = "linesCache", key = "#lineIndex")
    public Line getLineFromFile(Integer lineIndex) {
        Path lineReaderPath = Paths.get(this.lineReadFilePath);
        // Validate requirements before processing
        this.validateRequirements(lineIndex, lineReaderPath);

         AtomicInteger lineCounter = new AtomicInteger(1); // Integer final wrapper required for changes to be done within stream
        try (BufferedReader lineReader = Files.newBufferedReader(lineReaderPath)) {

            return lineReader.lines()
                    .filter(
                            // check for the line requested
                            line -> lineCounter.getAndIncrement() == lineIndex
                    )
                    .findFirst() // collect the result from the found line
                    .map(
                            // Build the result as an object
                            lineValue -> Line.builder().content(lineValue).build()
                    )
                    .orElseThrow(
                            // Index was invalid, throw out of bounds
                            () -> new OutOfBoundsIndexException(ERROR_MESSAGE_OUT_BOUNDS)
                    );

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new GenericServiceException("IO Exception issues");
        }
    }
}
