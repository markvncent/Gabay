package frontend.quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Question component for the Candidate Quiz
 * This component displays a question with agree/disagree/neutral options
 */
public class Question extends JPanel {
    // Font variables
    private Font interRegular;
    private Font interMedium;
    private Font interSemiBold;
    
    // Colors
    private Color primaryBlue = new Color(0x2B, 0x37, 0x80); // #2B3780
    private Color neutralGray = new Color(0x64, 0x74, 0x8B); // #64748B
    private Color agreeGreen = new Color(0x10, 0xB9, 0x81); // #10B981
    private Color disagreeRed = new Color(0xEF, 0x44, 0x44); // #EF4444
    private Color textColor = new Color(0x47, 0x55, 0x69); // #475569
    private Color lightGray = new Color(0xF1, 0xF5, 0xF9); // #F1F5F9
    
    // Component dimensions
    private int panelWidth = 900;
    private int panelHeight = 600;
    
    // Questions
    private List<String> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    
    // UI Components
    private JLabel questionLabel;
    private JButton agreeButton;
    private JButton neutralButton;
    private JButton disagreeButton;
    private ProgressBar progressBar;
    
    // Animation properties
    private Timer fadeInTimer;
    private float opacity = 0.0f;
    
    // Question response tracking
    private List<String> responses = new ArrayList<>();
    
    // Callback for when quiz is completed
    private Runnable onQuizCompleted;
    
    public Question() {
        // Load fonts
        loadFonts();
        
        // Load questions
        loadQuestions();
        
        // Set up panel properties
        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        
        // Initialize UI components
        initializeUI();
        
        // Set up fade-in animation
        setupFadeInAnimation();
        
        // Debug output
        System.out.println("Question component initialized with " + questions.size() + " questions");
    }
    
