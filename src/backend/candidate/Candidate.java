package backend.candidate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Candidate implements Serializable {
    private String name;
    private int age;
    private String position;
    private String partyAffiliation;
    private String region;
    private int yearsOfExperience;
    private String campaignSlogan;
    private List<String> platforms;
    private List<String> supportedIssues;
    private List<String> opposedIssues;
    private List<String> notableLaws;
    private String imagePath;
    private List<String> socialStance;

    // Default constructor
    public Candidate() {
        super();
        this.platforms = new ArrayList<>();
        this.supportedIssues = new ArrayList<>();
        this.opposedIssues = new ArrayList<>();
        this.notableLaws = new ArrayList<>();
        this.socialStance = new ArrayList<>();
    }

    public Candidate(String name, int age, String position, String partyAffiliation, String region, int yearsOfExperience, String campaignSlogan, List<String> platforms, List<String> supportedIssues, List<String> opposedIssues, List<String> notableLaws, String imagePath, List<String> socialStance) {
        this.name = name;
        this.age = age;
        this.position = position;
        this.partyAffiliation = partyAffiliation;
        this.region = region;
        this.yearsOfExperience = yearsOfExperience;
        this.campaignSlogan = campaignSlogan;
        this.platforms = platforms != null ? new ArrayList<>(platforms) : new ArrayList<>();
        this.supportedIssues = supportedIssues != null ? new ArrayList<>(supportedIssues) : new ArrayList<>();
        this.opposedIssues = opposedIssues != null ? new ArrayList<>(opposedIssues) : new ArrayList<>();
        this.notableLaws = notableLaws != null ? new ArrayList<>(notableLaws) : new ArrayList<>();
        this.imagePath = imagePath;
        this.socialStance = socialStance != null ? new ArrayList<>(socialStance) : new ArrayList<>();
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getPartyAffiliation() { return partyAffiliation; }
    public void setPartyAffiliation(String partyAffiliation) { this.partyAffiliation = partyAffiliation; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getCampaignSlogan() { return campaignSlogan; }
    public void setCampaignSlogan(String campaignSlogan) { this.campaignSlogan = campaignSlogan; }

    public List<String> getPlatforms() { return new ArrayList<>(platforms); }
    public void setPlatforms(List<String> platforms) { this.platforms = new ArrayList<>(platforms); }

    public List<String> getSupportedIssues() { return new ArrayList<>(supportedIssues); }
    public void setSupportedIssues(List<String> supportedIssues) { this.supportedIssues = new ArrayList<>(supportedIssues); }

    public List<String> getOpposedIssues() { return new ArrayList<>(opposedIssues); }
    public void setOpposedIssues(List<String> opposedIssues) { this.opposedIssues = new ArrayList<>(opposedIssues); }

    public List<String> getNotableLaws() { return new ArrayList<>(notableLaws); }
    public void setNotableLaws(List<String> notableLaws) { this.notableLaws = new ArrayList<>(notableLaws); }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public List<String> getSocialStance() { return new ArrayList<>(socialStance); }
    public void setSocialStance(List<String> socialStance) { this.socialStance = new ArrayList<>(socialStance); }

    @Override
    public String toString() {
        return name; // For display in list
    }

    // Convert candidate to string format for file storage
    public String toFileString() {
        return String.join("|",
            name,
            String.valueOf(age),
            position,
            partyAffiliation,
            region,
            String.valueOf(yearsOfExperience),
            campaignSlogan,
            String.join(";", platforms),
            String.join(";", supportedIssues),
            String.join(";", opposedIssues),
            String.join(";", notableLaws),
            imagePath != null ? imagePath : "",
            String.join(";", socialStance)
        );
    }

    // Create candidate from string format
    public static Candidate fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 13) return null;
        return new Candidate(
            parts[0],
            Integer.parseInt(parts[1]),
            parts[2],
            parts[3],
            parts[4],
            Integer.parseInt(parts[5]),
            parts[6],
            List.of(parts[7].split(";")),
            List.of(parts[8].split(";")),
            List.of(parts[9].split(";")),
            List.of(parts[10].split(";")),
            parts[11],
            List.of(parts[12].split(";"))
        );
    }
}