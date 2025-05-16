package frontend.quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import backend.model.Candidate;
import frontend.comparison.MinimalScrollBarUI;

/**
 * Results component for the Candidate Quiz
 * This component displays matching candidates based on quiz answers
 */
public class Results extends JPanel {
    // Font variables
    private Font interRegular;
    private Font interMedium;
    private Font interSemiBold;
    private Font interBold;
    
    // Colors
    private Color primaryBlue = new Color(0x2B, 0x37, 0x80); // #2B3780
    private Color primaryRed = new Color(0xE9, 0x45, 0x40); // #E94540
    private Color textColor = new Color(0x47, 0x55, 0x69); // #475569
    private Color lightGray = new Color(0xF1, 0xF5, 0xF9); // #F1F5F9
    private Color darkGray = new Color(0x64, 0x74, 0x8B); // #64748B
    private Color agreeGreen = new Color(0x10, 0xB9, 0x81); // #10B981
    private Color disagreeRed = new Color(0xEF, 0x44, 0x44); // #EF4444
    
    // Component dimensions
    private int panelWidth = 900;
    private int panelHeight = 600;
    
    // Data
    private List<String> questions;
    private List<String> userResponses;
    private List<CandidateMatch> matchingCandidates;
    
    // UI Components
    private JPanel resultsPanel;
    private JPanel candidatesPanel;
    private JButton tryAgainButton;
    private JLabel titleLabel;
    
    // Animation properties
    private javax.swing.Timer fadeInTimer;
    private float opacity = 0.0f;
    
    // Callback for when user wants to try again
    private Runnable onTryAgain;
    
    // Scroll pane for candidates
    private JScrollPane scrollPane;
    
    /**
     * Class to represent a candidate match
     */
    private static class CandidateMatch {
        Candidate candidate;
        double matchPercentage;
        List<Integer> matchingQuestionIndices;
        
        public CandidateMatch(Candidate candidate, double matchPercentage, List<Integer> matchingQuestionIndices) {
            this.candidate = candidate;
            this.matchPercentage = matchPercentage;
            this.matchingQuestionIndices = matchingQuestionIndices;
        }
    }
    
    public Results() {
        // Load fonts
        loadFonts();
        
        // Set up panel properties
        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        
        // Initialize empty data
        questions = new ArrayList<>();
        userResponses = new ArrayList<>();
        matchingCandidates = new ArrayList<>();
        
        // Initialize UI components
        initializeUI();
        
        // Set up fade-in animation
        setupFadeInAnimation();
    }
    
    /**
     * Load fonts used in the UI
     */
    private void loadFonts() {
        interRegular = new Font("Inter", Font.PLAIN, 14);
        interMedium = new Font("Inter", Font.PLAIN, 14);
        interSemiBold = new Font("Inter", Font.BOLD, 16);
        interBold = new Font("Inter", Font.BOLD, 18);
        
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Font[] fonts = ge.getAllFonts();
            
            for (Font font : fonts) {
                String fontName = font.getFontName().toLowerCase();
                if (fontName.contains("inter")) {
                    if (fontName.contains("regular") || fontName.contains("normal")) {
                        interRegular = font.deriveFont(14f);
                    } else if (fontName.contains("medium")) {
                        interMedium = font.deriveFont(14f);
                    } else if (fontName.contains("semibold") || fontName.contains("semi bold") || fontName.contains("semi-bold")) {
                        interSemiBold = font.deriveFont(16f);
                    } else if (fontName.contains("bold") && !fontName.contains("semi")) {
                        interBold = font.deriveFont(18f);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading custom fonts: " + e.getMessage());
        }
    }
    
    /**
     * Initialize UI components
     */
    private void initializeUI() {
        // Create main panel with padding
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BorderLayout(0, 20));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        resultsPanel.setOpaque(false);
        
        // Title
        titleLabel = new JLabel("Your Candidate Matches");
        titleLabel.setFont(interBold.deriveFont(28f));
        titleLabel.setForeground(primaryBlue);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Candidates panel (will be populated with candidate cards)
        candidatesPanel = new JPanel();
        candidatesPanel.setLayout(new BoxLayout(candidatesPanel, BoxLayout.Y_AXIS));
        candidatesPanel.setOpaque(false);
        
        // Scroll pane for candidates
        scrollPane = new JScrollPane(candidatesPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Apply minimal scrollbar UI
        scrollPane.getVerticalScrollBar().setUI(new MinimalScrollBarUI());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Try again button
        tryAgainButton = createButton("Try Again", primaryRed);
        tryAgainButton.addActionListener(e -> {
            if (onTryAgain != null) {
                onTryAgain.run();
            }
        });
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(tryAgainButton);
        
        // Add components to main panel
        resultsPanel.add(titleLabel, BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to this panel
        add(resultsPanel, BorderLayout.CENTER);
    }
    
    /**
     * Create a styled button
     */
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Get button dimensions
                int width = getWidth();
                int height = getHeight();
                
                // Draw background
                if (getModel().isPressed()) {
                    // Darker when pressed
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    // Full color when hovered
                    g2d.setColor(color);
                } else {
                    // Light background with colored border when not hovered
                    g2d.setColor(lightGray);
                }
                
                // Fill rounded rectangle
                g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, 10, 10));
                
                // Draw border if not hovered/pressed
                if (!getModel().isRollover() && !getModel().isPressed()) {
                    g2d.setColor(color);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.draw(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, 10, 10));
                }
                
