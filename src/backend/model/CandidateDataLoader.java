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
     * List of common issues that can be searched for
     */
    public static final String[] COMMON_ISSUES = {
        "divorce", "sogie", "death penalty", "criminal", "federal", 
        "rotc", "marriage", "terror", "jeepney", "foreign", 
        "healthcare", "sex education", "minimum wage", "education",
        "abortion", "tax", "corruption", "debt", "farming", "agriculture",
        "transportation", "housing", "climate", "environment", "employment",
        "poverty", "inflation", "human rights", "security", "sovereignty",
        "west philippine sea", "china", "drugs", "covid", "election",
        "job creation", "police", "military", "constitutional", "charter change",
        "dynasty", "political dynasty", "media", "press freedom", "endo"
    };
    
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
        private String supportedIssues;
        private String opposedIssues;
        private List<String> socialStances = new ArrayList<>();
        private String platforms;
        private String notableLaws;
        
        public Candidate(String name, String position, String party, String region, String age) {
            this(name, position, party, region, age, null, null, null);
        }
        
        public Candidate(String name, String position, String party, String region, String age, String imagePath) {
            this(name, position, party, region, age, imagePath, null, null);
        }
        
        public Candidate(String name, String position, String party, String region, String age, 
                         String imagePath, String supportedIssues, String opposedIssues) {
            this.name = name;
            this.position = position;
            this.party = party;
            this.region = region;
            this.age = age;
            this.imagePath = imagePath != null ? imagePath : "resources/images/candidates/default_candidate.jpg";
            this.supportedIssues = supportedIssues;
            this.opposedIssues = opposedIssues;
            this.platforms = "";
            this.notableLaws = "";
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
        
        public String getSupportedIssues() {
            return supportedIssues;
        }
        
        public String getOpposedIssues() {
            return opposedIssues;
        }
        
        public List<String> getSocialStances() {
            return socialStances;
        }
        
        public void addSocialStance(String stance) {
            if (stance != null && !stance.isEmpty()) {
                socialStances.add(stance);
            }
        }
        
        public String getPlatforms() {
            return platforms;
        }
        
        public void setPlatforms(String platforms) {
            this.platforms = platforms;
        }
        
        public String getNotableLaws() {
            return notableLaws;
        }
        
        public void setNotableLaws(String notableLaws) {
            this.notableLaws = notableLaws;
        }
        
        /**
         * Check if this candidate has a stance on the given issue
         */
        public boolean hasStanceOn(String issue) {
            if (issue == null || issue.isEmpty()) {
                return false;
            }
            
            String lowerIssue = issue.toLowerCase();
            
            // Check if this is a single-word search - make it more flexible for partial matches
            boolean isPartialWordSearch = !lowerIssue.contains(" ") && lowerIssue.length() <= 12;
            
            // Check in social stances with more flexible matching for single words
            for (String stance : socialStances) {
                String lowerStance = stance.toLowerCase();
                
                // For single-word searches, look for the word anywhere in the stance
                if (isPartialWordSearch) {
                    // For single words, match if any word in the stance contains the search term
                    String[] stanceWords = lowerStance.split("\\s+");
                    for (String word : stanceWords) {
                        // Clean up the word from punctuation
                        word = word.replaceAll("[^a-z0-9]", "");
                        if (word.contains(lowerIssue) || lowerIssue.contains(word)) {
                            return true;
                        }
                    }
                }
                
                // Standard contains check
                if (lowerStance.contains(lowerIssue)) {
                    return true;
                }
            }
            
            // Handle misspellings in stance values (e.g., "nuetral" vs "neutral")
            String[] commonStanceValues = {"agree", "disagree", "neutral", "nuetral", "no data"};
            if (isPartialWordSearch) {
                for (String stanceValue : commonStanceValues) {
                    if (stanceValue.contains(lowerIssue) || lowerIssue.contains(stanceValue)) {
                        // Check if any stance contains this value
                        for (String stance : socialStances) {
                            String lowerStance = stance.toLowerCase();
                            if (lowerStance.contains(stanceValue)) {
                                return true;
                            }
                        }
                    }
                }
            }
            
            // Check in supported/opposed issues with more flexible matching
            String lowerSupportedIssues = supportedIssues != null ? supportedIssues.toLowerCase() : "";
            String lowerOpposedIssues = opposedIssues != null ? opposedIssues.toLowerCase() : "";
            
            // For single-word searches, check if any word in the issues contains the search term
            if (isPartialWordSearch) {
                // Check in supported issues
                String[] supportedWords = lowerSupportedIssues.split("\\s+");
                for (String word : supportedWords) {
                    // Clean the word
                    word = word.replaceAll("[^a-z0-9]", "");
                    if (!word.isEmpty() && (word.contains(lowerIssue) || lowerIssue.contains(word))) {
                        return true;
                    }
                }
                
                // Check in opposed issues
                String[] opposedWords = lowerOpposedIssues.split("\\s+");
                for (String word : opposedWords) {
                    // Clean the word
                    word = word.replaceAll("[^a-z0-9]", "");
                    if (!word.isEmpty() && (word.contains(lowerIssue) || lowerIssue.contains(word))) {
                        return true;
                    }
                }
            }
            
            // Standard contains checks
            if (lowerSupportedIssues.contains(lowerIssue) || lowerOpposedIssues.contains(lowerIssue)) {
                return true;
            }
            
            // Also try to match issue names that might be mentioned in stances
            for (String issueKeyword : new String[] {
                "divorce", "sogie", "death penalty", "criminal", "federal", 
                "rotc", "marriage", "terror", "jeepney", "foreign", 
                "healthcare", "sex education", "minimum wage", "education",
                "abortion", "tax", "corruption"
            }) {
                // If the query is related to this keyword
                if ((issueKeyword.contains(lowerIssue) || lowerIssue.contains(issueKeyword))) {
                    // Check if we have any stance on this issue
                    for (String stance : socialStances) {
                        if (stance.toLowerCase().contains(issueKeyword)) {
                            return true;
                        }
                    }
                }
            }
            
            return false;
        }

        /**
         * Get all social stance topics for this candidate
         * @return List of stance topics
         */
        public List<String> getSocialStanceTopics() {
            List<String> topics = new ArrayList<>();
            
            for (String stance : socialStances) {
                String topic = extractStanceTopic(stance);
                if (topic != null && !topic.isEmpty()) {
                    topics.add(topic);
                }
            }
            
            return topics;
        }

        /**
         * Extract the topic from a social stance string
         * @param stance The full social stance string (e.g., "Mandatory ROTC for Senior High Students - Agree")
         * @return The topic part (e.g., "Mandatory ROTC for Senior High Students")
         */
        private String extractStanceTopic(String stance) {
            if (stance == null || stance.isEmpty()) {
                return null;
            }
            
            // Find the last occurrence of " - " which separates topic from stance
            int separatorIndex = stance.lastIndexOf(" - ");
            if (separatorIndex > 0) {
                return stance.substring(0, separatorIndex).trim();
            }
            
            return stance; // Return full stance if separator not found
        }

        /**
         * Get formatted social stances as a single string
         */
        public String getFormattedSocialStances() {
            if (socialStances.isEmpty()) {
                return "";
            }
            
            StringBuilder result = new StringBuilder();
            for (String stance : socialStances) {
                result.append(stance).append("; ");
            }
            
            // Remove last separator
            if (result.length() > 2) {
                result.setLength(result.length() - 2);
            }
            
            return result.toString();
        }

        /**
         * Check if this candidate has a stance on the given topic, regardless of position
         */
        public boolean hasStanceOnTopic(String topic) {
            if (topic == null || topic.isEmpty()) {
                return false;
            }
            
            String lowerTopic = topic.toLowerCase();
            
            // Check if this candidate has any social stances on this topic
            for (String stance : socialStances) {
                String stanceTopic = extractStanceTopic(stance);
                if (stanceTopic != null && stanceTopic.toLowerCase().contains(lowerTopic)) {
                    return true;
                }
            }
            
            return false;
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
            String supportedIssues = null;
            String opposedIssues = null;
            String platforms = null;
            String notableLaws = null;
            Candidate currentCandidate = null;
            
            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }
                
                // If we encounter a new "Name:" entry and already have candidate data,
                // add the previous candidate to the list before starting the new one
                if (line.startsWith("Name:") && name != null) {
                    currentCandidate = new Candidate(name, position, party, region, age, imagePath, supportedIssues, opposedIssues);
                    if (platforms != null) {
                        currentCandidate.setPlatforms(platforms);
                    }
                    if (notableLaws != null) {
                        currentCandidate.setNotableLaws(notableLaws);
                    }
                    candidates.add(currentCandidate);
                    
                    // Reset candidate data
                    name = null;
                    position = null;
                    party = null;
                    region = null;
                    age = null;
                    imagePath = null;
                    supportedIssues = null;
                    opposedIssues = null;
                    platforms = null;
                    notableLaws = null;
                    currentCandidate = null;
                }
                
                // Parse each line based on prefix
                if (line.startsWith("Name:")) {
                    name = line.substring("Name:".length()).trim();
                } else if (line.startsWith("Position:")) {
                    position = line.substring("Position:".length()).trim();
                } else if (line.startsWith("Positions:")) {
                    // Support both singular and plural forms for backward compatibility
                    position = line.substring("Positions:".length()).trim();
                } else if (line.startsWith("Running Position:")) {
                    // Support the "Running Position:" format found in newer data entries
                    position = line.substring("Running Position:".length()).trim();
                } else if (line.startsWith("Party Affiliation:")) {
                    party = line.substring("Party Affiliation:".length()).trim();
                } else if (line.startsWith("Region:")) {
                    region = line.substring("Region:".length()).trim();
                } else if (line.startsWith("Hometown Region:")) {
                    // Support "Hometown Region:" as an alternative to "Region:"
                    region = line.substring("Hometown Region:".length()).trim();
                } else if (line.startsWith("Age:")) {
                    age = line.substring("Age:".length()).trim();
                } else if (line.startsWith("Image:")) {
                    imagePath = line.substring("Image:".length()).trim();
                } else if (line.startsWith("Supported Issues:")) {
                    supportedIssues = line.substring("Supported Issues:".length()).trim();
                } else if (line.startsWith("Opposed Issues:")) {
                    opposedIssues = line.substring("Opposed Issues:".length()).trim();
                } else if (line.startsWith("Platforms:") || line.startsWith("Platform:")) {
                    // Handle both singular and plural forms
                    String prefix = line.startsWith("Platforms:") ? "Platforms:" : "Platform:";
                    platforms = line.substring(prefix.length()).trim();
                } else if (line.startsWith("Notable Laws:") || line.startsWith("Notable Laws Enacted:")) {
                    // Handle different prefixes for notable laws
                    String prefix = line.startsWith("Notable Laws:") ? "Notable Laws:" : "Notable Laws Enacted:";
                    notableLaws = line.substring(prefix.length()).trim();
                } else if (line.startsWith("Social Stance:")) {
                    // Handle Social Stance entries
                    if (currentCandidate == null && name != null) {
                        // Create the candidate object if we haven't already
                        currentCandidate = new Candidate(name, position, party, region, age, imagePath, supportedIssues, opposedIssues);
                        if (platforms != null) {
                            currentCandidate.setPlatforms(platforms);
                        }
                        if (notableLaws != null) {
                            currentCandidate.setNotableLaws(notableLaws);
                        }
                    }
                    
                    if (currentCandidate != null) {
                        currentCandidate.addSocialStance(line.substring("Social Stance:".length()).trim());
                    }
                } else if (line.startsWith("Stances On Social Issues:") && currentCandidate == null && name != null) {
                    // Create the candidate object when we see the "Stances On Social Issues:" header
                    currentCandidate = new Candidate(name, position, party, region, age, imagePath, supportedIssues, opposedIssues);
                    if (platforms != null) {
                        currentCandidate.setPlatforms(platforms);
                    }
                    if (notableLaws != null) {
                        currentCandidate.setNotableLaws(notableLaws);
                    }
                }
            }
            
            // Add the last candidate if there's data
            if (name != null) {
                if (currentCandidate == null) {
                    currentCandidate = new Candidate(name, position, party, region, age, imagePath, supportedIssues, opposedIssues);
                    if (platforms != null) {
                        currentCandidate.setPlatforms(platforms);
                    }
                    if (notableLaws != null) {
                        currentCandidate.setNotableLaws(notableLaws);
                    }
                }
                candidates.add(currentCandidate);
            }
            
        } catch (IOException e) {
            System.out.println("Error reading candidates file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return candidates;
    }
    
    /**
     * Get a candidate by name
     * 
     * @param name The candidate name to search for
     * @return The candidate with the matching name, or null if not found
     */
    public Candidate getCandidateByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        
        List<Candidate> candidates = loadCandidates();
        for (Candidate candidate : candidates) {
            if (candidate.getName().equalsIgnoreCase(name) || 
                candidate.getName().toLowerCase().contains(name.toLowerCase()) ||
                name.toLowerCase().contains(candidate.getName().toLowerCase())) {
                return candidate;
            }
        }
        
        return null;
    }
    
    /**
     * Get related issues to a given query
     */
    public static List<String> getRelatedIssues(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerQuery = query.toLowerCase().trim();
        List<String> related = new ArrayList<>();
        
        for (String issue : COMMON_ISSUES) {
            // Add issue if it contains the query or query contains it
            if (issue.contains(lowerQuery) || lowerQuery.contains(issue)) {
                related.add(issue);
            }
        }
        
        return related;
    }
} 