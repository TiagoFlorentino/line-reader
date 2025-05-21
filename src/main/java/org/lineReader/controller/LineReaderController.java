package org.lineReader.controller;

import com.lineReader.controller.*;
import com.lineReader.model.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class LineReaderController implements LinesApi {

    @Override
    public ResponseEntity<Line> getLineFromFile(
            Integer lineIndex
    ) {
        return ResponseEntity.ok(Line.builder().content("123").build());
    }
}
