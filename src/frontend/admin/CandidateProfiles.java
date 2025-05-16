package frontend.admin;

import java.io.*;
import java.util.*;

/**
 * CandidateProfiles - A utility class for managing candidate profile data
 * Handles loading, saving, and modifying candidate information
 */
public class CandidateProfiles {
    // Path to the candidate data file
    private static final String DATA_FILE_PATH = "resources/data/candidates.txt";
    
    // List of candidate profiles
    private static List<Map<String, String>> candidateList = new ArrayList<>();
    
    // Flag to indicate if data has been loaded
    private static boolean dataLoaded = false;
    
    /**
     * Load all candidate profiles from the data file
     * @return A list of maps, where each map represents a candidate's data
     */
    public static List<Map<String, String>> loadCandidates() {
        if (dataLoaded) {
            return candidateList;
        }
        
        candidateList.clear();
        
        try {
            File dataFile = new File(DATA_FILE_PATH);
            if (!dataFile.exists()) {
                System.out.println("Candidate data file not found. Creating empty file.");
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
                dataLoaded = true;
                return candidateList;
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(dataFile));
            String line;
            Map<String, String> candidateData = null;
            Map<String, String> socialStances = new HashMap<>();
            boolean inSocialStancesSection = false;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip comment lines and empty lines
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                
                // If this line starts with "Name:", it's the beginning of a new candidate
                if (line.startsWith("Name:")) {
                    // If we have previous candidate data, add collected social stances and add to list
                    if (candidateData != null) {
                        // Format social stances collected so far
                        if (!socialStances.isEmpty()) {
                            candidateData.put("Social Stances", formatSocialStances(socialStances));
                        }
                        candidateList.add(candidateData);
                        socialStances = new HashMap<>(); // Reset for next candidate
                    }
                    
                    // Start a new candidate
                    candidateData = new HashMap<>();
                    inSocialStancesSection = false;
                }
                
                // Check if we're entering the "Stances On Social Issues:" section
                if (line.equals("Stances On Social Issues:")) {
                    inSocialStancesSection = true;
                    continue; // Skip to next line
                }
                
                // Handle entries within the social stances section
                if (inSocialStancesSection && candidateData != null) {
                    // Format is expected to be "Issue - Stance"
                    int dashIndex = line.indexOf(" - ");
                    if (dashIndex > 0) {
                        String issue = line.substring(0, dashIndex).trim();
                        String stance = line.substring(dashIndex + 3).trim();
                        socialStances.put(issue, stance);
                    }
                    continue; // Skip the regular processing below
                }
                
                // Handle Social Stance lines (with the individual stance format)
                if (line.startsWith("Social Stance:") && candidateData != null) {
                    String stanceData = line.substring("Social Stance:".length()).trim();
                    // Format is expected to be "Issue - Stance"
                    int dashIndex = stanceData.indexOf(" - ");
                    if (dashIndex > 0) {
                        String issue = stanceData.substring(0, dashIndex).trim();
                        String stance = stanceData.substring(dashIndex + 3).trim();
                        socialStances.put(issue, stance);
                    }
                    continue; // Skip the regular processing below
                }
                
                // If we have an active candidate data map, parse the line
                if (candidateData != null && line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        
                        // Legacy format: if this is the old Social Stances format, parse it
                        if (key.equals("Social Stances")) {
                            Map<String, String> parsedStances = parseSocialStances(value);
                            for (Map.Entry<String, String> entry : parsedStances.entrySet()) {
                                socialStances.put(entry.getKey(), entry.getValue());
                            }
                        } else {
                            candidateData.put(key, value);
                        }
                    }
                }
            }
            
            // Add the last candidate if there is one
            if (candidateData != null && !candidateData.isEmpty()) {
                // Format social stances collected so far
                if (!socialStances.isEmpty()) {
                    candidateData.put("Social Stances", formatSocialStances(socialStances));
                }
                candidateList.add(candidateData);
            }
            
            reader.close();
            dataLoaded = true;
            
