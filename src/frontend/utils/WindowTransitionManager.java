package frontend.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Utility class to handle page transitions with a white fade and fading emblem effect
 */
public class WindowTransitionManager {
    // Transition properties
    private static final int FADE_DURATION = 250; // milliseconds for fade in/out
    private static final int EMBLEM_DURATION = 300; // milliseconds for emblem fade in/out
    private static final int FADE_STEPS = 10;
    private static final float FADE_STEP = 1.0f / FADE_STEPS;
    
    // Emblem image
    private static BufferedImage emblemImage = null;
    
    // Load the emblem image once
    static {
        try {
            File emblemFile = new File("resources/images/GabayEmblem.png");
            if (emblemFile.exists()) {
                emblemImage = ImageIO.read(emblemFile);
                System.out.println("Loaded Gabay Emblem successfully for transitions: " + 
                                  emblemImage.getWidth() + "x" + emblemImage.getHeight());
            } else {
                System.err.println("Gabay Emblem file not found at: " + emblemFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error loading Gabay Emblem: " + e.getMessage());
        }
    }
    
    /**
     * Transition from the current window to a new window with a white fade and fading emblem effect
     * 
     * @param currentWindow The current window to fade out
     * @param newWindowSupplier A supplier that creates the new window
     */
    public static void fadeTransition(JFrame currentWindow, Supplier<JFrame> newWindowSupplier) {
        // Save current window size and position
        final Dimension currentSize = currentWindow.getSize();
        final Point currentLocation = currentWindow.getLocation();
        
        // Create a glass pane for the fade effect
        class TransitionGlassPane extends JPanel {
            private float opacity = 0.0f;
            private boolean showEmblem = false;
            private float emblemOpacity = 0.0f;
            
            public TransitionGlassPane() {
                setOpaque(false);
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Set rendering hints for better quality
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                // Draw white background with current opacity
                g2d.setColor(Color.WHITE);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw the emblem if needed
                if (showEmblem && emblemImage != null) {
                    // Calculate emblem position (centered) - size is 1/4 of screen width
                    int emblemeWidth = Math.min(emblemImage.getWidth(), getWidth() / 4);
                    int emblemeHeight = (int)((float)emblemeWidth / emblemImage.getWidth() * emblemImage.getHeight());
                    int x = (getWidth() - emblemeWidth) / 2;
                    int y = (getHeight() - emblemeHeight) / 2;
                    
                    // Draw with current emblem opacity
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, emblemOpacity));
                    g2d.drawImage(emblemImage, x, y, emblemeWidth, emblemeHeight, null);
                }
            }
            
            public void setOpacity(float value) {
                this.opacity = value;
                repaint();
            }
            
            public float getOpacity() {
                return opacity;
            }
            
            public void setShowEmblem(boolean show) {
                this.showEmblem = show;
                repaint();
            }
            
            public void setEmblemOpacity(float value) {
                this.emblemOpacity = value;
                repaint();
            }
            
            public float getEmblemOpacity() {
                return emblemOpacity;
            }
        }
        
        // Create and add the glass pane to the current window
        TransitionGlassPane glassPane = new TransitionGlassPane();
        currentWindow.setGlassPane(glassPane);
        glassPane.setVisible(true);
        
        // Create fade out timer
        Timer fadeOutTimer = new Timer(FADE_DURATION / FADE_STEPS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float currentOpacity = glassPane.getOpacity();
                currentOpacity += FADE_STEP;
                
                if (currentOpacity >= 1.0f) {
                    currentOpacity = 1.0f;
                    ((Timer) e.getSource()).stop();
                    
                    // When fade out is complete, show the emblem and start the fading
                    glassPane.setShowEmblem(true);
                    
                    // Start the emblem fade in timer
                    int fadeSteps = 10; // Number of steps for emblem fade
                    int halfDuration = EMBLEM_DURATION / 2;
                    Timer emblemFadeTimer = new Timer(halfDuration / fadeSteps, new ActionListener() {
                        private int elapsedTime = 0;
                        private boolean fadingIn = true;
                        
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            elapsedTime += halfDuration / fadeSteps;
                            
                            if (fadingIn) {
                                // Fade in stage
                                float newOpacity = elapsedTime / (float)halfDuration;
                                if (newOpacity >= 1.0f) {
                                    newOpacity = 1.0f;
                                    fadingIn = false;
                                    elapsedTime = 0; // Reset for fade out
                                }
                                glassPane.setEmblemOpacity(newOpacity);
                            } else {
                                // Fade out stage
                                float newOpacity = 1.0f - (elapsedTime / (float)halfDuration);
                                if (newOpacity <= 0.0f) {
                                    newOpacity = 0.0f;
                                    ((Timer) e.getSource()).stop();
                                    
                                    // When emblem fading is complete, create and show the new window
                                    SwingUtilities.invokeLater(() -> {
                                        // Create the new window
                                        JFrame newWindow = newWindowSupplier.get();
                                        
                                        // Create glass pane for fade in
                                        TransitionGlassPane newGlassPane = new TransitionGlassPane();
                                        newGlassPane.setOpacity(1.0f);
                                        newWindow.setGlassPane(newGlassPane);
                                        newGlassPane.setVisible(true);
                                        
                                        // Set the size and position of the new window
                                        newWindow.setSize(currentSize);
                                        newWindow.setLocation(currentLocation);
                                        
                                        // Show the new window (still covered by the fade)
                                        newWindow.setVisible(true);
                                        
                                        // Dispose the old window
                                        currentWindow.dispose();
                                        
                                        // Start fade in timer
                                        Timer fadeInTimer = new Timer(FADE_DURATION / FADE_STEPS, new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                float opacity = newGlassPane.getOpacity();
                                                opacity -= FADE_STEP;
                                                
                                                if (opacity <= 0.0f) {
                                                    opacity = 0.0f;
                                                    ((Timer) e.getSource()).stop();
                                                    
                                                    // When fade in is complete, hide glass pane
                                                    newGlassPane.setVisible(false);
                                                }
                                                
                                                newGlassPane.setOpacity(opacity);
                                            }
                                        });
                                        fadeInTimer.start();
                                    });
                                }
                                glassPane.setEmblemOpacity(newOpacity);
                            }
                        }
                    });
                    emblemFadeTimer.start();
                }
                
                glassPane.setOpacity(currentOpacity);
            }
        });
        fadeOutTimer.start();
    }
} 