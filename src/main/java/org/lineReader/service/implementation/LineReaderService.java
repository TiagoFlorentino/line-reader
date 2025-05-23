package org.lineReader.service.implementation;

import com.lineReader.model.*;
import lombok.extern.slf4j.*;
import org.lineReader.exceptions.*;
import org.lineReader.service.interfaces.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;

import javax.annotation.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.concurrent.atomic.*;

import static org.lineReader.messages.GenericMesssages.*;

@Service
@Slf4j
public class LineReaderService implements LineReaderServiceInterface {

    @Value("${line-read-path}")
    private String lineReadFilePath;

    private String txtLineReadFilePath;

    private String idxLineReadFilePath;

    private final AtomicBoolean finishedIndexingFile = new AtomicBoolean(false);

    private final Integer logLineRangeIndex = 10_000_000;


    private void validateRequirements(Integer lineIndex, Path lineReaderPath, Path indexFilePath) {
        // Validate generic requirements which are easy to stop
        if (lineIndex < 1) {
            // Index should be positive and higher than 0
            log.error(ERROR_MESSAGE_INVALID_INDEX);
            throw new GenericServiceException(ERROR_MESSAGE_INVALID_INDEX);
        }

        if (!Files.exists(lineReaderPath)) {
            // File to read exists in the project
            log.error(ERROR_MESSAGE_FILE_MISSING);
            throw new GenericServiceException(ERROR_MESSAGE_FILE_MISSING);
        }

        if (!ObjectUtils.isEmpty(indexFilePath)){
            if (!Files.exists(indexFilePath)) {
                // Index file is missing
                log.error(ERROR_MESSAGE_INDEX_FILE_MISSING);
                throw new GenericServiceException(ERROR_MESSAGE_FILE_MISSING);
            }
        }

    }

    @PostConstruct
    private void warmUpFileProcess() {
        // Warm up - index the required file into a separate file
        txtLineReadFilePath = lineReadFilePath + ".txt";
        idxLineReadFilePath = lineReadFilePath + ".idx";

        log.info(LOG_MESSAGE_START_INDEXING);
        Path readLineFilePath = Paths.get(txtLineReadFilePath);
        Path offSetFilePath = Paths.get(idxLineReadFilePath);

        try {
            // Delete the index the file on application start (given there might be new changes in the file)
            Files.deleteIfExists(Paths.get(idxLineReadFilePath));
        } catch (IOException e) {
            log.error(LOG_MESSAGE_FAILED_TO_DELETE_INDEX_FILE);
            return;
        }

        // Async process in order to not block the application
        new Thread(() -> {

            if (!Files.exists(readLineFilePath)) {
                // Cannot index a missing file
                log.error(LOG_MESSAGE_FILE_MISSING_AT_INDEX);
                return;
            }

            long currentOffset = 0;
            String currentLine;
            int indexedLineCount = 0;

            // Read from the line file
            try (BufferedReader lineReader = Files.newBufferedReader(readLineFilePath, StandardCharsets.UTF_8);
                 // Write to an index file
                 BufferedWriter offSetWriter = Files.newBufferedWriter(offSetFilePath, StandardCharsets.UTF_8)) {

                while (true) {
                    currentLine = lineReader.readLine();
                    if (ObjectUtils.isEmpty(currentLine)) {
                        // EOF, we can stop indexing!
                        break;
                    }

                    if (currentOffset != 0) {
                        // Skip the new line on the first iteration
                        offSetWriter.newLine();
                    }

                    // Write the offset - starts at 0 and then always write the previous offSet
                    offSetWriter.write(Long.toString(currentOffset));

                    currentOffset += currentLine.getBytes(StandardCharsets.UTF_8).length + 1; // The offset shifts the size of the line + 1 (in order to know where the line starts)
                    indexedLineCount++; // Next line

                    if (indexedLineCount % logLineRangeIndex == 0) {
                        // Log every 10 000 000 lines
                        log.info("Already indexed {} lines, still going ⏳", indexedLineCount);
                    }
                }

                // Finished indexing the whole file!
                finishedIndexingFile.set(true);
                log.info("Completed indexing! {} lines where indexed 🎉", indexedLineCount);
            } catch (IOException e) {
                log.error(e.getMessage());
                log.error("Failed to index the file!");
            }
        }).start();
    }

    private Line collectLineFromIndexedFile(Integer lineIndex, Path linePath, Path offSetPath) {
        // Read from the indexed file - optimized solution
        String offSetLine;
        try (BufferedReader offsetReader = Files.newBufferedReader(offSetPath, StandardCharsets.UTF_8)) {
            for (int lineCounter = 1; lineCounter < lineIndex; lineCounter++) {
                // Iterate though the lines of the file
                if (ObjectUtils.isEmpty(offsetReader.readLine())) {
                    // Out of bounds - we only use this solution if the full read line file was indexed
                    throw new OutOfBoundsIndexException(ERROR_MESSAGE_OUT_BOUNDS);
                }
            }
            offSetLine = offsetReader.readLine();
            if (ObjectUtils.isEmpty(offSetLine)) {
                // Expected line was empty, no value can be processed
                throw new OutOfBoundsIndexException(ERROR_MESSAGE_OUT_BOUNDS);
            }

            Long resultOffSet = Long.parseLong(offSetLine);

            try (RandomAccessFile randomAccessFile = new RandomAccessFile(linePath.toFile(), "r")) {
                // RandomAccessFile allows us to read from a file from a certain position (that we have calculated before)
                randomAccessFile.seek(resultOffSet);
                String lineResult = randomAccessFile.readLine();
                return Line.builder().content(lineResult).build();
            }

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new GenericServiceException(ERROR_MESSAGE_FAILED_READING_INDEX);
        }
    }

    private Line collectLineFromSampleFile(Integer lineIndex, Path linePath) {
        String expectedLine;
        try (BufferedReader lineReader = Files.newBufferedReader(linePath)) {

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
            throw new GenericServiceException(ERROR_MESSAGE_FAILED_READING);
        }

        // Success!
        return Line.builder().content(expectedLine).build();
    }


    @Override
    @Cacheable(cacheNames = "linesCache", key = "#lineIndex")
    public Line getLineFromFile(Integer lineIndex) {
        Path readFilePath = Paths.get(txtLineReadFilePath);

        if (finishedIndexingFile.get()) {
            // If the index file is complete, use the optimized solution
            Path offSetPath = Paths.get(idxLineReadFilePath);
            // Check requirements
            validateRequirements(lineIndex, readFilePath, offSetPath);
            return collectLineFromIndexedFile(lineIndex, readFilePath, offSetPath);
        } else {
            // Check requirements
            validateRequirements(lineIndex, readFilePath, null);
            // Default to go through the file
            return collectLineFromSampleFile(lineIndex, readFilePath);
        }
    }

}