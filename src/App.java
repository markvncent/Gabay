import backend.candidate.CandidateDataManager;
import frontend.landingpage.LandingPageUI;
import frontend.admin.AdminPanelUI;
import javax.swing.SwingUtilities;

public class App {
    private static CandidateDataManager candidateManager;

    public static void main(String[] args) {
        try {
            // Initialize the candidate manager
            candidateManager = new CandidateDataManager();
            
            // Start the UI
            SwingUtilities.invokeLater(() -> {
                LandingPageUI ui = new LandingPageUI();
                ui.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing application: " + e.getMessage());
        }
    }

    public static CandidateDataManager getCandidateManager() {
        return candidateManager;
    }
}
