package org.lineReader;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.cache.annotation.*;

@SpringBootApplication
@EnableCaching
public class LineReaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(LineReaderApplication.class, args);
    }
}