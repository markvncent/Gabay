package frontend.quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * QuizArea component for the Candidate Quiz
 * This component serves as a container for other quiz components
 */
public class QuizArea extends JPanel {
    // Font variables
    private Font interRegular;
    private Font interMedium;
    private Font interBlack;
    private Font interSemiBold;
    
    // Colors
    private Color primaryRed = new Color(0xE9, 0x45, 0x40); // #E94540
    private Color headingColor = new Color(0x47, 0x55, 0x69); // #475569
    
    // Component dimensions
    private int panelWidth = 900;
    private int panelHeight = 600;
    
    // Quiz components
    private QuizStart quizStart;
    private Question question;
    private Results results;
    
    // Layout
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // Panel state
    private static final String START_PANEL = "start";
    private static final String QUESTION_PANEL = "question";
    private static final String RESULTS_PANEL = "results";
    
    // Transition animation
    private Timer fadeOutTimer;
    private float opacity = 1.0f;
    private String currentPanel = START_PANEL;
    private String nextPanel = "";
    private boolean isTransitioning = false;
    
    public QuizArea() {
        // Load fonts
        loadFonts();
        
        // Set up panel properties
        setOpaque(false); // Make panel transparent
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        
        // Create card layout for transitions
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        
        // Create components
        createComponents();
        
        // Add components to card panel
        cardPanel.add(quizStart, START_PANEL);
        cardPanel.add(question, QUESTION_PANEL);
        cardPanel.add(results, RESULTS_PANEL);
        
        // Show start panel by default
        cardLayout.show(cardPanel, START_PANEL);
        currentPanel = START_PANEL;
        
        // Add card panel to this panel
        add(cardPanel, BorderLayout.CENTER);
        
        // Set up transition animation
        setupTransitionAnimation();
        
        // Debug output
        System.out.println("QuizArea initialized");
    }
    
    /**
     * Set up transition animation
     */
    private void setupTransitionAnimation() {
        fadeOutTimer = new Timer(30, e -> {
            opacity -= 0.05f;
            if (opacity <= 0.0f) {
                opacity = 0.0f;
                fadeOutTimer.stop();
                
                // Switch to the next panel
                completeTransition();
            }
            repaint();
        });
    }
    
    /**
     * Start a transition to another panel
     * @param targetPanel The panel to transition to
     */
    private void startTransition(String targetPanel) {
        if (isTransitioning) return;
        
        isTransitioning = true;
        nextPanel = targetPanel;
        opacity = 1.0f;
        fadeOutTimer.start();
        
        System.out.println("Starting transition from " + currentPanel + " to " + nextPanel);
    }
    
    /**
     * Complete the transition to the next panel
     */
    private void completeTransition() {
        // Show the next panel
        cardLayout.show(cardPanel, nextPanel);
        currentPanel = nextPanel;
        
        // Start fade-in for the appropriate component
        if (QUESTION_PANEL.equals(currentPanel)) {
            question.startFadeIn();
        } else if (RESULTS_PANEL.equals(currentPanel)) {
            results.startFadeIn();
        } else if (START_PANEL.equals(currentPanel)) {
            // QuizStart doesn't have a startFadeIn method, but it's already visible
            // We'll handle the fade-in through the cardPanel's opacity
        }
        
        // Reset opacity and transition state
        opacity = 1.0f;
        isTransitioning = false;
        
        // Force revalidate and repaint
        cardPanel.revalidate();
        cardPanel.repaint();
        this.revalidate();
        this.repaint();
        
        System.out.println("Transition to " + currentPanel + " complete");
    }
    
