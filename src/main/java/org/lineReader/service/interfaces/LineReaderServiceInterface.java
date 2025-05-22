package org.lineReader.service.interfaces;

import com.lineReader.model.*;

public interface LineReaderServiceInterface {

    // collects the contents of a line from a file
    Line getLineFromFile(Integer lineIndex);
}
