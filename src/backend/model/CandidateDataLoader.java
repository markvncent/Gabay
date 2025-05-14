package backend.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to load candidate data from the candidates.txt file
 */
public class CandidateDataLoader {
    
    /**
     * Represents a candidate with their basic information
     */
    public static class Candidate {
        private String name;
        private String position;
        private String party;
        private String region;
        private String age;
        private String imagePath;
        
        public Candidate(String name, String position, String party, String region, String age) {
            this(name, position, party, region, age, null);
        }
        
        public Candidate(String name, String position, String party, String region, String age, String imagePath) {
            this.name = name;
            this.position = position;
            this.party = party;
            this.region = region;
            this.age = age;
            this.imagePath = imagePath != null ? imagePath : "resources/images/candidates/default_candidate.jpg";
        }
        
        public String getName() {
            return name;
        }
        
        public String getPosition() {
            return position;
        }
        
        public String getParty() {
            return party;
        }
        
        public String getRegion() {
            return region;
        }
        
        public String getAge() {
            return age;
        }
        
        public String getImagePath() {
            return imagePath;
        }
    }
    
    /**
     * Load candidates from the data file
     */
    public static List<Candidate> loadCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        
        try {
            // Try to load from the resources directory first
            File candidatesFile = new File("resources/data/candidates.txt");
            
            // If not found, try alternative paths
            if (!candidatesFile.exists()) {
                // Try various alternative paths
                String[] possiblePaths = {
                    "data/candidates.txt",
                    "../resources/data/candidates.txt",
                    "candidates.txt"
                };
                
                for (String path : possiblePaths) {
                    File testFile = new File(path);
                    if (testFile.exists()) {
                        candidatesFile = testFile;
                        System.out.println("Found candidates file at: " + path);
                        break;
                    }
                }
            }
            
            if (!candidatesFile.exists()) {
                System.err.println("WARNING: candidates.txt file not found");
                return candidates; // Return empty list
            }
            
            // Read the file line by line
            BufferedReader reader = new BufferedReader(new FileReader(candidatesFile));
            String line;
            String name = null;
            String position = null;
            String party = null;
            String region = null;
            String age = null;
            String imagePath = null;
            
            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }
                
                // If we encounter a new "Name:" entry and already have candidate data,
                // add the previous candidate to the list before starting the new one
                if (line.startsWith("Name:") && name != null) {
                    candidates.add(new Candidate(name, position, party, region, age, imagePath));
                    
                    // Reset candidate data
                    name = null;
                    position = null;
                    party = null;
                    region = null;
                    age = null;
                    imagePath = null;
                }
                
                // Parse each line based on prefix
                if (line.startsWith("Name:")) {
                    name = line.substring("Name:".length()).trim();
                } else if (line.startsWith("Position:")) {
                    position = line.substring("Position:".length()).trim();
                } else if (line.startsWith("Positions:")) {
                    // Support both singular and plural forms for backward compatibility
                    position = line.substring("Positions:".length()).trim();
                } else if (line.startsWith("Party Affiliation:")) {
                    party = line.substring("Party Affiliation:".length()).trim();
                } else if (line.startsWith("Region:")) {
                    region = line.substring("Region:".length()).trim();
                } else if (line.startsWith("Age:")) {
                    age = line.substring("Age:".length()).trim();
                } else if (line.startsWith("Image:")) {
                    imagePath = line.substring("Image:".length()).trim();
                }
            }
            
            // Add the last candidate if there's data
            if (name != null) {
                candidates.add(new Candidate(name, position, party, region, age, imagePath));
            }
            
        } catch (IOException e) {
            System.out.println("Error reading candidates file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return candidates;
    }
} 