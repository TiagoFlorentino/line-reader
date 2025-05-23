package org.lineReader.service.implementation;

import com.lineReader.model.*;
import lombok.extern.slf4j.*;
import org.lineReader.exceptions.*;
import org.lineReader.service.interfaces.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;

import java.io.*;
import java.nio.file.*;

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
        String expectedLine = null;

        try (BufferedReader lineReader = Files.newBufferedReader(lineReaderPath)) {

            for (int lineCounter = 1; lineCounter < lineIndex; lineCounter++) {
                // Iterate though the lines of the file
                if (ObjectUtils.isEmpty(lineReader.readLine())) {
                    // Stop execution if we find EOF - no need to continue through the loop
                    throw new OutOfBoundsIndexException(ERROR_MESSAGE_OUT_BOUNDS);
                }
            }

            expectedLine = lineReader.readLine();
            if (ObjectUtils.isEmpty(expectedLine)) {
                // The line we got to was the one after the EOF
                throw new OutOfBoundsIndexException(ERROR_MESSAGE_OUT_BOUNDS);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new GenericServiceException("IO Exception issues");
        }

        // Success!
        return Line.builder().content(expectedLine).build();
    }
}
