package frontend.quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import frontend.comparison.MinimalScrollBarUI;

/**
 * QuizStart component for the Candidate Quiz
 * This component groups together the welcome message, title, and start button
 */
public class QuizStart extends JPanel {
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
    
    // Button properties
    private JButton startButton;
    private int buttonX = -1; // Center horizontally (-1 means center)
    private int buttonY = 350; // Position further below the title
    private boolean isButtonHovered = false;
    private float hoverScale = 1.1f; // Scale factor when hovered (10% larger)
    
    // Animation properties
    private Timer animationTimer;
    private float currentScale = 1.0f;
    private boolean animatingIn = false;
    private boolean animatingOut = false;
    private int animationDuration = 150; // milliseconds
    private int animationDelay = 15; // milliseconds between animation frames
    private long animationStartTime;
    
    // Fade animation properties
    private Timer fadeOutTimer;
    private float opacity = 1.0f;
    
    // Text content for easy customization
    private String welcomeText = "Welcome to";
    private String titleText = "Gabáy Quiz Match!";
    private String buttonText = "Start Now";
    
    // Font sizes for easy customization
    private float welcomeFontSize = 19f;
    private float titleFontSize = 100f;
    private float buttonFontSize = 18f;
    
    // Text position for easy customization (Y position from top)
    private int welcomeY = 200;
    private int titleY = 300;
    
    // Callback for when start button is clicked
    private Runnable onStartClicked;
    
    public QuizStart() {
        // Load fonts
        loadFonts();
        
        // Set up panel properties
        setOpaque(false); // Make panel transparent
        setLayout(null); // Use absolute positioning
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        
        // Create start button
        createStartButton();
        
        // Set up animation timer
        setupAnimationTimer();
        
        // Set up fade out animation
        setupFadeOutAnimation();
    }
    
    /**
     * Set the fonts for this component
     * @param regular Regular font
     * @param medium Medium font
     * @param black Black font
     * @param semiBold SemiBold font
     */
    public void setFonts(Font regular, Font medium, Font black, Font semiBold) {
        this.interRegular = regular;
        this.interMedium = medium;
        this.interBlack = black;
        this.interSemiBold = semiBold;
        
        // Update button font if it exists
        if (startButton != null && interSemiBold != null) {
            startButton.setFont(interSemiBold.deriveFont(buttonFontSize));
        }
        
        repaint();
    }
    
