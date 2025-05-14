package backend.util;

public class Constants {
    // File paths
    public static final String CANDIDATES_FILE = "data/candidates.txt";
    
    // File delimiters
    public static final String FIELD_DELIMITER = "|";
    public static final String LIST_DELIMITER = ";";
    
    // Validation constants
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MIN_AGE = 18;
    public static final int MAX_AGE = 100;
    public static final int MIN_EXPERIENCE = 0;
    public static final int MAX_EXPERIENCE = 80;
    
    // Error messages
    public static final String ERROR_INVALID_NAME = "Name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters";
    public static final String ERROR_INVALID_AGE = "Age must be between " + MIN_AGE + " and " + MAX_AGE;
    public static final String ERROR_INVALID_EXPERIENCE = "Experience must be between " + MIN_EXPERIENCE + " and " + MAX_EXPERIENCE + " years";
    public static final String ERROR_FILE_ACCESS = "Error accessing file: ";
    public static final String ERROR_INVALID_DATA = "Invalid data format in file";
} 