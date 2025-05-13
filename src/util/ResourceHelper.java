package util;

import java.io.File;
import java.net.URL;

/**
 * Helper class for handling resources in a platform-independent way.
 * This centralizes resource access and makes the code more robust across different environments.
 */
public class ResourceHelper {
    
    // Base paths
    private static final String RESOURCES_PATH = "resources";
    private static final String IMAGES_PATH = RESOURCES_PATH + File.separator + "images";
    private static final String FONTS_PATH = "lib" + File.separator + "fonts";
    private static final String DATA_PATH = RESOURCES_PATH + File.separator + "data";
    
    // Specific image directories
    private static final String CANDIDATE_SEARCH_PATH = IMAGES_PATH + File.separator + "Candidate Search";
    private static final String CANDIDATES_PATH = IMAGES_PATH + File.separator + "candidates";
    private static final String BUTTONS_PATH = IMAGES_PATH + File.separator + "Buttons Icon";
    
    /**
     * Returns a File object for a resource path, ensuring the path is constructed
     * with proper file separators for the current platform.
     * 
     * @param basePath The base path (like IMAGES_PATH)
     * @param fileName The file name with extension
     * @return A File object with the proper path
     */
    public static File getResourceFile(String basePath, String fileName) {
        return new File(basePath + File.separator + fileName);
    }
    
    /**
     * Returns a file from the images directory
     * 
     * @param fileName The image file name with extension
     * @return A File object for the image
     */
    public static File getImageFile(String fileName) {
        return getResourceFile(IMAGES_PATH, fileName);
    }
    
    /**
     * Returns a file from the Candidate Search images directory
     * 
     * @param fileName The image file name with extension
     * @return A File object for the image
     */
    public static File getCandidateSearchImageFile(String fileName) {
        return getResourceFile(CANDIDATE_SEARCH_PATH, fileName);
    }
    
    /**
     * Returns a file from the candidates directory
     * 
     * @param fileName The image file name with extension
     * @return A File object for the image
     */
    public static File getCandidateImageFile(String fileName) {
        return getResourceFile(CANDIDATES_PATH, fileName);
    }
    
    /**
     * Returns a file from the buttons directory
     * 
     * @param fileName The image file name with extension
     * @return A File object for the image
     */
    public static File getButtonImageFile(String fileName) {
        return getResourceFile(BUTTONS_PATH, fileName);
    }
    
    /**
     * Returns a font file from the fonts directory
     * 
     * @param fileName The font file name with extension
     * @return A File object for the font
     */
    public static File getFontFile(String fileName) {
        return getResourceFile(FONTS_PATH, fileName);
    }
    
    /**
     * Returns a data file from the data directory
     * 
     * @param fileName The data file name with extension
     * @return A File object for the data file
     */
    public static File getDataFile(String fileName) {
        return getResourceFile(DATA_PATH, fileName);
    }
    
    /**
     * Tries multiple paths to find an image, useful for fallback logic
     * 
     * @param fileName The image name
     * @param directories Array of directory paths to check
     * @return The first file that exists, or null if none found
     */
    public static File findImage(String fileName, String[] directories) {
        for (String directory : directories) {
            File file = new File(directory + File.separator + fileName);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }
    
    /**
     * Ensures a directory exists, creating it if necessary
     * 
     * @param path The directory path to check/create
     * @return true if the directory exists or was created, false otherwise
     */
    public static boolean ensureDirectoryExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return directory.isDirectory();
    }
    
    /**
     * Get a resource URL from the classpath (useful for loading from JAR files)
     * 
     * @param path The resource path
     * @return A URL to the resource, or null if not found
     */
    public static URL getResourceURL(String path) {
        return ResourceHelper.class.getClassLoader().getResource(path);
    }
} 