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
        
        public Candidate(String name, String position, String party, String region, String age) {
            this.name = name;
            this.position = position;
            this.party = party;
            this.region = region;
            this.age = age;
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
    }
    
    /**
     * Load candidates from the candidates.txt file
     * 
     * @return List of Candidate objects
     */
    public static List<Candidate> loadCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        
        // Define possible file paths to search
        String[] possiblePaths = {
            "candidates.txt",
            "./candidates.txt",
            "../candidates.txt",
            "src/candidates.txt",
            "./src/candidates.txt"
        };
        
        // Try to find and load the file from one of the possible paths
        File candidatesFile = null;
        for (String path : possiblePaths) {
            File testFile = new File(path);
            if (testFile.exists()) {
                candidatesFile = testFile;
                System.out.println("Found candidates file at: " + testFile.getAbsolutePath());
                break;
            }
        }
        
        // If file is not found, return empty list
        if (candidatesFile == null || !candidatesFile.exists()) {
            System.out.println("Candidates file not found in any of the searched locations.");
            return candidates;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(candidatesFile))) {
            String line;
            String name = null;
            String position = null;
            String party = null;
            String region = null;
            String age = null;
            
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    // If we have collected enough data for a candidate, add them to the list
                    if (name != null && position != null && party != null) {
                        candidates.add(new Candidate(name, position, party, region, age));
                        
                        // Reset candidate data
                        name = null;
                        position = null;
                        party = null;
                        region = null;
                        age = null;
                    }
                    continue;
                }
                
                // Parse each line based on prefix
                if (line.startsWith("Name:")) {
                    name = line.substring("Name:".length()).trim();
                } else if (line.startsWith("Positions:")) {
                    position = line.substring("Positions:".length()).trim();
                } else if (line.startsWith("Party Affiliation:")) {
                    party = line.substring("Party Affiliation:".length()).trim();
                } else if (line.startsWith("Region:")) {
                    region = line.substring("Region:".length()).trim();
                } else if (line.startsWith("Age:")) {
                    age = line.substring("Age:".length()).trim();
                }
            }
            
            // Add the last candidate if there's data
            if (name != null && position != null && party != null) {
                candidates.add(new Candidate(name, position, party, region, age));
            }
            
        } catch (IOException e) {
            System.out.println("Error reading candidates file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return candidates;
    }
} 