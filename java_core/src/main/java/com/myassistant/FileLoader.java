package com.myassistant;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class FileLoader {
    public static String loadFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }
}

