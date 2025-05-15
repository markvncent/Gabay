package frontend.comparison;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to manage candidate data for comparison panels
 */
public class CandidateDataManager {
    // Path to candidates.txt
    private static final String CANDIDATES_FILE = "resources/data/candidates.txt";
    
    // Path to default profile image
    private static final String DEFAULT_PROFILE_IMAGE = "resources/images/defaultprofpic.png";
    
    // Cache of candidate data
    private static final Map<String, Map<String, String>> candidateCache = new HashMap<>();
    
    // Cache for images to avoid repeated loading
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();
    
    // Social stances list from file
    private static final List<String> socialStancesList = new ArrayList<>();
    
    // Default profile image (loaded once)
    private static BufferedImage defaultProfileImage;
    
    // Static initializer to load default profile image
    static {
        try {
            File defaultImageFile = new File(DEFAULT_PROFILE_IMAGE);
            if (defaultImageFile.exists()) {
                defaultProfileImage = ImageIO.read(defaultImageFile);
                System.out.println("Loaded default profile image: " + DEFAULT_PROFILE_IMAGE);
            } else {
                System.err.println("Default profile image not found: " + DEFAULT_PROFILE_IMAGE);
                // Create a blank image as fallback
                defaultProfileImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            }
        } catch (IOException e) {
            System.err.println("Error loading default profile image: " + e.getMessage());
            defaultProfileImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        }
    }
    
    /**
     * Get candidate data by name
     * @param name Candidate's full name
     * @return Map of candidate data attributes, or null if not found
     */
    public static Map<String, String> getCandidateByName(String name) {
        // Check if we have this candidate in cache
        if (candidateCache.containsKey(name)) {
            return candidateCache.get(name);
        }
        
        // If not found, load all candidates to fill the cache
        loadAllCandidates();
        
        // Try to get from cache again
        return candidateCache.get(name);
    }
    
    /**
     * Get all social stance topics 
     * @return List of all social stance topics found in the data
     */
    public static List<String> getSocialStanceTopics() {
        if (socialStancesList.isEmpty()) {
            loadAllCandidates(); // This will populate the socialStancesList
        }
        return socialStancesList;
    }
    
