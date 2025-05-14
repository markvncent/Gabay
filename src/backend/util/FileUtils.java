package backend.util;

import java.io.*;
import java.nio.file.*;

public class FileUtils {
    private static final String DATA_DIR = "data";
    
    public static void ensureDirectoryExists(String path) {
        try {
            Path dirPath = Paths.get(path);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void ensureFileExists(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                ensureDirectoryExists(path.getParent().toString());
                Files.createFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String readFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static void writeFile(String filePath, String content) {
        try {
            Files.write(Paths.get(filePath), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String getDataDirectory() {
        return DATA_DIR;
    }
} 