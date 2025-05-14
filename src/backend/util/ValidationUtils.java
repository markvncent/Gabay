package backend.util;

import backend.candidate.Candidate;
import java.util.List;

public class ValidationUtils {
    
    public static String validateCandidate(Candidate candidate) {
        if (candidate == null) {
            return "Candidate cannot be null";
        }
        
        // Validate name
        if (candidate.getName() == null || candidate.getName().trim().isEmpty()) {
            return Constants.ERROR_INVALID_NAME;
        }
        if (candidate.getName().length() < Constants.MIN_NAME_LENGTH || 
            candidate.getName().length() > Constants.MAX_NAME_LENGTH) {
            return Constants.ERROR_INVALID_NAME;
        }
        
        // Validate age
        if (candidate.getAge() < Constants.MIN_AGE || 
            candidate.getAge() > Constants.MAX_AGE) {
            return Constants.ERROR_INVALID_AGE;
        }
        
        // Validate position
        if (candidate.getPosition() == null || candidate.getPosition().trim().isEmpty()) {
            return "Position cannot be empty";
        }
        
        // Validate party affiliation
        if (candidate.getPartyAffiliation() == null || candidate.getPartyAffiliation().trim().isEmpty()) {
            return "Party affiliation cannot be empty";
        }
        
        // Validate years of experience
        if (candidate.getYearsOfExperience() < Constants.MIN_EXPERIENCE || 
            candidate.getYearsOfExperience() > Constants.MAX_EXPERIENCE) {
            return Constants.ERROR_INVALID_EXPERIENCE;
        }
        
        // Validate lists
        if (!validateList(candidate.getPlatforms())) {
            return "Platforms list cannot be null";
        }
        if (!validateList(candidate.getSupportedIssues())) {
            return "Supported issues list cannot be null";
        }
        if (!validateList(candidate.getOpposedIssues())) {
            return "Opposed issues list cannot be null";
        }
        if (!validateList(candidate.getNotableLaws())) {
            return "Notable laws list cannot be null";
        }
        if (!validateList(candidate.getSocialStance())) {
            return "Social stance list cannot be null";
        }
        
        return null; // No validation errors
    }
    
    private static boolean validateList(List<String> list) {
        return list != null;
    }
    
    public static boolean isValidSearchQuery(String query) {
        return query != null && !query.trim().isEmpty();
    }
} 