                // Draw text
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle textRect = fm.getStringBounds(text, g2d).getBounds();
                
                // Set text color
                if (getModel().isRollover() || getModel().isPressed()) {
                    g2d.setColor(Color.WHITE);
                } else {
                    g2d.setColor(color);
                }
                
                // Draw centered text
                int x = (width - textRect.width) / 2;
                int y = (height - textRect.height) / 2 + fm.getAscent();
                g2d.drawString(text, x, y);
            }
        };
        
        // Set button properties
        button.setFont(interSemiBold.deriveFont(16f));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 50));
        
        return button;
    }
    
    /**
     * Set quiz data and calculate matches
     */
    public void setQuizData(List<String> questions, List<String> userResponses) {
        this.questions = questions;
        this.userResponses = userResponses;
        
        // Calculate matching candidates
        calculateMatches();
        
        // Populate UI with matching candidates
        populateCandidateMatches();
    }
    
    /**
     * Calculate matching candidates based on quiz responses
     */
    private void calculateMatches() {
        matchingCandidates = new ArrayList<>();
        
        // Load candidates from file
        List<Candidate> candidates = loadCandidates();
        
        // For each candidate, calculate match percentage
        for (Candidate candidate : candidates) {
            // Simulate matching algorithm
            // In a real implementation, this would compare user responses with candidate positions
            List<Integer> matchingIndices = new ArrayList<>();
            Random random = new Random(candidate.getName().hashCode()); // Use name as seed for consistent results
            
            // Determine which questions match with this candidate
            for (int i = 0; i < userResponses.size(); i++) {
                // For demo purposes, use a random match with 60% probability
                if (random.nextDouble() < 0.6) {
                    matchingIndices.add(i);
                }
            }
            
            // Calculate match percentage
            double matchPercentage = (double) matchingIndices.size() / userResponses.size();
            
            // Create candidate match object
            CandidateMatch match = new CandidateMatch(candidate, matchPercentage, matchingIndices);
            matchingCandidates.add(match);
        }
        
        // Sort by match percentage (highest first)
        matchingCandidates.sort((a, b) -> Double.compare(b.matchPercentage, a.matchPercentage));
        
        // Limit to top 5 matches
        if (matchingCandidates.size() > 5) {
            matchingCandidates = matchingCandidates.subList(0, 5);
        }
    }
    
    /**
     * Load candidates from file
     */
    private List<Candidate> loadCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/data/candidates.txt"))) {
            String line;
            String name = null;
            String party = null;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                if (line.startsWith("Name:")) {
                    // Extract name from "Name: Fernando "Nanding" Reyes" format
                    name = line.substring(line.indexOf(":") + 1).trim();
                } else if (line.startsWith("Party Affiliation:")) {
                    // Extract party from "Party Affiliation: Partido ng Pagbabago at Pag-asa (PPP)" format
                    party = line.substring(line.indexOf(":") + 1).trim();
                    
                    // If we have both name and party, add the candidate
                    if (name != null && party != null) {
                        candidates.add(new Candidate(name, party));
                        name = null;
                        party = null;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading candidates: " + e.getMessage());
            
            // Add sample candidates if file loading fails
            candidates.add(new Candidate("Fernando \"Nanding\" Reyes", "Partido ng Pagbabago at Pag-asa (PPP)"));
            candidates.add(new Candidate("Maria \"Maring\" Villanueva-Santos", "Partido Demokratiko ng Pilipinas (PDP)"));
            candidates.add(new Candidate("Roberto \"Bert\" Gonzales Jr.", "Lakas ng Bayan (LNB)"));
            candidates.add(new Candidate("Danilo \"Danny\" Macaraig", "Partido ng Pagbabago at Pag-asa (PPP)"));
            candidates.add(new Candidate("Rosario \"Charo\" Lim-Tan", "Partido Demokratiko ng Pilipinas (PDP)"));
        }
        
        return candidates;
    }
    
    /**
     * Populate UI with candidate matches
     */
    private void populateCandidateMatches() {
        // Clear existing cards
        candidatesPanel.removeAll();
        
        // Add candidate cards
        for (CandidateMatch match : matchingCandidates) {
            JPanel card = createCandidateCard(match);
            candidatesPanel.add(card);
            candidatesPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        
        // Update UI
        candidatesPanel.revalidate();
        candidatesPanel.repaint();
    }
    
    /**
     * Set up fade-in animation
     */
    private void setupFadeInAnimation() {
        fadeInTimer = new javax.swing.Timer(30, e -> {
            opacity += 0.05f;
            if (opacity >= 1.0f) {
                opacity = 1.0f;
                fadeInTimer.stop();
            }
            repaint();
        });
    }
    
    /**
     * Start the fade-in animation
     */
    public void startFadeIn() {
        opacity = 0.0f;
        fadeInTimer.start();
    }
    
    /**
     * Set callback for when user wants to try again
     */
    public void setOnTryAgain(Runnable callback) {
        this.onTryAgain = callback;
    }
    
    /**
     * Create a candidate card panel
     */
    private JPanel createCandidateCard(CandidateMatch match) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background
                g2d.setColor(lightGray);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
            }
        };
        
        card.setLayout(new BorderLayout(15, 10));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(panelWidth - 100, 250));
        card.setPreferredSize(new Dimension(panelWidth - 100, 250));
        
        // Left panel for candidate info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        // Candidate name
        JLabel nameLabel = new JLabel(match.candidate.getName());
        nameLabel.setFont(interBold.deriveFont(20f));
        nameLabel.setForeground(primaryBlue);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Match percentage
        JLabel matchLabel = new JLabel(String.format("%.1f%% Match", match.matchPercentage * 100));
        matchLabel.setFont(interSemiBold.deriveFont(18f));
        matchLabel.setForeground(primaryRed);
        matchLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Candidate party
        JLabel partyLabel = new JLabel("<html><div style='width:200px'>" + match.candidate.getParty() + "</div></html>");
        partyLabel.setFont(interRegular.deriveFont(14f));
        partyLabel.setForeground(darkGray);
        partyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add to info panel
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(matchLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(partyLabel);
        infoPanel.add(Box.createVerticalGlue());
        
        // Right panel for matching views
        JPanel viewsPanel = new JPanel();
        viewsPanel.setLayout(new BoxLayout(viewsPanel, BoxLayout.Y_AXIS));
        viewsPanel.setOpaque(false);
        
        // Matching views label
        JLabel viewsLabel = new JLabel("Matching Views:");
        viewsLabel.setFont(interSemiBold.deriveFont(16f));
        viewsLabel.setForeground(textColor);
        viewsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        viewsPanel.add(viewsLabel);
        viewsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add matching views
        for (Integer index : match.matchingQuestionIndices) {
            if (index < questions.size()) {
                String question = questions.get(index);
                String response = userResponses.get(index);
                
                // Create view panel
                JPanel viewPanel = new JPanel();
                viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.Y_AXIS));
                viewPanel.setOpaque(false);
                viewPanel.setMaximumSize(new Dimension(panelWidth - 200, 70));
                viewPanel.setPreferredSize(new Dimension(panelWidth - 200, 70));
                
                // Question text with text wrapping
                JLabel questionLabel = new JLabel("<html><div style='width: 350px'>" + question + "</div></html>");
                questionLabel.setFont(interRegular.deriveFont(14f));
                questionLabel.setForeground(textColor);
                questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                // Response label
                JLabel responseLabel = new JLabel("Your answer: " + response);
                responseLabel.setFont(interMedium.deriveFont(13f));
                responseLabel.setForeground(response.equals("Agree") ? agreeGreen : 
                                          response.equals("Disagree") ? disagreeRed : darkGray);
                responseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                responseLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
                
                // Add to view panel
                viewPanel.add(questionLabel);
                viewPanel.add(responseLabel);
                
                // Add to views panel
                viewsPanel.add(viewPanel);
                viewsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        // Add scrollable views panel with custom scrollbar
        JScrollPane viewsScrollPane = new JScrollPane(viewsPanel);
        viewsScrollPane.setOpaque(false);
        viewsScrollPane.getViewport().setOpaque(false);
        viewsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        viewsScrollPane.setPreferredSize(new Dimension(450, 180));
        
        // Apply minimal scrollbar UI
        viewsScrollPane.getVerticalScrollBar().setUI(new MinimalScrollBarUI());
        viewsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Add panels to card
        card.add(infoPanel, BorderLayout.WEST);
        card.add(viewsScrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Apply opacity to the whole panel
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
    }
} 