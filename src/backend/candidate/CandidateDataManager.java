package backend.candidate;

import backend.util.FileUtils;
import backend.util.Constants;
import backend.util.ValidationUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CandidateDataManager {
    private List<Candidate> candidates;

    public CandidateDataManager() {
        candidates = new ArrayList<>();
        loadCandidates();
    }

    public void loadCandidates() {
        candidates.clear();
        FileUtils.ensureFileExists(Constants.CANDIDATES_FILE);
        
        String content = FileUtils.readFile(Constants.CANDIDATES_FILE);
        if (content.isEmpty()) return;
        
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            Candidate candidate = Candidate.fromFileString(line);
            if (candidate != null) {
                candidates.add(candidate);
            }
        }
    }

    public void saveCandidates() {
        StringBuilder content = new StringBuilder();
        for (Candidate candidate : candidates) {
            content.append(candidate.toFileString()).append("\n");
        }
        FileUtils.writeFile(Constants.CANDIDATES_FILE, content.toString());
    }

    public List<Candidate> getAllCandidates() {
        return new ArrayList<>(candidates);
    }

    public String addCandidate(Candidate candidate) {
        String validationError = ValidationUtils.validateCandidate(candidate);
        if (validationError != null) {
            return validationError;
        }
        
        candidates.add(candidate);
        saveCandidates();
        return null;
    }

    public String updateCandidate(int index, Candidate updatedCandidate) {
        String validationError = ValidationUtils.validateCandidate(updatedCandidate);
        if (validationError != null) {
            return validationError;
        }
        
        if (index >= 0 && index < candidates.size()) {
            candidates.set(index, updatedCandidate);
            saveCandidates();
            return null;
        }
        return "Invalid candidate index";
    }

    public String deleteCandidate(int index) {
        if (index >= 0 && index < candidates.size()) {
            candidates.remove(index);
            saveCandidates();
            return null;
        }
        return "Invalid candidate index";
    }

    public List<Candidate> searchCandidates(String query) {
        if (!ValidationUtils.isValidSearchQuery(query)) {
            return new ArrayList<>();
        }
        
        String lowerQuery = query.toLowerCase();
        return candidates.stream()
            .filter(c -> 
                c.getName().toLowerCase().contains(lowerQuery) ||
                c.getPosition().toLowerCase().contains(lowerQuery) ||
                c.getPartyAffiliation().toLowerCase().contains(lowerQuery) ||
                c.getRegion().toLowerCase().contains(lowerQuery) ||
                c.getCampaignSlogan().toLowerCase().contains(lowerQuery) ||
                c.getPlatforms().stream().anyMatch(p -> p.toLowerCase().contains(lowerQuery)) ||
                c.getSupportedIssues().stream().anyMatch(i -> i.toLowerCase().contains(lowerQuery)) ||
                c.getOpposedIssues().stream().anyMatch(i -> i.toLowerCase().contains(lowerQuery)) ||
                c.getNotableLaws().stream().anyMatch(l -> l.toLowerCase().contains(lowerQuery)) ||
                c.getSocialStance().stream().anyMatch(s -> s.toLowerCase().contains(lowerQuery)))
            .collect(Collectors.toList());
    }

    public Candidate getCandidate(int index) {
        if (index >= 0 && index < candidates.size()) {
            return candidates.get(index);
        }
        return null;
    }
} 