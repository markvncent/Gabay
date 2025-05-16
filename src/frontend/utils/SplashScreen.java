package frontend.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * A splash screen that displays when the application first launches.
 * Shows the Gabay emblem and a progress bar for 3 seconds before
 * transitioning to the landing page.
 */
public class SplashScreen extends JFrame {
    // Duration of splash screen in milliseconds
    private static final int SPLASH_DURATION = 3000;
    
    // Progress bar properties
    private int progressValue = 0;
    private final int MAX_PROGRESS = 100;
    private final int PROGRESS_BAR_HEIGHT = 6;
    private final int PROGRESS_BAR_BOTTOM_MARGIN = 40;
    private final int PROGRESS_HEAD_SIZE = 24;
    
    // Colors
    private Color primaryRed = new Color(0xE9, 0x45, 0x40); // #E94540 - Red color from ProgressBar
    private Color lightGray = new Color(0xF1, 0xF5, 0xF9); // #F1F5F9 - light gray
    private Color labelColor = new Color(0x64, 0x74, 0x8B); // #64748B - slate gray
    
    // Animation timer
    private Timer progressTimer;
    private Timer fadeOutTimer;
    
    // Images
    private BufferedImage emblemImage;
    private BufferedImage progressHeadImage;
    
    // Flag to track if this is the first launch
    private static boolean firstLaunch = true;
    
    /**
     * Create a new splash screen
     */
    public SplashScreen() {
        // Set up the window
        setUndecorated(true); // No window decorations
        setBackground(new Color(255, 255, 255, 255)); // White background
        setSize(600, 400); // Fixed size for splash screen
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Load images
        loadEmblemImage();
        loadProgressHeadImage();
        
        // Create content panel
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Set rendering hints for better quality
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                // Draw white background
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw emblem image centered
                if (emblemImage != null) {
                    int emblemeWidth = Math.min(emblemImage.getWidth(), getWidth() / 2);
                    int emblemeHeight = (int)((float)emblemeWidth / emblemImage.getWidth() * emblemImage.getHeight());
                    int x = (getWidth() - emblemeWidth) / 2;
                    int y = (getHeight() - emblemeHeight) / 2 - 20; // Slightly above center
                    
                    g2d.drawImage(emblemImage, x, y, emblemeWidth, emblemeHeight, null);
                }
                
                // Draw progress bar
                int barWidth = getWidth() / 2;
                int barX = (getWidth() - barWidth) / 2;
                int barY = getHeight() - PROGRESS_BAR_BOTTOM_MARGIN;
                
                // Draw background bar (light gray)
                g2d.setColor(lightGray);
                g2d.fill(new RoundRectangle2D.Float(barX, barY, barWidth, PROGRESS_BAR_HEIGHT, 
                                                   PROGRESS_BAR_HEIGHT, PROGRESS_BAR_HEIGHT));
                
                // Draw progress bar (blue)
                int progressWidth = (int)(barWidth * (progressValue / (float)MAX_PROGRESS));
                g2d.setColor(primaryRed);
                g2d.fill(new RoundRectangle2D.Float(barX, barY, progressWidth, PROGRESS_BAR_HEIGHT, 
                                                   PROGRESS_BAR_HEIGHT, PROGRESS_BAR_HEIGHT));
                
                // Draw progress head at the end of the progress bar
                if (progressHeadImage != null) {
                    int headX = barX + progressWidth - (PROGRESS_HEAD_SIZE / 2);
                    int headY = barY + (PROGRESS_BAR_HEIGHT / 2) - (PROGRESS_HEAD_SIZE / 2);
                    g2d.drawImage(progressHeadImage, headX, headY, PROGRESS_HEAD_SIZE, PROGRESS_HEAD_SIZE, null);
                }
                
                // Draw "Loading..." text
                g2d.setColor(labelColor);
                Font font = new Font("Inter", Font.PLAIN, 14);
                g2d.setFont(font);
                String loadingText = "Loading...";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(loadingText);
                g2d.drawString(loadingText, (getWidth() - textWidth) / 2, barY - 10);
            }
        };
        
        // Add content panel to frame
        setContentPane(contentPanel);
        
        // Set up progress timer
        int updateInterval = SPLASH_DURATION / MAX_PROGRESS;
        progressTimer = new Timer(updateInterval, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressValue++;
                
                if (progressValue >= MAX_PROGRESS) {
                    progressTimer.stop();
                    fadeOutAndShowMainWindow();
                }
                
                repaint();
            }
        });
    }
    
    /**
     * Load the emblem image
     */
    private void loadEmblemImage() {
        try {
            File emblemFile = new File("resources/images/HeaderEmblem.png");
            if (emblemFile.exists()) {
                emblemImage = ImageIO.read(emblemFile);
                System.out.println("Loaded Gabay Header Emblem for splash screen");
            } else {
                System.err.println("Gabay Header Emblem file not found at: " + emblemFile.getAbsolutePath());
                
                // Try alternative paths
                String[] alternativePaths = {
                    "resources/images/GabayEmblem.png",
                    "resources/images/logo.png",
                    "../resources/images/HeaderEmblem.png"
                };
                
                for (String path : alternativePaths) {
                    File altFile = new File(path);
                    if (altFile.exists()) {
                        emblemImage = ImageIO.read(altFile);
                        System.out.println("Loaded alternative emblem from: " + path);
                        return;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading Gabay Header Emblem: " + e.getMessage());
        }
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
                System.out.println("Loaded progress head image for splash screen");
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
                    System.out.println("Loaded progress head image from: " + path);
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
        progressHeadImage = new BufferedImage(PROGRESS_HEAD_SIZE, PROGRESS_HEAD_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = progressHeadImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(primaryRed);
        g2d.fillOval(0, 0, PROGRESS_HEAD_SIZE, PROGRESS_HEAD_SIZE);
        g2d.dispose();
        System.out.println("Created default progress head image");
    }
    
    /**
     * Fade out the splash screen and show the main window
     */
    private void fadeOutAndShowMainWindow() {
        // Create fade out timer
        fadeOutTimer = new Timer(50, new ActionListener() {
            private float opacity = 1.0f;
            private final float FADE_STEP = 0.1f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= FADE_STEP;
                
                if (opacity <= 0) {
                    opacity = 0;
                    fadeOutTimer.stop();
                    dispose(); // Close the splash screen
                    
                    // Launch the main application
                    SwingUtilities.invokeLater(() -> {
                        // Reset first launch flag for next time
                        firstLaunch = false;
                        
                        // Launch the landing page
                        frontend.landingpage.LandingPageUI ui = new frontend.landingpage.LandingPageUI();
                        ui.setVisible(true);
                    });
                }
                
                // Set window opacity
                setOpacity(opacity);
            }
        });
        
        fadeOutTimer.start();
    }
    
    /**
     * Show the splash screen and start the progress timer
     */
    public void showSplash() {
        setVisible(true);
        progressTimer.start();
    }
    
    /**
     * Check if this is the first launch of the application
     * @return true if this is the first launch, false otherwise
     */
    public static boolean isFirstLaunch() {
        return firstLaunch;
    }
} 