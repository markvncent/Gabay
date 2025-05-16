package backend.model;

/**
 * Candidate model class for the quiz matching feature
 */
public class Candidate {
    private String name;
    private String party;
    
    public Candidate(String name, String party) {
        this.name = name;
        this.party = party;
    }
    
    public String getName() {
        return name;
    }
    
    public String getParty() {
        return party;
    }
    
    @Override
    public String toString() {
        return name + " (" + party + ")";
    }
} 