            // Sort by surname (last word in Name field)
            candidateList.sort((a, b) -> {
                String nameA = a.getOrDefault("Name", "").trim();
                String nameB = b.getOrDefault("Name", "").trim();
                String surnameA = nameA.isEmpty() ? "" : nameA.substring(nameA.lastIndexOf(' ') + 1).toLowerCase();
                String surnameB = nameB.isEmpty() ? "" : nameB.substring(nameB.lastIndexOf(' ') + 1).toLowerCase();
                int cmp = surnameA.compareTo(surnameB);
                if (cmp != 0) return cmp;
                // If surnames are the same, fallback to full name
                return nameA.compareToIgnoreCase(nameB);
            });
            
        } catch (IOException e) {
            System.err.println("Error loading candidate data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return candidateList;
    }
    
    /**
     * Save all candidate profiles to the data file
     * @return true if save was successful, false otherwise
     */
    public static boolean saveCandidates() {
        try {
            File dataFile = new File(DATA_FILE_PATH);
            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            }
            
            FileWriter writer = new FileWriter(dataFile);
            writer.write("# Candidate Profiles - Generated by Gabay Application\n");
            writer.write("# Last updated: " + new Date().toString() + "\n\n");
            
            // Define the order of fields to save
            List<String> fieldOrder = Arrays.asList(
                "Name",
                "Age",
                "Position",
                "Party Affiliation",
                "Region",
                "Notable Laws",
                "Campaign Slogan",
                "Years of Experience",
                "Platforms",
                "Supported Issues",
                "Opposed Issues",
                "Image"
            );
            
            for (Map<String, String> candidateData : candidateList) {
                // First write fields in the specified order
                for (String key : fieldOrder) {
                    if (candidateData.containsKey(key)) {
                        writer.write(key + ": " + candidateData.get(key) + "\n");
                    }
                }
                
                // Handle social stances specially - split them into multiple lines
                if (candidateData.containsKey("Social Stances")) {
                    String stancesData = candidateData.get("Social Stances");
                    if (stancesData != null && !stancesData.isEmpty()) {
                        // Parse the social stances string
                        String[] pairs = stancesData.split(";");
                        for (String pair : pairs) {
                            String[] parts = pair.split(":", 2);
                            if (parts.length == 2) {
                                writer.write("Social Stance: " + parts[0] + " - " + parts[1] + "\n");
                            }
                        }
                    }
                }
                
                // Then write any additional fields not specified in the order and not Social Stances
                for (Map.Entry<String, String> entry : candidateData.entrySet()) {
                    if (!fieldOrder.contains(entry.getKey()) && !entry.getKey().equals("Social Stances")) {
                        writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
                    }
                }
                
                writer.write("\n"); // Empty line between candidates
            }
            
            writer.close();
            return true;
            
        } catch (IOException e) {
            System.err.println("Error saving candidate data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Add a new candidate profile to the list
     * @param candidateData Map containing candidate information
     * @return true if candidate was added, false otherwise
     */
    public static boolean addCandidate(Map<String, String> candidateData) {
        // Make sure data is loaded
        if (!dataLoaded) {
            loadCandidates();
        }
        
        // Add candidate to the list
        candidateList.add(candidateData);
        
        // Save changes to file
        return saveCandidates();
    }
    
    /**
     * Update an existing candidate profile
     * @param index Index of the candidate to update
     * @param candidateData New candidate data
     * @return true if candidate was updated, false otherwise
     */
    public static boolean updateCandidate(int index, Map<String, String> candidateData) {
        // Make sure data is loaded
        if (!dataLoaded) {
            loadCandidates();
        }
        
        // Check bounds
        if (index < 0 || index >= candidateList.size()) {
            return false;
        }
        
        // Update candidate
        candidateList.set(index, candidateData);
        
        // Save changes to file
        return saveCandidates();
    }
    
    /**
     * Delete a candidate profile
     * @param index Index of the candidate to delete
     * @return true if candidate was deleted, false otherwise
     */
    public static boolean deleteCandidate(int index) {
        // Make sure data is loaded
        if (!dataLoaded) {
            loadCandidates();
        }
        
        // Check bounds
        if (index < 0 || index >= candidateList.size()) {
            return false;
        }
        
        // Delete candidate
        candidateList.remove(index);
        
        // Save changes to file
        return saveCandidates();
    }
    
    /**
     * Get a candidate profile by index
     * @param index Index of the candidate to return
     * @return Map containing candidate data, or null if not found
     */
    public static Map<String, String> getCandidate(int index) {
        // Make sure data is loaded
        if (!dataLoaded) {
            loadCandidates();
        }
        
        // Check bounds
        if (index < 0 || index >= candidateList.size()) {
            return null;
        }
        
        // Return a deep copy of the candidate data to prevent external changes
        return new HashMap<>(candidateList.get(index));
    }
    
    /**
     * Get the number of candidates in the list
     * @return Number of candidates
     */
    public static int getCandidateCount() {
        // Make sure data is loaded
        if (!dataLoaded) {
            loadCandidates();
        }
        
        return candidateList.size();
    }
    
    /**
     * Create an empty candidate with default values
     * @return Map with empty candidate data
     */
    public static Map<String, String> createEmptyCandidate() {
        Map<String, String> candidate = new HashMap<>();
        
        // Set default values for required fields
        candidate.put("Name", "");
        candidate.put("Age", "");
        candidate.put("Position", "");
        candidate.put("Party Affiliation", "");
        candidate.put("Region", "");
        candidate.put("Notable Laws", "");
        candidate.put("Campaign Slogan", "");
        candidate.put("Years of Experience", "");
        candidate.put("Platforms", "");
        candidate.put("Supported Issues", "");
        candidate.put("Opposed Issues", "");
        
        // Empty social stances
        candidate.put("Social Stances", "");
        
        return candidate;
    }
    
    /**
     * Format social stances as a single string for storage
     * @param stances Map of social stances
     * @return Formatted string representation
     */
    public static String formatSocialStances(Map<String, String> stances) {
        if (stances == null || stances.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        
        for (Map.Entry<String, String> entry : stances.entrySet()) {
            if (!first) {
                sb.append(";");
            }
            sb.append(entry.getKey()).append(":").append(entry.getValue());
            first = false;
        }
        
        return sb.toString();
    }
    
    /**
     * Parse social stances from a formatted string
     * @param stancesStr Formatted social stances string
     * @return Map of social stances
     */
    public static Map<String, String> parseSocialStances(String stancesStr) {
        Map<String, String> stances = new HashMap<>();
        
        if (stancesStr == null || stancesStr.isEmpty()) {
            return stances;
        }
        
        String[] pairs = stancesStr.split(";");
        for (String pair : pairs) {
            String[] parts = pair.split(":", 2);
            if (parts.length == 2) {
                stances.put(parts[0], parts[1]);
            }
        }
        
        return stances;
    }
} 