    /**
     * Load all candidates from file and cache them
     */
    private static void loadAllCandidates() {
        try {
            File file = new File(CANDIDATES_FILE);
            if (!file.exists()) {
                System.err.println("Candidates file not found: " + CANDIDATES_FILE);
                return;
            }
            
            // Debug message
            System.out.println("Loading candidates from file: " + CANDIDATES_FILE);
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            Map<String, String> currentCandidate = null;
            String currentName = null;
            
            while ((line = reader.readLine()) != null) {
                // Empty line indicates end of current candidate record
                if (line.trim().isEmpty()) {
                    if (currentCandidate != null && currentName != null) {
                        candidateCache.put(currentName, currentCandidate);
                        currentCandidate = null;
                        currentName = null;
                    }
                    continue;
                }
                
                // Start of a new candidate record
                if (line.startsWith("Name:")) {
                    // Save previous candidate if exists
                    if (currentCandidate != null && currentName != null) {
                        candidateCache.put(currentName, currentCandidate);
                    }
                    
                    // Start new candidate
                    currentName = line.substring(6).trim();
                    currentCandidate = new HashMap<>();
                    currentCandidate.put("Name", currentName);
                    continue;
                }
                
                // Process other attributes
                if (currentCandidate != null && line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        
                        // Handle special case for social stances
                        if (key.equals("Social Stance")) {
                            // Format is "Social Stance: Topic - Stance"
                            String socialStanceContent = value.trim();
                            
                            // Check if this has the form "Topic - Stance"
                            if (socialStanceContent.contains(" - ")) {
                                String[] stanceParts = socialStanceContent.split(" - ", 2);
                                if (stanceParts.length == 2) {
                                    String topic = stanceParts[0].trim();
                                    String stance = stanceParts[1].trim();
                                    
                                    // Store as "Social Stance: Topic" -> "Stance"
                                    String socialStanceKey = "Social Stance: " + topic;
                                    currentCandidate.put(socialStanceKey, stance);
                                    
                                    // Add topic to list of social stances if it's not already there
                                    if (!socialStancesList.contains(topic)) {
                                        socialStancesList.add(topic);
                                    }
                                    
                                    // Debug output
                                    System.out.println("Parsed social stance: " + socialStanceKey + " -> " + stance);
                                }
                            } else {
                                // Just in case it doesn't follow the expected format
                                currentCandidate.put(key, value);
                            }
                        } else {
                            // Store in candidate data for other key types
                            currentCandidate.put(key, value);
                            
                            // If this is a social stance entry with the full key, add to our list of stances
                            if (key.startsWith("Social Stance:")) {
                                String stanceTopic = key.substring("Social Stance:".length()).trim();
                                if (!socialStancesList.contains(stanceTopic)) {
                                    socialStancesList.add(stanceTopic);
                                }
                            }
                        }
                    }
                }
            }
            
            // Add the last candidate
            if (currentCandidate != null && currentName != null) {
                candidateCache.put(currentName, currentCandidate);
            }
            
            reader.close();
            System.out.println("Loaded " + candidateCache.size() + " candidates into cache");
            System.out.println("Found " + socialStancesList.size() + " social stance topics");
            
            // Debug: Print the first candidate's social stances
            if (!candidateCache.isEmpty()) {
                String firstCandidateName = candidateCache.keySet().iterator().next();
                Map<String, String> firstCandidate = candidateCache.get(firstCandidateName);
                System.out.println("\nSocial stances for " + firstCandidateName + ":");
                for (Map.Entry<String, String> entry : firstCandidate.entrySet()) {
                    if (entry.getKey().startsWith("Social Stance:")) {
                        System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error loading candidates: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get a specific attribute for a candidate
     * @param name Candidate name
     * @param attribute Attribute name to retrieve
     * @return Attribute value or "No Data" if not found
     */
    public static String getCandidateAttribute(String name, String attribute) {
        Map<String, String> candidate = getCandidateByName(name);
        if (candidate != null && candidate.containsKey(attribute)) {
            return candidate.get(attribute);
        }
        return "No Data";
    }
    
    /**
     * Get a list of platform items for a candidate
     * @param name Candidate name
     * @return List of platform items or empty list if none
     */
    public static List<String> getCandidatePlatforms(String name) {
        return parseListAttribute(name, "Platforms");
    }
    
    /**
     * Get a list of supported issues for a candidate
     * @param name Candidate name
     * @return List of supported issues or empty list if none
     */
    public static List<String> getCandidateSupportedIssues(String name) {
        return parseListAttribute(name, "Supported Issues");
    }
    
    /**
     * Get a list of opposed issues for a candidate
     * @param name Candidate name
     * @return List of opposed issues or empty list if none
     */
    public static List<String> getCandidateOpposedIssues(String name) {
        return parseListAttribute(name, "Opposed Issues");
    }
    
    /**
     * Get a list of notable laws enacted by a candidate
     * @param name Candidate name
     * @return List of notable laws or empty list if none
     */
    public static List<String> getCandidateNotableLaws(String name) {
        return parseListAttribute(name, "Notable Laws");
    }
    
    /**
     * Get a candidate's stance on a specific social issue
     * @param name Candidate name
     * @param issue Social issue
     * @return Stance (Agree, Disagree, Neutral, or No Data)
     */
    public static String getCandidateSocialStance(String name, String issue) {
        Map<String, String> candidate = getCandidateByName(name);
        if (candidate != null) {
            // Try direct key match first
            String exactKey = "Social Stance: " + issue;
            if (candidate.containsKey(exactKey)) {
                return candidate.get(exactKey);
            }
            
            // If not found, look for partial matches
            for (Map.Entry<String, String> entry : candidate.entrySet()) {
                String key = entry.getKey();
                
                // If it's a social stance key containing this issue
                if (key.startsWith("Social Stance:") && key.substring("Social Stance:".length()).trim().contains(issue)) {
                    // If it's a key with embedded stance (Social Stance: Topic - Stance)
                    if (key.contains(" - ")) {
                        return key.substring(key.lastIndexOf(" - ") + 3).trim();
                    } else {
                        // Otherwise return the value
                        return entry.getValue();
                    }
                }
            }
        }
        return "No Data";
    }
    
    /**
     * Get a candidate's profile image
     * @param name Candidate name
     * @return BufferedImage of the candidate's profile picture or default image if not found
     */
    public static BufferedImage getCandidateImage(String name) {
        // Check image cache first
        if (imageCache.containsKey(name)) {
            return imageCache.get(name);
        }
        
        // Get image path from candidate data
        String imagePath = getCandidateAttribute(name, "Image");
        
        // If no valid path, use default
        if (imagePath.equals("No Data")) {
            imageCache.put(name, defaultProfileImage);
            return defaultProfileImage;
        }
        
        // Try to load image from the specified path
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                BufferedImage image = ImageIO.read(imageFile);
                imageCache.put(name, image);
                return image;
            } else {
                System.out.println("Image file not found for " + name + ": " + imagePath + " - Using default image");
                imageCache.put(name, defaultProfileImage);
                return defaultProfileImage;
            }
        } catch (IOException e) {
            System.out.println("Error loading image for " + name + ": " + e.getMessage() + " - Using default image");
            imageCache.put(name, defaultProfileImage);
            return defaultProfileImage;
        }
    }
    
    /**
     * Get the default profile image
     * @return Default profile image
     */
    public static BufferedImage getDefaultProfileImage() {
        return defaultProfileImage;
    }
    
    /**
     * Parse a semicolon-separated list attribute
     * @param name Candidate name
     * @param attribute Attribute name
     * @return List of items
     */
    public static List<String> parseListAttribute(String name, String attribute) {
        List<String> result = new ArrayList<>();
        
        Map<String, String> candidate = getCandidateByName(name);
        if (candidate != null && candidate.containsKey(attribute)) {
            String value = candidate.get(attribute);
            // Split by semicolon and trim each item
            String[] items = value.split(";");
            for (String item : items) {
                String trimmed = item.trim();
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
        }
        
        return result;
    }
} 