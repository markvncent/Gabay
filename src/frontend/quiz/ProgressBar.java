package frontend.quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Custom progress bar for quiz questions
 * Shows visual progress through questions with a head icon
 */
public class ProgressBar extends JPanel {
    private int currentQuestion = 0;
    private int totalQuestions = 0;
    
    // Animation properties
    private float currentProgress = 0.0f;
    private float targetProgress = 0.0f;
    private Timer animationTimer;
    private final int ANIMATION_DURATION = 300; // milliseconds
    private final int ANIMATION_STEPS = 20;
    
    // Colors
    private Color primaryRed = new Color(0xE9, 0x45, 0x40); // #E94540
    private Color lightGray = new Color(0xF1, 0xF5, 0xF9); // #F1F5F9
    
    // Dimensions
    private int barHeight = 6;
    private int headSize = 24; // Increased from 16 to 24
    private int barWidth = 400;
    private int verticalOffset = 10; // Added vertical offset to shift down
    
    // Progress head image
    private BufferedImage progressHeadImage;
    
    // Font
    private Font labelFont = new Font("Inter", Font.PLAIN, 14);
    private Color labelColor = new Color(0x64, 0x74, 0x8B); // #64748B
    
    /**
     * Create a new progress bar
     */
    public ProgressBar() {
        setOpaque(false);
        setPreferredSize(new Dimension(barWidth, 50)); // Increased height to accommodate the shift
        loadProgressHeadImage();
        setupAnimationTimer();
    }
    
    /**
     * Set up the animation timer
     */
    private void setupAnimationTimer() {
        animationTimer = new Timer(ANIMATION_DURATION / ANIMATION_STEPS, e -> {
            // Calculate the next step in the animation
            float step = (targetProgress - currentProgress) / 5.0f;
            
            // If we're very close to the target, just set it directly
            if (Math.abs(targetProgress - currentProgress) < 0.01f) {
                currentProgress = targetProgress;
                animationTimer.stop();
            } else {
                currentProgress += step;
            }
            
            // Repaint to show the new progress
            repaint();
        });
        animationTimer.setRepeats(true);
    }
    
    /**
     * Load the progress head image
     */
    private void loadProgressHeadImage() {
        try {
            // Try to load directly from resources/images folder
            File imageFile = new File("resources/images/progress_hea.png");
            if (imageFile.exists()) {
                progressHeadImage = ImageIO.read(imageFile);
                return;
            }
            
            // If not found, try alternative paths
            String[] alternativePaths = {
                "resources/images/quiz/progress_hea.png",
                "lib/images/progress_hea.png",
                "../resources/images/progress_hea.png"
            };
            
            for (String path : alternativePaths) {
                File altFile = new File(path);
                if (altFile.exists()) {
                    progressHeadImage = ImageIO.read(altFile);
                    return;
                }
            }
            
            // If still null, create a default image
            createDefaultHeadImage();
        } catch (IOException e) {
            System.err.println("Error loading progress head image: " + e.getMessage());
            createDefaultHeadImage();
        }
    }
    
    /**
     * Create a default progress head image
     */
    private void createDefaultHeadImage() {
        progressHeadImage = new BufferedImage(headSize, headSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = progressHeadImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(primaryRed);
        g2d.fillOval(0, 0, headSize, headSize);
        g2d.dispose();
    }
    
    /**
     * Set the current progress
     * @param current Current question index (0-based)
     * @param total Total number of questions
     */
    public void setProgress(int current, int total) {
        this.currentQuestion = current;
        this.totalQuestions = total;
        
        // Calculate the target progress for animation
        this.targetProgress = totalQuestions > 0 ? (float)(current) / total : 0;
        
        // Start the animation timer if it's not already running
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        int width = getWidth();
        int height = getHeight();
        
        // Calculate bar position with vertical offset
        int barY = (height - barHeight) / 2 + verticalOffset;
        int barX = (width - barWidth) / 2;
        
        // Draw background bar (light gray)
        g2d.setColor(lightGray);
        g2d.fill(new RoundRectangle2D.Float(barX, barY, barWidth, barHeight, barHeight, barHeight));
        
        // Calculate progress width using the animated currentProgress
        int progressWidth = (int)(barWidth * currentProgress);
        
        // Draw progress bar (red)
        g2d.setColor(primaryRed);
        g2d.fill(new RoundRectangle2D.Float(barX, barY, progressWidth, barHeight, barHeight, barHeight));
        
        // Draw progress head at the end of the progress bar
        if (progressHeadImage != null) {
            int headX = barX + progressWidth - (headSize / 2);
            int headY = barY + (barHeight / 2) - (headSize / 2);
            g2d.drawImage(progressHeadImage, headX, headY, headSize, headSize, null);
        }
        
        // Draw text label with vertical offset
        if (totalQuestions > 0) {
            String progressText = "Question " + (currentQuestion + 1) + " of " + totalQuestions;
            g2d.setFont(labelFont);
            g2d.setColor(labelColor);
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(progressText);
            int textHeight = fm.getHeight();
            int textX = (width - textWidth) / 2;
            int textY = barY - 10; // Position above the bar, but with the vertical offset applied
            
            g2d.drawString(progressText, textX, textY);
        }
    }
} 