    /**
     * Set up the animation timer for hover effects
     */
    private void setupAnimationTimer() {
        animationTimer = new Timer(animationDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAnimation();
                repaint();
            }
        });
    }
    
    /**
     * Set up fade out animation
     */
    private void setupFadeOutAnimation() {
        fadeOutTimer = new Timer(30, e -> {
            opacity -= 0.05f;
            if (opacity <= 0.0f) {
                opacity = 0.0f;
                fadeOutTimer.stop();
                
                // Notify listener that fade out is complete
                if (onStartClicked != null) {
                    onStartClicked.run();
                }
            }
            repaint();
        });
    }
    
    /**
     * Start the fade out animation
     */
    public void startFadeOut() {
        opacity = 1.0f;
        fadeOutTimer.start();
    }
    
    /**
     * Update animation state
     */
    private void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        float elapsedTime = currentTime - animationStartTime;
        float progress = Math.min(1.0f, elapsedTime / animationDuration);
        
        if (animatingIn) {
            // Scale up animation
            currentScale = 1.0f + (hoverScale - 1.0f) * progress;
            
            if (progress >= 1.0f) {
                currentScale = hoverScale;
                animatingIn = false;
                animationTimer.stop();
            }
        } else if (animatingOut) {
            // Scale down animation
            currentScale = hoverScale - (hoverScale - 1.0f) * progress;
            
            if (progress >= 1.0f) {
                currentScale = 1.0f;
                animatingOut = false;
                animationTimer.stop();
            }
        }
    }
    
    /**
     * Start the hover animation
     * @param hovering true if hovering, false if not
     */
    private void startHoverAnimation(boolean hovering) {
        animationStartTime = System.currentTimeMillis();
        
        if (hovering) {
            // Start animating in (scale up)
            animatingIn = true;
            animatingOut = false;
        } else {
            // Start animating out (scale down)
            animatingIn = false;
            animatingOut = true;
        }
        
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }
    
    /**
     * Set the position of the start button
     * @param x X position (center offset), -1 for center
     * @param y Y position from top
     */
    public void setButtonPosition(int x, int y) {
        this.buttonX = x;
        this.buttonY = y;
        doLayout(); // Update layout
    }
    
    /**
     * Set the text for the welcome message
     * @param text The welcome text
     */
    public void setWelcomeText(String text) {
        this.welcomeText = text;
        repaint();
    }
    
    /**
     * Set the text for the main title
     * @param text The title text
     */
    public void setTitleText(String text) {
        this.titleText = text;
        repaint();
    }
    
    /**
     * Set the text for the button
     * @param text The button text
     */
    public void setButtonText(String text) {
        this.buttonText = text;
        if (startButton != null) {
            startButton.setText(text);
        }
        repaint();
    }
    
    /**
     * Set the font sizes
     * @param welcomeSize Welcome text size
     * @param titleSize Title text size
     * @param buttonSize Button text size
     */
    public void setFontSizes(float welcomeSize, float titleSize, float buttonSize) {
        this.welcomeFontSize = welcomeSize;
        this.titleFontSize = titleSize;
        this.buttonFontSize = buttonSize;
        
        if (startButton != null) {
            startButton.setFont(interSemiBold.deriveFont(buttonFontSize));
        }
        
        repaint();
    }
    
    /**
     * Set the positions of the text elements
     * @param welcomeYPos Welcome text Y position from top
     * @param titleYPos Title text Y position from top
     */
    public void setTextPositions(int welcomeYPos, int titleYPos) {
        this.welcomeY = welcomeYPos;
        this.titleY = titleYPos;
        repaint();
    }
    
    /**
     * Set the hover scale effect for the button
     * @param scale Scale factor (1.0 = no scaling, 1.1 = 10% larger)
     */
    public void setButtonHoverScale(float scale) {
        this.hoverScale = scale;
        repaint();
    }
    
    /**
     * Set the animation duration for the hover effect
     * @param durationMs Duration in milliseconds
     */
    public void setAnimationDuration(int durationMs) {
        this.animationDuration = durationMs;
    }
    
    /**
     * Set callback for when start button is clicked
     */
    public void setOnStartClicked(Runnable callback) {
        this.onStartClicked = callback;
    }
    
    private void createStartButton() {
        startButton = new JButton(buttonText);
        startButton.setFont(interSemiBold.deriveFont(buttonFontSize));
        startButton.setForeground(primaryRed); // Default color same as outline (non-hover state)
        startButton.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Make button look nicer with custom painting
        startButton.setContentAreaFilled(false);
        
        // Add hover effect
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isButtonHovered = true;
                startButton.setForeground(Color.WHITE); // White text on hover
                startHoverAnimation(true); // Start scale up animation
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isButtonHovered = false;
                startButton.setForeground(primaryRed); // Back to original color off hover
                startHoverAnimation(false); // Start scale down animation
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                repaint();
            }
        });
        
        // Add action listener
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start fade out animation
                startFadeOut();
            }
        });
        
        // Add to panel
        add(startButton);
    }
    
    @Override
    public void doLayout() {
        super.doLayout();
        
        // Position the button based on the set coordinates
        if (startButton != null) {
            int buttonWidth = 200; // Slightly wider button
            int buttonHeight = 50;
            
            int finalX = buttonX;
            if (buttonX == -1) {
                // Center horizontally
                finalX = (getWidth() - buttonWidth) / 2;
            }
            
            startButton.setBounds(
                finalX,
                buttonY,
                buttonWidth,
                buttonHeight
            );
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Apply opacity for fade out effect
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        
        int width = getWidth();
        
        // Draw welcome text
        if (interMedium != null) {
            Map<TextAttribute, Object> attributes = new HashMap<>();
            attributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
            Font welcomeFont = interMedium.deriveFont(welcomeFontSize).deriveFont(attributes);
            
            g2d.setFont(welcomeFont);
            g2d.setColor(headingColor);
            
            FontMetrics welcomeMetrics = g2d.getFontMetrics(welcomeFont);
            int welcomeWidth = welcomeMetrics.stringWidth(welcomeText);
            int welcomeX = (width - welcomeWidth) / 2;
            
            g2d.drawString(welcomeText, welcomeX, welcomeY);
        }
        
        // Draw title text
        if (interBlack != null) {
            Map<TextAttribute, Object> attributes = new HashMap<>();
            attributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
            Font titleFont = interBlack.deriveFont(titleFontSize).deriveFont(attributes);
            
            g2d.setFont(titleFont);
            g2d.setColor(primaryRed);
            
            FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);
            int titleWidth = titleMetrics.stringWidth(titleText);
            int titleX = (width - titleWidth) / 2;
            
            g2d.drawString(titleText, titleX, titleY);
        }
        
        // Draw the button if it exists
        if (startButton != null && startButton.isVisible()) {
            Rectangle bounds = startButton.getBounds();
            boolean isPressed = startButton.getModel().isPressed();
            
            // Set stroke for outline
            g2d.setStroke(new BasicStroke(2.0f));
            
            // Calculate scaled dimensions with animation
            int buttonWidth = bounds.width;
            int buttonHeight = bounds.height;
            int buttonX = bounds.x;
            int buttonY = bounds.y;
            
            if ((isButtonHovered || animatingIn || animatingOut) && !isPressed) {
                // Apply scaling based on current animation state
                int originalWidth = buttonWidth;
                int originalHeight = buttonHeight;
                
                // Calculate new dimensions
                buttonWidth = (int)(buttonWidth * currentScale);
                buttonHeight = (int)(buttonHeight * currentScale);
                
                // Adjust position to keep button centered
                buttonX = bounds.x - (buttonWidth - originalWidth) / 2;
                buttonY = bounds.y - (buttonHeight - originalHeight) / 2;
            }
            
            // Draw the button with different appearance based on state
            if (isPressed) {
                // Pressed state: darker fill
                g2d.setColor(primaryRed.darker());
                g2d.fill(new RoundRectangle2D.Double(
                    bounds.x, bounds.y, bounds.width, bounds.height, 10, 10));
            } else if (isButtonHovered || animatingIn || animatingOut) {
                // Hover or animating state: filled with color and scaled
                g2d.setColor(primaryRed);
                g2d.fill(new RoundRectangle2D.Double(
                    buttonX, buttonY, buttonWidth, buttonHeight, 10, 10));
            } else {
                // Normal state: just outline, transparent fill
                g2d.setColor(primaryRed);
                g2d.draw(new RoundRectangle2D.Double(
                    bounds.x, bounds.y, bounds.width, bounds.height, 10, 10));
            }
            
            // Draw the text on the button - custom rendering to apply scaling
            if (interSemiBold != null) {
                // Use custom text rendering to match animation
                String text = startButton.getText();
                
                // Determine text color based on state
                if (isButtonHovered || animatingIn || animatingOut) {
                    g2d.setColor(Color.WHITE);
                } else {
                    g2d.setColor(primaryRed);
                }
                
                // Calculate font size with animation
                float scaledFontSize = buttonFontSize;
                if ((isButtonHovered || animatingIn || animatingOut) && !isPressed) {
                    scaledFontSize = buttonFontSize * currentScale;
                }
                
                // Create the font with correct size
                Font buttonFont = interSemiBold.deriveFont(scaledFontSize);
                g2d.setFont(buttonFont);
                
                // Calculate text position
                FontMetrics metrics = g2d.getFontMetrics(buttonFont);
                int textWidth = metrics.stringWidth(text);
                int textHeight = metrics.getHeight();
                
                // Center the text in the button
                int textX = buttonX + (buttonWidth - textWidth) / 2;
                int textY = buttonY + (buttonHeight - textHeight) / 2 + metrics.getAscent();
                
                // Draw the text
                g2d.drawString(text, textX, textY);
            }
            
            // Hide the button's text since we're drawing it manually
            // We need to keep the actual JButton for click handling
            startButton.setForeground(new Color(0, 0, 0, 0)); // Transparent
        }
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
    
    /**
     * Get the start button for attaching additional event handlers
     * @return The start button instance
     */
    public JButton getStartButton() {
        return startButton;
    }
} 