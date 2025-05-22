package org.lineReader.controller;

import com.lineReader.controller.*;
import com.lineReader.model.*;
import lombok.*;
import org.lineReader.service.implementation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class LineReaderController implements LinesApi {

    private LineReaderService lineReaderService;

    @Override
    public ResponseEntity<Line> getLineFromFile(
            Integer lineIndex
    ) {
        Line line = this.lineReaderService.getLineFromFile(lineIndex);
        return ResponseEntity.ok(line);
    }
}