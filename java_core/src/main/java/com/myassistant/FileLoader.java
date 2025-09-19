package com.myassistant;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {

    public static List<Path> getAllTextFiles(String folderPath) throws IOException {
        List<Path> filePaths = new ArrayList<>();

        // Walk through the folder and all subfolders
        Files.walk(Paths.get(folderPath))
                .filter(Files::isRegularFile)          // Only files, ignore directories
                .filter(path -> path.toString().endsWith(".txt")) // Only .txt files
                .forEach(filePaths::add);

        return filePaths;
    }

    public static String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }
}