    /**
     * Load fonts used in the UI
     */
    private void loadFonts() {
        interRegular = new Font("Inter", Font.PLAIN, 14);
        interMedium = new Font("Inter", Font.PLAIN, 14);
        interSemiBold = new Font("Inter", Font.BOLD, 16);
        
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
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading custom fonts: " + e.getMessage());
        }
    }
    
    /**
     * Load questions from file
     */
    private void loadQuestions() {
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/data/quizquestions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    // Normalize question to make it easier to match with candidate stances
                    line = normalizeQuestion(line.trim());
                    questions.add(line);
                }
            }
            System.out.println("Loaded " + questions.size() + " questions");
        } catch (IOException e) {
            System.err.println("Error loading questions: " + e.getMessage());
            // Add a default question in case file loading fails
            questions.add("Do you support this policy?");
        }
    }
    
    /**
     * Normalize question text to ensure consistent matching with candidate stances
     */
    private String normalizeQuestion(String question) {
        // Convert to lowercase for consistent matching
        String normalized = question.toLowerCase();
        
        // Replace common synonyms and related terms
        if (normalized.contains("legalization of divorce") || 
            normalized.contains("allow divorce") || 
            normalized.contains("divorce law")) {
            return "Do you support the legalization of divorce in the Philippines?";
        } else if (normalized.contains("sogie") || 
                  normalized.contains("sexual orientation") || 
                  normalized.contains("gender identity") || 
                  normalized.contains("gender expression") || 
                  normalized.contains("equality bill") ||
                  normalized.contains("lgbtq rights")) {
            return "Do you agree with passing the SOGIE Equality Bill?";
        } else if (normalized.contains("death penalty") || 
                  normalized.contains("capital punishment") || 
                  normalized.contains("execution for crimes")) {
            return "Should the death penalty be reinstated in the Philippines?";
        } else if (normalized.contains("criminal responsibility") || 
                  normalized.contains("juvenile justice") || 
                  normalized.contains("youth offenders") ||
                  normalized.contains("minor criminals")) {
            return "Do you support lowering the age of criminal responsibility?";
        } else if (normalized.contains("federalism") || 
                  normalized.contains("federal government") || 
                  normalized.contains("federal system")) {
            return "Are you in favor of federalism as a form of government?";
        } else if (normalized.contains("rotc") || 
                  normalized.contains("reserve officers") || 
                  normalized.contains("military training") ||
                  normalized.contains("mandatory training")) {
            return "Should mandatory ROTC be implemented for senior high school students?";
        } else if (normalized.contains("same-sex marriage") || 
                  normalized.contains("same sex marriage") || 
                  normalized.contains("gay marriage") || 
                  normalized.contains("lgbtq marriage")) {
            return "Do you agree with same-sex marriage being legalized?";
        } else if (normalized.contains("anti-terror") || 
                  normalized.contains("anti terror") || 
                  normalized.contains("terrorism") || 
                  normalized.contains("security law")) {
            return "Do you support the Anti-Terror Law?";
        } else if (normalized.contains("foreign investment") && normalized.contains("land")) {
            return "Should foreign investment in land ownership be allowed?";
        } else if (normalized.contains("healthcare") || 
                  normalized.contains("medical care") || 
                  normalized.contains("health insurance") ||
                  normalized.contains("universal healthcare")) {
            return "Do you agree with universal healthcare funding?";
        } else if (normalized.contains("sex education") || 
                  normalized.contains("sexual education") ||
                  normalized.contains("sex ed")) {
            return "Should mandatory sex education be implemented in schools?";
        } else if (normalized.contains("minimum wage") || 
                  normalized.contains("salary standard") || 
                  normalized.contains("wage standardization")) {
            return "Do you support the standardization of minimum wage across regions?";
        } else if (normalized.contains("jeepney modernization") || 
                  normalized.contains("public transport") || 
                  normalized.contains("transport modernization")) {
            return "Should the Jeepney Modernization Program be pursued?";
        }
        
        // If no match found, return the original question
        return question;
    }
    
    /**
     * Initialize UI components
     */
    private void initializeUI() {
        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 30));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        mainPanel.setOpaque(false);
        
        // Create question panel
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BorderLayout());
        questionPanel.setOpaque(false);
        
        // Question label
        questionLabel = new JLabel();
        questionLabel.setFont(interSemiBold.deriveFont(24f));
        questionLabel.setForeground(textColor);
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Progress bar (replacing progress label)
        progressBar = new ProgressBar();
        progressBar.setProgress(currentQuestionIndex, questions.size());
        
        // Add to question panel
        questionPanel.add(progressBar, BorderLayout.NORTH);
        questionPanel.add(questionLabel, BorderLayout.CENTER);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 3, 20, 0));
        buttonsPanel.setOpaque(false);
        
        // Create buttons
        disagreeButton = createOptionButton("Disagree", disagreeRed);
        neutralButton = createOptionButton("Neutral", neutralGray);
        agreeButton = createOptionButton("Agree", agreeGreen);
        
        // Add buttons to panel
        buttonsPanel.add(disagreeButton);
        buttonsPanel.add(neutralButton);
        buttonsPanel.add(agreeButton);
        
        // Add components to main panel
        mainPanel.add(questionPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
        
        // Display first question
        updateQuestion();
        
        // Debug output
        System.out.println("Question UI initialized with first question: " + 
                          (questions.isEmpty() ? "No questions available" : questions.get(0)));
    }
    
    /**
     * Create an option button with hover effects
     */
    private JButton createOptionButton(String text, Color color) {
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
        button.setPreferredSize(new Dimension(150, 60));
        
        // Add action listener
        button.addActionListener(e -> {
            String response = text;
            responses.add(response);
            System.out.println("Question " + (currentQuestionIndex + 1) + ": " + response);
            
            // Move to next question or complete quiz
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                updateQuestion();
            } else {
                completeQuiz();
            }
        });
        
        return button;
    }
    
    /**
     * Update the question display
     */
    private void updateQuestion() {
        if (currentQuestionIndex < questions.size()) {
            String questionText = questions.get(currentQuestionIndex);
            questionLabel.setText("<html><div style='text-align: center; width: 500px;'>" + 
                                 questionText + "</div></html>");
            
            // Update progress bar instead of progress label
            progressBar.setProgress(currentQuestionIndex, questions.size());
            
            // Debug output
            System.out.println("Displaying question " + (currentQuestionIndex + 1) + ": " + questionText);
        }
    }
    
    /**
     * Complete the quiz and notify listener
     */
    private void completeQuiz() {
        System.out.println("Quiz completed!");
        // Print all responses
        for (int i = 0; i < questions.size(); i++) {
            System.out.println("Q" + (i + 1) + ": " + questions.get(i));
            System.out.println("A" + (i + 1) + ": " + responses.get(i));
        }
        
        // Notify listener if set
        if (onQuizCompleted != null) {
            onQuizCompleted.run();
        }
    }
    
    /**
     * Set up fade-in animation
     */
    private void setupFadeInAnimation() {
        fadeInTimer = new Timer(30, e -> {
            opacity += 0.05f;
            if (opacity >= 1.0f) {
                opacity = 1.0f;
                fadeInTimer.stop();
                System.out.println("Question fade-in animation complete");
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
        System.out.println("Starting question fade-in animation");
    }
    
    /**
     * Set callback for when quiz is completed
     */
    public void setOnQuizCompleted(Runnable callback) {
        this.onQuizCompleted = callback;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Apply opacity to the whole panel
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        
        // Draw a subtle background for debugging
        g2d.setColor(new Color(230, 240, 255, 20)); // Very light blue, mostly transparent
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    
    /**
     * Get the list of responses
     */
    public List<String> getResponses() {
        return responses;
    }
    
    /**
     * Get the list of questions
     */
    public List<String> getQuestions() {
        return questions;
    }
    
    /**
     * Reset questions to start the quiz over
     */
    public void resetQuestions() {
        currentQuestionIndex = 0;
        responses.clear();
        updateQuestion();
    }
} 