    /**
     * Create and initialize components
     */
    private void createComponents() {
        // Create QuizStart component
        quizStart = new QuizStart();
        
        // Pass fonts to QuizStart
        quizStart.setFonts(interRegular, interMedium, interBlack, interSemiBold);
        
        // Set callback for when start button is clicked
        quizStart.setOnStartClicked(() -> {
            System.out.println("Start button clicked, transitioning to question panel");
            
            // Start transition to question panel
            startTransition(QUESTION_PANEL);
        });
        
        // Create Question component
        question = new Question();
        
        // Set callback for when quiz is completed
        question.setOnQuizCompleted(() -> {
            // Show results panel with the quiz data
            System.out.println("Quiz completed! Showing results...");
            
            // Pass quiz data to results panel
            results.setQuizData(question.getQuestions(), question.getResponses());
            
            // Start transition to results panel
            startTransition(RESULTS_PANEL);
        });
        
        // Create Results component
        results = new Results();
        
        // Set callback for when try again button is clicked
        results.setOnTryAgain(() -> {
            System.out.println("Try Again button clicked, resetting quiz...");
            
            // Reset quiz
            resetQuiz();
            
            // Start transition to start panel
            startTransition(START_PANEL);
        });
    }
    
    /**
     * Reset the quiz to start over
     */
    public void resetQuiz() {
        // Reset question component
        question.resetQuestions();
        
        // Create a new QuizStart component
        QuizStart newQuizStart = new QuizStart();
        newQuizStart.setFonts(interRegular, interMedium, interBlack, interSemiBold);
        
        // Set callback for when start button is clicked (same as in createComponents)
        newQuizStart.setOnStartClicked(() -> {
            System.out.println("Start button clicked, transitioning to question panel");
            
            // Start transition to question panel
            startTransition(QUESTION_PANEL);
        });
        
        // Replace the old QuizStart with the new one in the card layout
        cardPanel.removeAll();
        cardPanel.add(newQuizStart, START_PANEL);
        cardPanel.add(question, QUESTION_PANEL);
        cardPanel.add(results, RESULTS_PANEL);
        
        // Update our reference to the new component
        this.quizStart = newQuizStart;
        
        System.out.println("Quiz reset complete");
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Apply fade effect during transitions
        if (isTransitioning) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        }
        
        // Only render a very subtle background for debugging purposes
        // This visualization helps ensure the component is positioned correctly
        if (isOpaque()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(240, 240, 240, 30)); // Very light gray, mostly transparent
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Draw a subtle border
            g2d.setColor(new Color(200, 200, 200, 30));
            g2d.drawRect(0, 0, getWidth()-1, getHeight()-1);
        }
    }
    
    @Override
    public void paint(Graphics g) {
        if (!isTransitioning) {
            super.paint(g);
            return;
        }
        
        // For transitions, use a custom painting approach
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Set up the fade effect
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        
        // Paint the component with the fade effect applied
        super.paint(g2d);
        
        // Clean up
        g2d.dispose();
    }
    
    private void loadFonts() {
        try {
            // Load Inter fonts
            File interMediumFile = new File("lib/fonts/Inter_18pt-Medium.ttf");
            File interBlackFile = new File("lib/fonts/Inter_18pt-Black.ttf");
            File interRegularFile = new File("lib/fonts/Inter_18pt-Regular.ttf");
            File interSemiBoldFile = new File("lib/fonts/Inter_18pt-SemiBold.ttf");
            
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            
            if (interMediumFile.exists()) {
                interMedium = Font.createFont(Font.TRUETYPE_FONT, interMediumFile);
                ge.registerFont(interMedium);
            } else {
                interMedium = new Font("Sans-Serif", Font.PLAIN, 12);
            }
            
            if (interBlackFile.exists()) {
                interBlack = Font.createFont(Font.TRUETYPE_FONT, interBlackFile);
                ge.registerFont(interBlack);
            } else {
                interBlack = new Font("Sans-Serif", Font.BOLD, 12);
            }
            
            if (interRegularFile.exists()) {
                interRegular = Font.createFont(Font.TRUETYPE_FONT, interRegularFile);
                ge.registerFont(interRegular);
            } else {
                interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
            }
            
            if (interSemiBoldFile.exists()) {
                interSemiBold = Font.createFont(Font.TRUETYPE_FONT, interSemiBoldFile);
                ge.registerFont(interSemiBold);
            } else {
                interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
            }
            
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            // Fallback to system fonts
            interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
            interMedium = new Font("Sans-Serif", Font.PLAIN, 12);
            interBlack = new Font("Sans-Serif", Font.BOLD, 12);
            interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
        }
    }
} 