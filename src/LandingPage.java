import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class LandingPage extends JFrame {
    
    // Font variables
    private Font interRegular;
    private Font interBlack;
    private Font interSemiBold;  // For paragraph text
    private Font interBold;      // For section title
    private Font interMedium;    // For section content
    
    // Button positioning variables
    private JButton button1, button2, button3, button4;
    private int buttonWidth = 529; // Base width at 1440px window width
    private int buttonHeight = 131; // Base height
    private int horizontalGap = 35; // Gap between buttons horizontally
    private int verticalGap = 30;   // Gap between buttons vertically
    private float originalButtonFontSize = 38f; // Original font size for buttons
    
    // Admin button
    private JButton adminButton;
    
    // Background image and header
    private BufferedImage backgroundImage;
    private BufferedImage headerImage;
    private BufferedImage searchIconImage;
    private BufferedImage compareIconImage;
    private BufferedImage overviewIconImage;
    private BufferedImage quizIconImage;
    private boolean showBackgroundImage = true; // Set to false to hide the image
    
    // Divider line properties
    private Color dividerColor = new Color(0xD9, 0xD9, 0xD9); // #D9D9D9
    private int dividerLength = 1093;
    private int dividerX = 173;
    private int dividerY = 703;
    private int dividerThickness = 1; // Default line thickness
    private boolean dynamicDividerPosition = true; // Flag to enable dynamic positioning
    
    // Header positioning
    private int headerY = 48; // Default Y position when window is large enough
    private int initialWindowWidth = 1440; // Initial window width for reference
    private int initialWindowHeight = 1024; // Initial window height for reference
    private int baseMinimumHeaderButtonSpace = 50; // Base minimum space between header bottom and buttons
    
    // Button panel positioning
    private JPanel buttonPanel;
    
    // About section properties
    private String aboutHeading = "About Gabáy:";
    private String aboutText = "Recognizing that many people lack the time or resources to thoroughly research all candidates, Gabáy serves as an accessible and user-friendly platform of their backgrounds, platforms, views, etc.";
    private Color headingColor = new Color(0x47, 0x55, 0x69); // #475569 - slate gray
    private Color paragraphColor = new Color(0x8D, 0x8D, 0x8D); // #8D8D8D - medium gray
    private int aboutHeadingX = dividerX; // Start at left side of divider
    private int aboutHeadingY = dividerY + 48 + 3 + 2; // 10px below divider line + text height + 3px + additional 2px lower
    private int aboutParagraphX = dividerX; // Start at same X as heading
    private int aboutParagraphY = aboutHeadingY + 48; // Below heading
    private int aboutParagraphWidth = dividerLength; // Same width as divider
    
    // Info section properties
    private String infoSectionTitle = "Search Functionality";  // Updated title
    private String infoSectionContent = "Enter a keywords to search for specific political issues. Highlighted stance of each candidate in the search results.";
    private int infoSectionHeight = 142; // 141.5 rounded up
    private int infoTitleFontSize = 28; // Reduced from 30 for better fit
    private int infoContentFontSize = 14; // Reduced from 16 for better readability
    
    // Second info section properties
    private String infoSection2Title = "Candidate Comparison";
    private String infoSection2Content = "Select two candidates to compare. Displayed side-by-side comparison of platforms, experiences, and issue stances.";
    
    // Third info section properties
    private String infoSection3Title = "Issue-Based Quiz Matching";
    private String infoSection3Content = "Quiz containing questions about users' political views or preferences. Calculate and display the candidate(s) that best align with the user's quiz answers.";
    
    // Watermark properties
    private String watermarkText = "All Rights Reserved. 2025";
    private int watermarkFontSize = 12;
    private Color watermarkColor = new Color(0xA1, 0xA1, 0xA1); // New #A1A1A1 color for the watermark
    
    public LandingPage() {
        // Load Inter fonts
        loadFonts();
        
        // Load background image and header
        loadBackgroundImage();
        loadHeaderImage();
        loadSearchIconImage();
        
        // Set up the window
        setTitle("Gabáy Landing Page");
        setSize(1440, 1024); // Window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set default font for all UI elements
        setUIFont(interRegular);
        
        // Create a custom panel with white background and the images
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Fill the background with white
                g.setColor(new Color(0xFF, 0xFF, 0xFF)); // #FFFFFF
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw background image centered with reduced opacity (5% - reduced from 8%)
                if (backgroundImage != null && showBackgroundImage) {
                    // Use Graphics2D for better quality rendering and to set opacity
                    Graphics2D g2d = (Graphics2D)g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    
                    int imageX = (getWidth() - 1802) / 2;
                    int imageY = (getHeight() - 1519) / 2;
                    
                    // Print debugging info once, not on every repaint
                    if (!paintedOnce) {
                        System.out.println("Drawing image at: " + imageX + "," + imageY + 
                                          " with size: 1802x1519. Panel size: " + 
                                          getWidth() + "x" + getHeight() + 
                                          ", Opacity: 5%");
                        paintedOnce = true;
                    }
                    
                    // Set the opacity to 5% (0.05f) - reduced from 8%
                    AlphaComposite alphaComposite = AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 0.05f);
                    g2d.setComposite(alphaComposite);
                    
                    g2d.drawImage(backgroundImage, imageX, imageY, 1802, 1519, this);
                    
                    // Reset composite for other components
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
                
                // Draw header image, centered horizontally and at adjusted Y position
                if (headerImage != null) {
                    Graphics2D g2d = (Graphics2D)g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    
                    // Calculate scaling factors based on both dimensions
                    double widthScaleFactor = Math.min(1.0, getWidth() / (double)initialWindowWidth);
                    double heightScaleFactor = Math.min(1.0, getHeight() / (double)initialWindowHeight);
                    
                    // Calculate maximum header height to avoid button overlap
                    // Use 40% of screen height as max header height on small screens
                    double maxHeaderHeightFactor = 0.4;
                    if (getHeight() < 700) {
                        // Reduce header size more aggressively on smaller screens
                        maxHeaderHeightFactor = 0.3;
                    }
                    if (getHeight() < 500) {
                        // Even smaller on very small screens
                        maxHeaderHeightFactor = 0.25;
                    }
                    
                    double maxHeightBasedSize = getHeight() * maxHeaderHeightFactor / headerImage.getHeight();
                    
                    // Use the smaller factor to maintain aspect ratio while ensuring it fits
                    double scaleFactor = Math.min(Math.min(widthScaleFactor, heightScaleFactor), maxHeightBasedSize);
                    
                    // Apply minimum scale to ensure header is never too small
                    scaleFactor = Math.max(0.3, scaleFactor);
                    
                    int scaledHeaderWidth = (int)(headerImage.getWidth() * scaleFactor);
                    int scaledHeaderHeight = (int)(headerImage.getHeight() * scaleFactor);
                    
                    // Calculate x position to center the header image
                    int headerX = (getWidth() - scaledHeaderWidth) / 2;
                    
                    // Adjust Y position if window is very small
                    // Scale the Y position proportionally to the window height
                    int adjustedHeaderY = (int)(headerY * heightScaleFactor);
                    
                    // Ensure minimum visibility at the top
                    adjustedHeaderY = Math.max(10, adjustedHeaderY);
                    
                    // For very small windows, keep header near the top but still visible
                    if (getHeight() < 600) {
                        adjustedHeaderY = Math.min(adjustedHeaderY, getHeight() / 10);
                    }
                    
                    // Log position on first paint for debugging
                    if (!headerPaintedOnce) {
                        System.out.println("Drawing header at: " + headerX + "," + adjustedHeaderY + 
                                          " with size: " + scaledHeaderWidth + "x" + scaledHeaderHeight + 
                                          " (scale: " + scaleFactor + ")");
                        headerPaintedOnce = true;
                    }
                    
                    g2d.drawImage(headerImage, headerX, adjustedHeaderY, 
                                 scaledHeaderWidth, scaledHeaderHeight, this);
                }
                
                // Draw the horizontal divider line
                if (true) { // Always show the divider (can be made conditional if needed)
                    Graphics2D g2d = (Graphics2D)g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Calculate scaling factors for responsive positioning
                    double widthScaleFactor = Math.min(1.0, getWidth() / (double)initialWindowWidth);
                    double heightScaleFactor = Math.min(1.0, getHeight() / (double)initialWindowHeight);
                    
                    // Scale position and length based on current window dimensions
                    int scaledDividerX = (int)(dividerX * widthScaleFactor);
                    int scaledDividerY = (int)(dividerY * heightScaleFactor);
                    int scaledDividerLength = (int)(dividerLength * widthScaleFactor);
                    
                    // Set line properties
                    g2d.setColor(dividerColor);
                    g2d.setStroke(new BasicStroke(dividerThickness));
                    
                    // Draw the horizontal line
                    g2d.drawLine(scaledDividerX, scaledDividerY, 
                                scaledDividerX + scaledDividerLength, scaledDividerY);
                    
                    // Draw the "About Gabáy:" heading 
                    if (interBlack != null) {
                        // Set font for the heading
                        float headingFontSize = 38f * (float)widthScaleFactor;
                        headingFontSize = Math.max(24f, headingFontSize); // Minimum size
                        Font scaledHeadingFont = interBlack.deriveFont(headingFontSize);
                        
                        // Apply letter spacing (-5%)
                        Map<TextAttribute, Object> headingAttributes = new HashMap<>();
                        headingAttributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
                        scaledHeadingFont = scaledHeadingFont.deriveFont(headingAttributes);
                        
                        g2d.setFont(scaledHeadingFont);
                        
                        // Position the heading near the left end of the divider
                        // Lower only the heading text by 2 pixels for alignment with paragraph
                        int scaledHeadingX = scaledDividerX;
                        int scaledHeadingY = scaledDividerY + (int)(48 * heightScaleFactor) + 23;
                        
                        // Set text color and draw the heading
                        g2d.setColor(headingColor);
                        g2d.drawString(aboutHeading, scaledHeadingX, scaledHeadingY);
                        
                        // Calculate metrics for paragraph placement
                        FontMetrics headingMetrics = g2d.getFontMetrics(scaledHeadingFont);
                        int headingWidth = headingMetrics.stringWidth(aboutHeading);
                        
                        // Draw paragraph text
                        if (interSemiBold != null && aboutText != null) {
                            // Set font for paragraph - making it smaller
                            float paragraphFontSize = 16f * (float)widthScaleFactor; // Reduced from 18f
                            paragraphFontSize = Math.max(10f, paragraphFontSize); // Minimum size reduced
                            Font scaledParagraphFont = interSemiBold.deriveFont(paragraphFontSize);
                            
                            // Apply letter spacing (-5%)
                            Map<TextAttribute, Object> paragraphAttributes = new HashMap<>();
                            paragraphAttributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
                            scaledParagraphFont = scaledParagraphFont.deriveFont(paragraphAttributes);
                            
                            g2d.setFont(scaledParagraphFont);
                            
                            // Get heading metrics for vertical centering
                            int headingHeight = headingMetrics.getHeight();
                            int headingAscent = headingMetrics.getAscent();
                            
                            // Get paragraph metrics
                            FontMetrics paragraphMetrics = g2d.getFontMetrics(scaledParagraphFont);
                            int paragraphHeight = paragraphMetrics.getHeight();
                            int paragraphAscent = paragraphMetrics.getAscent();
                            
                            // Position paragraph to the right of the heading with some horizontal space
                            int scaledParagraphX = scaledHeadingX + headingWidth + (int)(20 * widthScaleFactor);
                            
                            // Vertical center alignment - align the midpoint of the first line of paragraph text 
                            // with the midpoint of the heading text, but without adding the extra 2px for paragraph
                            int headingMidpoint = (scaledHeadingY - 2) - headingAscent + (headingHeight / 2);
                            int scaledParagraphY = headingMidpoint + paragraphAscent - (paragraphHeight / 2);
                            
                            // Calculate available width for paragraph (from end of heading to end of divider)
                            int scaledParagraphWidth = (scaledDividerX + scaledDividerLength) - scaledParagraphX - (int)(20 * widthScaleFactor);
                            
                            // Set paragraph color
                            g2d.setColor(paragraphColor);
                            
                            // Draw the paragraph text with wrapping and justification
                            drawJustifiedText(g2d, aboutText, scaledParagraphX, scaledParagraphY, scaledParagraphWidth);
                        }
                    }
                    
                    // Draw the information sections below the about section
                    if (interBold != null && interMedium != null) {
                        // Calculate scaled dimensions and positions
                        double scaleRatio = Math.min(widthScaleFactor, heightScaleFactor);
                        
                        // Calculate the total width available (same as divider length)
                        int totalAvailableWidth = scaledDividerLength;
                        
                        // Calculate the width for each section (accounting for gaps between them)
                        // Increase spacing between sections to 40px (from 20px)
                        int gapBetweenSections = (int)(40 * widthScaleFactor);
                        int sectionWidth = (totalAvailableWidth - (2 * gapBetweenSections)) / 3;
                        int sectionHeight = (int)(infoSectionHeight * scaleRatio);
                        
                        // Position the sections with the same space as between divider and "About Gabáy" heading
                        // The about heading is positioned at scaledDividerY + (int)(48 * heightScaleFactor) + 3 + 4
                        // So we'll use the same spacing below the about section
                        int aboutTextSpace = (int)(48 * heightScaleFactor);
                        
                        // Position the sections below the about section with appropriate spacing
                        // Add the same amount of space below the about text as there is above it
                        // Account for the additional 3+4=7px we added to the About section
                        int sectionsY = scaledDividerY + aboutTextSpace + aboutTextSpace + 7;
                        
                        // Check if there's enough space for sections below
                        int availableHeightForSections = getHeight() - sectionsY - (int)(50 * heightScaleFactor);
                        if (availableHeightForSections < (infoSectionHeight * 3) && getHeight() < 800) {
                            // For smaller screens, adjust section layout
                            // Make sections smaller and position them closer together
                            sectionHeight = (int)(infoSectionHeight * 0.7 * scaleRatio); // Reduce height
                            
                            // Reduce spacing between sections
                            gapBetweenSections = (int)(20 * widthScaleFactor);
                            
                            // Recalculate section width with reduced gaps
                            sectionWidth = (totalAvailableWidth - (2 * gapBetweenSections)) / 3;
                        }
                        
                        // Prepare fonts and attributes for all sections
                        float scaledTitleFontSize = infoTitleFontSize * (float)scaleRatio;
                        scaledTitleFontSize = Math.max(14f, scaledTitleFontSize); // Minimum size
                        
                        // Apply letter spacing (-5%) to title font
                        Map<TextAttribute, Object> titleAttributes = new HashMap<>();
                        titleAttributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
                        Font scaledTitleFont = interBold.deriveFont(scaledTitleFontSize).deriveFont(titleAttributes);
                        
                        float scaledContentFontSize = infoContentFontSize * (float)scaleRatio;
                        scaledContentFontSize = Math.max(9f, scaledContentFontSize); // Minimum size
                        
                        // Apply letter spacing (-5%) to content font
                        Map<TextAttribute, Object> contentAttributes = new HashMap<>();
                        contentAttributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
                        Font scaledContentFont = interMedium.deriveFont(scaledContentFontSize).deriveFont(contentAttributes);
                        
                        // Metrics for alignment
                        g2d.setFont(scaledTitleFont);
                        FontMetrics titleMetrics = g2d.getFontMetrics();
                        int titleHeight = titleMetrics.getHeight();
                        
                        g2d.setFont(scaledContentFont);
                        FontMetrics contentMetrics = g2d.getFontMetrics();
                        
                        // Draw all three sections
                        String[] titles = {infoSectionTitle, infoSection2Title, infoSection3Title};
                        String[] contents = {infoSectionContent, infoSection2Content, infoSection3Content};
                        
                        for (int i = 0; i < 3; i++) {
                            // Calculate X position for this section
                            int sectionX = scaledDividerX + (i * (sectionWidth + gapBetweenSections));
                            
                            // Draw section title
                            g2d.setFont(scaledTitleFont);
                            g2d.setColor(headingColor); // Same color as About Gabáy heading (#475569)
                            
                            // Center align the title within the section width
                            int titleWidth = titleMetrics.stringWidth(titles[i]);
                            int centeredTitleX = sectionX + ((sectionWidth - titleWidth) / 2);
                            int titleY = sectionsY + titleHeight; // Position for baseline
                            
                            g2d.drawString(titles[i], centeredTitleX, titleY);
                            
                            // Draw section content
                            g2d.setFont(scaledContentFont);
                            g2d.setColor(paragraphColor); // Same as about paragraph (#8D8D8D)
                            
                            // Position content 25px below title
                            int contentY = titleY + (int)(25 * heightScaleFactor);
                            
                            // Draw the content with centered text and wrapping
                            drawCenteredText(g2d, contents[i], sectionX, contentY, sectionWidth);
                        }
                        
                        // Draw watermark at the bottom of the window, with proper spacing
                        int watermarkY = getHeight() - (int)(30 * heightScaleFactor); // 30px from bottom
                        
                        // In very small windows, ensure watermark is still visible
                        if (getHeight() < 700) {
                            // Position at fixed bottom distance
                            watermarkY = getHeight() - 15;
                        }
                        
                        // Create watermark font
                        float scaledWatermarkFontSize = watermarkFontSize * (float)scaleRatio;
                        scaledWatermarkFontSize = Math.max(8f, scaledWatermarkFontSize); // Minimum 8pt
                        
                        // Apply letter spacing (-5%) to watermark font
                        Map<TextAttribute, Object> watermarkAttributes = new HashMap<>();
                        watermarkAttributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
                        Font scaledWatermarkFont = interMedium.deriveFont(scaledWatermarkFontSize).deriveFont(watermarkAttributes);
                        
                        g2d.setFont(scaledWatermarkFont);
                        // Use the new watermark color
                        g2d.setColor(watermarkColor);
                        
                        // Center watermark horizontally
                        FontMetrics watermarkMetrics = g2d.getFontMetrics();
                        int watermarkWidth = watermarkMetrics.stringWidth(watermarkText);
                        int watermarkX = (getWidth() - watermarkWidth) / 2;
                        
                        g2d.drawString(watermarkText, watermarkX, watermarkY);
                    }
                }
            }
            
            /**
             * Helper method to draw multi-line text with center alignment
             * @param g2d Graphics2D context to draw with
             * @param text The text to draw
             * @param x X position to start drawing
             * @param y Y position for the first line
             * @param maxWidth Maximum width before wrapping
             */
            private void drawCenteredText(Graphics2D g2d, String text, int x, int y, int maxWidth) {
                if (text == null || text.isEmpty()) {
                    return;
                }
                
                FontMetrics fm = g2d.getFontMetrics();
                int lineHeight = fm.getHeight();
                
                String[] words = text.split("\\s+");
                List<String> lines = new ArrayList<>();
                StringBuilder currentLine = new StringBuilder();
                
                // First, break the text into lines
                for (String word : words) {
                    String potentialLine = currentLine.length() > 0 ? currentLine + " " + word : word;
                    if (fm.stringWidth(potentialLine) < maxWidth) {
                        // Add the word to the current line
                        if (currentLine.length() > 0) {
                            currentLine.append(" ");
                        }
                        currentLine.append(word);
                    } else {
                        // End current line and start a new one
                        if (currentLine.length() > 0) {
                            lines.add(currentLine.toString());
                            currentLine = new StringBuilder(word);
                        } else {
                            // This single word is too long
                            lines.add(word);
                        }
                    }
                }
                
                // Add the last line
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                }
                
                // Now draw each line with center alignment
                int currentY = y;
                for (String line : lines) {
                    int textWidth = fm.stringWidth(line);
                    int textX = x + (maxWidth - textWidth) / 2; // Center the text
                    
                    g2d.drawString(line, textX, currentY);
                    currentY += lineHeight;
                }
            }
            
            private boolean paintedOnce = false;
            private boolean headerPaintedOnce = false;
        };
        backgroundPanel.setLayout(new GridBagLayout());
        
        // Create admin button in the top left corner
        adminButton = new JButton("Admin");
        if (interMedium != null) {
            // Use Inter-SemiBold if available, with Medium as fallback
            Font adminFont = interSemiBold != null ? interSemiBold.deriveFont(14f) : interMedium.deriveFont(14f);
            adminButton.setFont(adminFont);
        } else {
            adminButton.setFont(new Font("Sans-Serif", Font.BOLD, 14));
        }
        adminButton.setForeground(Color.WHITE);
        adminButton.setBackground(new Color(0x2F, 0x39, 0x8E)); // #2F398E - Primary blue
        adminButton.setFocusPainted(false);
        adminButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Apply the same styled button behavior as the main buttons
        adminButton = createStyledAdminButton("Admin", new Color(0x2F, 0x39, 0x8E));
        
        // Create a layer for absolute positioning components on top of the main layout
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1440, 1024));
        
        // Add the main background panel to the layered pane
        backgroundPanel.setBounds(0, 0, 1440, 1024);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);
        
        // Position the admin button in the top right corner with equal margin from top and right edge
        int margin = 20;
        int adminButtonWidth = 120;
        int adminButtonHeight = 40;
        int rightOffset = 40; // Increased distance from right edge
        adminButton.setBounds(getWidth() - adminButtonWidth - margin - rightOffset, margin, adminButtonWidth, adminButtonHeight);
        layeredPane.add(adminButton, JLayeredPane.PALETTE_LAYER);
        
        // Create panel to hold the buttons in a 2x2 grid
        buttonPanel = new JPanel(new GridLayout(2, 2, horizontalGap, verticalGap));
        buttonPanel.setOpaque(false); // Make panel transparent
        
        // Create four buttons with specific size and colors
        button1 = createStyledButton("Search Candidate", new Color(0x2F, 0x39, 0x8E)); // #2f398e
        button2 = createStyledButton("Compare Candidates", new Color(0xF9, 0xB3, 0x45)); // #f9b345
        button3 = createStyledButton("Candidates Overview", new Color(0xEB, 0x42, 0x3E)); // #eb423e
        button4 = createStyledButton("Gabáy Quiz Match", new Color(0x2F, 0x39, 0x8E)); // #2f398e
        
        // Set initial font size for all buttons
        adjustButtonFont(button1, originalButtonFontSize);
        adjustButtonFont(button2, originalButtonFontSize);
        adjustButtonFont(button3, originalButtonFontSize);
        adjustButtonFont(button4, originalButtonFontSize);
        
        // Set preferred size for all buttons
        button1.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button2.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button3.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button4.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        
        // Add action listeners to buttons
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the Search Candidate page
                Dimension currentSize = getSize();
                dispose(); // Close the current window
                CandidateSearch searchPage = new CandidateSearch();
                searchPage.setSize(currentSize); // Set the same size as current window
                searchPage.setLocationRelativeTo(null); // Center on screen
                searchPage.setVisible(true);
            }
        });
        
        // Compare button no longer opens the CandidateComparison page
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Just show a message - CandidateComparison has been deleted
                JOptionPane.showMessageDialog(LandingPage.this, 
                    "Candidate Comparison feature has been removed.", 
                    "Feature Unavailable", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Add buttons to the panel
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);
        buttonPanel.add(button4);
        
        // Add the button panel to the background panel
        backgroundPanel.add(buttonPanel);
        
        // Add the layered pane to the frame
        setContentPane(layeredPane);
        
        // Add component listener to handle resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // This ensures layout is updated when window is resized
                adjustLayoutForWindowSize(); // This will adjust buttons and calculate space
                
                // Update the layered pane and background panel sizes
                layeredPane.setSize(getSize());
                backgroundPanel.setSize(getSize());
                
                // Update admin button position to right side with equal margins
                int margin = 20;
                int rightOffset = 40; // Increased distance from right edge
                int adminButtonWidth = 120;
                adminButton.setLocation(getWidth() - adminButtonWidth - margin - rightOffset, margin);
                
                revalidate();
                repaint(); // Make sure the background is redrawn
                
                // Log window size for debugging
                System.out.println("Window resized to: " + getWidth() + "x" + getHeight());
            }
        });
        
        // Add a key listener to toggle the background image
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_B) {
                    showBackgroundImage = !showBackgroundImage;
                    System.out.println("Background image " + (showBackgroundImage ? "shown" : "hidden"));
                    repaint();
                }
            }
        });
        
        // Make the frame focusable to receive key events
        setFocusable(true);
    }
    
    private void loadHeaderImage() {
        try {
            System.out.println("\n----- IMAGE DEBUGGING -----");
            
            // Get the current working directory
            String currentDir = System.getProperty("user.dir");
            
            // Try multiple possible paths
            String[] possiblePaths = {
                "resources/images/Landing_Header.png",
                "../resources/images/Landing_Header.png",
                "../../resources/images/Landing_Header.png",
                "src/resources/images/Landing_Header.png",
                "./resources/images/Landing_Header.png",
                new File(currentDir).getParent() + "/resources/images/Landing_Header.png"
            };
            
            File headerFile = null;
            for (String path : possiblePaths) {
                File testFile = new File(path);
                System.out.println("Checking for header image at: " + testFile.getAbsolutePath());
                if (testFile.exists()) {
                    headerFile = testFile;
                    System.out.println("Found header image at: " + headerFile.getAbsolutePath());
                    break;
                }
            }
            
            if (headerFile != null && headerFile.exists()) {
                headerImage = ImageIO.read(headerFile);
                System.out.println("Header image loaded successfully. Size: " + 
                                   headerImage.getWidth() + "x" + headerImage.getHeight());
            } else {
                System.out.println("Header image not found in any of the checked locations");
                createFallbackHeaderImage();
            }
        } catch (IOException e) {
            System.out.println("Error loading header image: " + e.getMessage());
            e.printStackTrace();
            createFallbackHeaderImage();
        }
    }
    
    private void createFallbackHeaderImage() {
        // Create a simple placeholder for the header
        System.out.println("Creating fallback header image...");
        headerImage = new BufferedImage(800, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = headerImage.createGraphics();
        g.setColor(new Color(0xF5, 0xF5, 0xF5)); // Light gray background
        g.fillRect(0, 0, 800, 200);
        g.setColor(new Color(0x2F, 0x39, 0x8E)); // Primary blue
        g.setFont(new Font("Dialog", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String text = "GABÁY";
        int textWidth = fm.stringWidth(text);
        g.drawString(text, (800 - textWidth) / 2, 110);
        g.dispose();
        System.out.println("Fallback header image created.");
    }
    
    private void loadSearchIconImage() {
        try {
            // Get the current working directory for debugging
            String currentDir = System.getProperty("user.dir");
            System.out.println("Current working directory: " + currentDir);
            
            // Define base directories to search
            String[] baseDirs = {
                "resources/images",
                "../resources/images",
                "../../resources/images",
                "src/resources/images",
                "./resources/images",
                new File(currentDir).getParent() + "/resources/images"
            };
            
            // Load search icon
            File searchIconFile = findFileInBaseDirs(baseDirs, "Buttons Icon/search_masked.png");
            if (searchIconFile != null && searchIconFile.exists()) {
                searchIconImage = ImageIO.read(searchIconFile);
                System.out.println("Search icon image loaded successfully. Size: " + 
                                   searchIconImage.getWidth() + "x" + searchIconImage.getHeight());
            }
            
            // Load compare icon
            File compareIconFile = findFileInBaseDirs(baseDirs, "Buttons Icon/compare_masked.png");
            if (compareIconFile != null && compareIconFile.exists()) {
                compareIconImage = ImageIO.read(compareIconFile);
                System.out.println("Compare icon loaded successfully");
            }
            
            // Load overview icon
            File overviewIconFile = findFileInBaseDirs(baseDirs, "Buttons Icon/overview_masked.png");
            if (overviewIconFile != null && overviewIconFile.exists()) {
                overviewIconImage = ImageIO.read(overviewIconFile);
                System.out.println("Overview icon loaded successfully");
            }
            
            // Load quiz icon
            File quizIconFile = findFileInBaseDirs(baseDirs, "Buttons Icon/quiz_masked.png");
            if (quizIconFile != null && quizIconFile.exists()) {
                quizIconImage = ImageIO.read(quizIconFile);
                System.out.println("Quiz icon loaded successfully");
            }
            
        } catch (IOException e) {
            System.out.println("Error loading icon images: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to find a file by searching in multiple base directories
     * @param baseDirs Array of base directories to search in
     * @param fileName The file name to look for in each base directory
     * @return The File object if found, null otherwise
     */
    private File findFileInBaseDirs(String[] baseDirs, String fileName) {
        for (String baseDir : baseDirs) {
            File testFile = new File(baseDir + "/" + fileName);
            if (testFile.exists()) {
                System.out.println("Found file at: " + testFile.getAbsolutePath());
                return testFile;
            }
        }
        System.out.println("Could not find file: " + fileName);
        return null;
    }
    
    private void loadBackgroundImage() {
        try {
            // Get the current working directory
            String currentDir = System.getProperty("user.dir");
            System.out.println("Current working directory: " + currentDir);
            
            // Try multiple possible locations for the image file
            String[] possiblePaths = {
                "resources/images/Landing-Backdrop.png",
                "../resources/images/Landing-Backdrop.png",
                "../../resources/images/Landing-Backdrop.png",
                "src/resources/images/Landing-Backdrop.png",
                "./resources/images/Landing-Backdrop.png",
                "Landing-Backdrop.png",
                "images/Landing-Backdrop.png",
                "../resources/images/Landing-Backdrop.png",
                new File(currentDir).getParent() + "/resources/images/Landing-Backdrop.png"
            };
            
            File imageFile = null;
            for (String path : possiblePaths) {
                File testFile = new File(path);
                System.out.println("Checking for image at: " + testFile.getAbsolutePath() + 
                                  " (exists: " + testFile.exists() + ")");
                if (testFile.exists()) {
                    imageFile = testFile;
                    break;
                }
            }
            
            if (imageFile != null && imageFile.exists()) {
                System.out.println("Found image at: " + imageFile.getAbsolutePath());
                System.out.println("Image file exists. Size: " + imageFile.length() + " bytes");
                
                backgroundImage = ImageIO.read(imageFile);
                if (backgroundImage != null) {
                    System.out.println("Background image loaded successfully. Dimensions: " + 
                                      backgroundImage.getWidth() + "x" + backgroundImage.getHeight() +
                                      ", Alpha: " + backgroundImage.getColorModel().hasAlpha());
                } else {
                    System.out.println("Failed to read image data from file: " + imageFile.getAbsolutePath());
                    createFallbackImage();
                }
            } else {
                System.out.println("Background image file not found in any of the checked locations.");
                createFallbackImage();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading background image: " + e.getMessage());
            createFallbackImage();
        }
    }
    
    private void createFallbackImage() {
        // Create a simple colored rectangle as a fallback
        System.out.println("Creating fallback image...");
        backgroundImage = new BufferedImage(1802, 1519, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = backgroundImage.createGraphics();
        g.setColor(new Color(230, 230, 250)); // Light lavender color
        g.fillRect(0, 0, 1802, 1519);
        g.setColor(new Color(0x2F, 0x39, 0x8E, 100)); // Translucent blue
        int border = 20;
        g.fillRect(border, border, 1802-2*border, 1519-2*border);
        g.dispose();
        System.out.println("Fallback image created.");
    }
    
    private void loadFonts() {
        try {
            System.out.println("\n----- FONT DEBUGGING -----");
            
            // Get the current working directory
            String currentDir = System.getProperty("user.dir");
            System.out.println("Current working directory: " + currentDir);
            
            // Try multiple possible font directory locations
            File fontsDir = null;
            String[] possibleFontPaths = {
                "lib/fonts",                  // relative to working directory
                "../lib/fonts",               // one level up
                "../../lib/fonts",            // two levels up
                "../../../lib/fonts",         // three levels up
                "src/lib/fonts",              // if running from project root but files are in src
                "./lib/fonts",                // explicit current directory
                new File(currentDir).getParent() + "/lib/fonts" // parent of current dir
            };
            
            // Try each path until we find one that exists
            for (String path : possibleFontPaths) {
                File testDir = new File(path);
                System.out.println("Checking font directory: " + testDir.getAbsolutePath());
                if (testDir.exists() && testDir.isDirectory()) {
                    fontsDir = testDir;
                    System.out.println("Found fonts directory at: " + fontsDir.getAbsolutePath());
                    break;
                }
            }
            
            if (fontsDir == null || !fontsDir.exists()) {
                System.out.println("Could not find fonts directory in any of the checked locations.");
                // Use system fonts as fallback
                interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
                interBlack = new Font("Sans-Serif", Font.BOLD, 12);
                interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
                interBold = new Font("Sans-Serif", Font.BOLD, 12);
                interMedium = new Font("Sans-Serif", Font.PLAIN, 12);
                return; // Exit early as we don't have font files
            }
            
            System.out.println("Fonts directory exists: " + fontsDir.exists() + " at " + fontsDir.getAbsolutePath());
            
            // Create file objects for all required fonts (use fontsDir as base)
            File interBlackFile = new File(fontsDir, "Inter_18pt-Black.ttf");
            File interSemiBoldFile = new File(fontsDir, "Inter_18pt-SemiBold.ttf");
            File interBoldFile = new File(fontsDir, "Inter_18pt-Bold.ttf");
            File interMediumFile = new File(fontsDir, "Inter_18pt-Medium.ttf");
            File interRegularFile = new File(fontsDir, "Inter_18pt-Regular.ttf");
            
            // Check if any required fonts are missing
            if (!interBlackFile.exists() || !interSemiBoldFile.exists() || 
                !interBoldFile.exists() || !interMediumFile.exists()) {
                System.out.println("Some font files not found, looking for alternatives...");
                // List all files in the fonts directory
                System.out.println("Files in fonts directory:");
                
                if (fontsDir.exists()) {
                    File[] files = fontsDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            System.out.println(" - " + file.getName() + " (" + file.length() + " bytes)");
                            
                            String fileName = file.getName().toLowerCase();
                            
                            // Look for any Inter Regular font
                            if (fileName.contains("inter") && fileName.contains("regular") && !interRegularFile.exists()) {
                                interRegularFile = file;
                                System.out.println("   Found Regular font: " + file.getName());
                            }
                            
                            // Look for Black font
                            if (fileName.equals("inter_18pt-black.ttf")) {
                                interBlackFile = file;
                                System.out.println("   Found Inter_18pt-Black.ttf: " + file.getName());
                            } else if (fileName.contains("inter") && fileName.contains("black") && !interBlackFile.exists()) {
                                interBlackFile = file;
                                System.out.println("   Found Black font (fallback): " + file.getName());
                            }
                            
                            // Look for SemiBold font
                            if (fileName.equals("inter_18pt-semibold.ttf")) {
                                interSemiBoldFile = file;
                                System.out.println("   Found Inter_18pt-SemiBold.ttf: " + file.getName());
                            } else if (fileName.contains("inter") && fileName.contains("semibold") && !interSemiBoldFile.exists()) {
                                interSemiBoldFile = file;
                                System.out.println("   Found SemiBold font (fallback): " + file.getName());
                            }
                            
                            // Look for Bold font
                            if (fileName.equals("inter_18pt-bold.ttf")) {
                                interBoldFile = file;
                                System.out.println("   Found Inter_18pt-Bold.ttf: " + file.getName());
                            } else if (fileName.contains("inter") && fileName.contains("bold") && 
                                     !fileName.contains("semibold") && !fileName.contains("extrabold") && 
                                     !interBoldFile.exists()) {
                                interBoldFile = file;
                                System.out.println("   Found Bold font (fallback): " + file.getName());
                            }
                            
                            // Look for Medium font
                            if (fileName.equals("inter_18pt-medium.ttf")) {
                                interMediumFile = file;
                                System.out.println("   Found Inter_18pt-Medium.ttf: " + file.getName());
                            } else if (fileName.contains("inter") && fileName.contains("medium") && !interMediumFile.exists()) {
                                interMediumFile = file;
                                System.out.println("   Found Medium font (fallback): " + file.getName());
                            }
                        }
                    } else {
                        System.out.println(" (No files found or unable to list files)");
                    }
                }
            } else {
                System.out.println("Found all required font files");
            }
            
            // Load and register the fonts
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            
            // Load Regular font first (used as fallback)
            if (interRegularFile != null && interRegularFile.exists()) {
                System.out.println("Loading Regular font: " + interRegularFile.getAbsolutePath());
                interRegular = Font.createFont(Font.TRUETYPE_FONT, interRegularFile);
                ge.registerFont(interRegular);
                System.out.println("Regular font loaded successfully: " + interRegular.getFamily());
            } else {
                interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
                System.out.println("Using system font for Regular");
            }
            
            // Load Black font
            if (interBlackFile != null && interBlackFile.exists()) {
                System.out.println("Loading Black font: " + interBlackFile.getAbsolutePath());
                interBlack = Font.createFont(Font.TRUETYPE_FONT, interBlackFile);
                ge.registerFont(interBlack);
                System.out.println("Black font loaded successfully: " + interBlack.getFamily());
            } else {
                interBlack = new Font("Sans-Serif", Font.BOLD, 12);
                System.out.println("Using system font for Black");
            }
            
            // Load SemiBold font
            if (interSemiBoldFile != null && interSemiBoldFile.exists()) {
                System.out.println("Loading SemiBold font: " + interSemiBoldFile.getAbsolutePath());
                interSemiBold = Font.createFont(Font.TRUETYPE_FONT, interSemiBoldFile);
                ge.registerFont(interSemiBold);
                System.out.println("SemiBold font loaded successfully: " + interSemiBold.getFamily());
            } else {
                interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
                System.out.println("Using system font for SemiBold");
            }
            
            // Load Bold font
            if (interBoldFile != null && interBoldFile.exists()) {
                System.out.println("Loading Bold font: " + interBoldFile.getAbsolutePath());
                interBold = Font.createFont(Font.TRUETYPE_FONT, interBoldFile);
                ge.registerFont(interBold);
                System.out.println("Bold font loaded successfully: " + interBold.getFamily());
            } else {
                interBold = new Font("Sans-Serif", Font.BOLD, 12);
                System.out.println("Using system font for Bold");
            }
            
            // Load Medium font
            if (interMediumFile != null && interMediumFile.exists()) {
                System.out.println("Loading Medium font: " + interMediumFile.getAbsolutePath());
                interMedium = Font.createFont(Font.TRUETYPE_FONT, interMediumFile);
                ge.registerFont(interMedium);
                System.out.println("Medium font loaded successfully: " + interMedium.getFamily());
            } else {
                interMedium = new Font("Sans-Serif", Font.PLAIN, 12);
                System.out.println("Using system font for Medium");
            }
            
            System.out.println("-------------------------\n");
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            // Fallback to system fonts
            interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
            interBlack = new Font("Sans-Serif", Font.BOLD, 12);
            interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
            interBold = new Font("Sans-Serif", Font.BOLD, 12);
            interMedium = new Font("Sans-Serif", Font.PLAIN, 12);
            System.out.println("Error loading fonts, using system fallbacks: " + e.getMessage());
        }
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        // Create a custom JButton with rounded corners and hover animation
        class StyledButton extends JButton {
            // Animation parameters
            private boolean isHovered = false;
            private int slideOffset = 0;
            private final int MAX_SLIDE = 500; // Increased maximum slide to ensure button completely exits frame
            private int ANIMATION_DURATION = 500; // Changed from final to allow dynamic adjustment
            private Timer animationTimer;
            private String hoverText = "Proceed";
            private String pageName = ""; // Store specific page name
            private String hoverTitle = ""; // First line - larger text
            private String hoverDescription = ""; // Second line - smaller text
            // Store text Y position to prevent shifting
            private int textBaselineY = 0;
            
            // Animation timing variables
            private long animationStartTime = 0;
            private int animationStartOffset = 0;
            private int animationTargetOffset = 0;

            // Zoom animation parameters
            private float zoomFactor = 1.0f;
            private final float MAX_ZOOM = 1.12f; // Increased from 1.05f to 1.12f (12% larger) for more obvious effect

            public StyledButton(String text) {
                super(text);
                
                // Initialize animation timer
                animationTimer = new Timer(16, new ActionListener() { // 16ms for ~60fps
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        long currentTime = System.currentTimeMillis();
                        float progress = Math.min(1.0f, (currentTime - animationStartTime) / (float)ANIMATION_DURATION);
                        
                        // Apply cubic bezier easing curve for smooth motion
                        float easedProgress = bezierEase(progress);
                        
                        // Calculate current position based on progress
                        slideOffset = animationStartOffset + (int)((animationTargetOffset - animationStartOffset) * easedProgress);
                        
                        // Apply zoom animation based on progress
                        if (isHovered) {
                            // Zoom in faster than sliding (complete in first 30% of animation)
                            float zoomProgress = Math.min(1.0f, progress / 0.3f);
                            zoomFactor = 1.0f + (MAX_ZOOM - 1.0f) * bezierEase(zoomProgress);
                        } else {
                            // Zoom out slightly faster than sliding (complete in first 40% of animation)
                            float zoomProgress = Math.min(1.0f, progress / 0.4f);
                            zoomFactor = MAX_ZOOM - (MAX_ZOOM - 1.0f) * bezierEase(zoomProgress);
                        }
                        
                        // Stop the timer when animation completes
                        if (progress >= 1.0f) {
                            slideOffset = animationTargetOffset;
                            zoomFactor = isHovered ? MAX_ZOOM : 1.0f; // Ensure final state is correct
                            animationTimer.stop();
                        }
                        
                        repaint();
                    }
                });

                // Add mouse listeners for hover effect
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        startAnimation(true);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        startAnimation(false);
                    }
                });
            }
            
            /**
             * Applies a cubic bezier curve easing function to create smooth animation
             * This particular curve gives a slow start, fast middle, and gentle finish
             * @param t Progress value from 0.0 to 1.0
             * @return Eased value from 0.0 to 1.0
             */
            private float bezierEase(float t) {
                // Cubic bezier parameters for a natural motion curve
                if (t <= 0) return 0;
                if (t >= 1) return 1;
                
                // Customize curve based on animation direction
                if (isHovered) {
                    // Opening curve: slow start (momentum), fast middle, slow end
                    // Control points for ease-in-out with heavier middle acceleration
                    return cubicBezier(t, 0.33f, 0.0f, 0.67f, 1.0f);
                } else {
                    // Closing curve: slow start, fast middle, slow end
                    // Using same curve for consistency in both directions
                    return cubicBezier(t, 0.33f, 0.0f, 0.67f, 1.0f);
                }
            }
            
            /**
             * Calculate a point on a cubic bezier curve
             * @param t Parameter from 0.0 to 1.0
             * @param p1x First control point x
             * @param p1y First control point y
             * @param p2x Second control point x
             * @param p2y Second control point y
             * @return The y value of the bezier curve at t
             */
            private float cubicBezier(float t, float p1x, float p1y, float p2x, float p2y) {
                // Newton-Raphson iteration to solve for t parameter given x
                float cx = 3.0f * p1x;
                float bx = 3.0f * (p2x - p1x) - cx;
                float ax = 1.0f - cx - bx;
                
                float cy = 3.0f * p1y;
                float by = 3.0f * (p2y - p1y) - cy;
                float ay = 1.0f - cy - by;
                
                // Solve for y given t
                return ((ay * t + by) * t + cy) * t;
            }
            
            /**
             * Start an animation with bezier easing
             * @param hovering Whether mouse is entering (true) or exiting (false)
             */
            private void startAnimation(boolean hovering) {
                isHovered = hovering;
                animationStartTime = System.currentTimeMillis();
                animationStartOffset = slideOffset;
                animationTargetOffset = hovering ? MAX_SLIDE : 0;
                
                // No need to set duration here since it's already set to 500ms
                
                if (!animationTimer.isRunning()) {
                    animationTimer.start();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int cornerRadius = 10; // Corner radius
                
                // Calculate baseline Y position for text once to prevent shifting
                if (textBaselineY == 0) {
                    FontMetrics fm = g2d.getFontMetrics(getFont());
                    textBaselineY = height / 2 + fm.getAscent() / 2;
                }
                
                // Create a slight shadow effect when zoomed to emphasize the elevation
                if (zoomFactor > 1.0f) {
                    // Draw a subtle shadow beneath the button to enhance zoom effect
                    g2d.setColor(new Color(0, 0, 0, 30)); // Very light shadow (30/255 alpha)
                    g2d.fill(new RoundRectangle2D.Double(5, 5, width, height, cornerRadius, cornerRadius));
                }
                
                // Apply zoom transformation
                if (zoomFactor > 1.0f) {
                    // Calculate scale parameters to zoom from center
                    float scaleX = zoomFactor;
                    float scaleY = zoomFactor;
                    
                    // Calculate translation to keep button centered during zoom
                    float transX = width * (1 - scaleX) / 2;
                    float transY = height * (1 - scaleY) / 2;
                    
                    // Apply the transformation
                    g2d.translate(transX, transY);
                    g2d.scale(scaleX, scaleY);
                }
                
                // Rest of the paint component method remains the same
                // Paint background with rounded corners - original button background
                g2d.setColor(getBackground());
                
                // When zoomed, use a slightly brighter color to enhance the effect
                if (zoomFactor > 1.01f) {
                    // Create a brighter version of the background color
                    Color bgColor = getBackground();
                    float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
                    // Increase brightness slightly but maintain the same hue and saturation
                    float brightnessFactor = Math.min(1.0f, hsb[2] * 1.15f);
                    Color brighterBg = Color.getHSBColor(hsb[0], hsb[1], brightnessFactor);
                    g2d.setColor(brighterBg);
                }
                
                g2d.fill(new RoundRectangle2D.Double(0, 0, width, height, cornerRadius, cornerRadius));
                
                // Draw the sliding effect - main button slides left to reveal content underneath
                if (slideOffset > 0) {
                    // Draw button portion that's sliding away
                    g2d.setColor(getBackground());
                    g2d.setClip(new RoundRectangle2D.Double(-slideOffset, 0, width, height, cornerRadius, cornerRadius));
                    g2d.fillRect(-slideOffset, 0, width, height);
                    g2d.setClip(null);
                    
                    // Draw the revealed area with darker shade of the same color
                    Color darkerBg = getBackground().darker();
                    g2d.setColor(darkerBg);
                    
                    // Create a custom shape that's only rounded on the right side for the revealed area
                    RoundRectangle2D.Double revealedArea = new RoundRectangle2D.Double(
                        0, 0, width, height, cornerRadius, cornerRadius);
                    g2d.fill(revealedArea);
                    
                    // Draw the multi-line hover text in the revealed area
                    g2d.setColor(Color.WHITE);
                    
                    // Only draw text once button has slid enough
                    if (slideOffset > 100) {
                        // First line - title text with Inter SemiBold at larger size
                        Font titleFont = interSemiBold;
                        if (titleFont != null) {
                            // Scale font size based on button height
                            float titleFontSize = 18f * (height / (float)buttonHeight);
                            titleFontSize = Math.max(12f, titleFontSize); // Minimum size
                            titleFont = titleFont.deriveFont(titleFontSize);
                            
                            // Apply letter spacing (-5%)
                            Map<TextAttribute, Object> attributes = new HashMap<>();
                            attributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
                            titleFont = titleFont.deriveFont(attributes);
                        } else {
                            // Fallback if Inter SemiBold is not available
                            titleFont = new Font("SansSerif", Font.PLAIN, (int)(18 * (height / (float)buttonHeight)));
                        }
                        
                        // Second line - description text with Inter Medium at smaller size
                        Font descFont = interMedium;
                        if (descFont != null) {
                            // Smaller size for description (14pt)
                            float descFontSize = 14f * (height / (float)buttonHeight);
                            descFontSize = Math.max(9f, descFontSize); // Minimum size
                            descFont = descFont.deriveFont(descFontSize);
                            
                            // Apply letter spacing (-5%)
                            Map<TextAttribute, Object> attributes = new HashMap<>();
                            attributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
                            descFont = descFont.deriveFont(attributes);
                        } else {
                            // Fallback if Inter Medium is not available
                            descFont = new Font("SansSerif", Font.PLAIN, (int)(14 * (height / (float)buttonHeight)));
                        }
                        
                        // Draw the title (first line) - position more toward center
                        g2d.setFont(titleFont);
                        FontMetrics titleFm = g2d.getFontMetrics();
                        int titleWidth = titleFm.stringWidth(hoverTitle);
                        int titleX = (width - titleWidth) / 2;
                        int titleY = height / 2 - 10; // Position more toward vertical center
                        
                        g2d.drawString(hoverTitle, titleX, titleY);
                        
                        // Draw the description (second line) - with wrapping if needed
                        g2d.setFont(descFont);
                        FontMetrics descFm = g2d.getFontMetrics();
                        
                        // Calculate available width for description (leave margins)
                        int margin = width / 10; // 10% margin on each side
                        int availableWidth = width - (margin * 2);
                        
                        // Wrap text if needed
                        String[] descWords = hoverDescription.split(" ");
                        StringBuilder currentLine = new StringBuilder();
                        int descY = titleY + titleFm.getHeight() + 5; // Start below title with small gap
                        
                        for (String word : descWords) {
                            if (currentLine.length() > 0) {
                                String testLine = currentLine + " " + word;
                                if (descFm.stringWidth(testLine) <= availableWidth) {
                                    currentLine.append(" ").append(word);
                                } else {
                                    // Draw current line and start a new one
                                    String lineToRender = currentLine.toString();
                                    int lineWidth = descFm.stringWidth(lineToRender);
                                    int lineX = (width - lineWidth) / 2;
                                    g2d.drawString(lineToRender, lineX, descY);
                                    
                                    descY += descFm.getHeight();
                                    currentLine = new StringBuilder(word);
                                }
                            } else {
                                currentLine.append(word);
                            }
                        }
                        
                        // Draw the last line if there's anything left
                        if (currentLine.length() > 0) {
                            String lineToRender = currentLine.toString();
                            int lineWidth = descFm.stringWidth(lineToRender);
                            int lineX = (width - lineWidth) / 2;
                            g2d.drawString(lineToRender, lineX, descY);
                        }
                    }
                    
                    // Draw the sliding button on top
                    g2d.setColor(getBackground());
                    g2d.fill(new RoundRectangle2D.Double(-slideOffset, 0, width, height, cornerRadius, cornerRadius));
                    
                    // Only draw main button text and icon if button is not fully slid away
                    if (slideOffset < width + 50) { // Add some extra buffer
                        // Draw icon on sliding button if available
                        BufferedImage iconImage = null;
                        if (getText().equals("Search Candidate") && searchIconImage != null) {
                            iconImage = searchIconImage;
                        } else if (getText().equals("Compare Candidates") && compareIconImage != null) {
                            iconImage = compareIconImage;
                        } else if (getText().equals("Candidates Overview") && overviewIconImage != null) {
                            iconImage = overviewIconImage;
                        } else if (getText().equals("Gabáy Quiz Match") && quizIconImage != null) {
                            iconImage = quizIconImage;
                        }
                        
                        // If we have an icon for this button, draw it on sliding portion
                        if (iconImage != null) {
                            // Scale the icon based on button size
                            double iconScaleFactor = height / (double)buttonHeight;
                            int scaledIconWidth = (int)(iconImage.getWidth() * iconScaleFactor);
                            int scaledIconHeight = (int)(iconImage.getHeight() * iconScaleFactor);
                            
                            int iconY = (height - scaledIconHeight) / 2;
                            int iconX;
                            
                            // Position icons based on button type
                            if (getText().equals("Compare Candidates") || getText().equals("Gabáy Quiz Match")) {
                                // Position these icons on the left side
                                iconX = (int)(0 * iconScaleFactor); // 10px padding from left edge
                            } else {
                                // Position other icons on the right side
                                iconX = width - scaledIconWidth - (int)(40 * iconScaleFactor);
                            }
                            
                            // Make the icon semi-transparent to blend with the button background
                            AlphaComposite alphaComposite = AlphaComposite.getInstance(
                                AlphaComposite.SRC_OVER, 0.3f);
                            g2d.setComposite(alphaComposite);
                            
                            // Draw icon on the sliding button
                            iconX -= slideOffset;
                            g2d.drawImage(iconImage, iconX, iconY, scaledIconWidth, scaledIconHeight, this);
                            
                            // Reset composite for text drawing
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                        }
                        
                        // Draw text on sliding button
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                
                        g2d.setColor(getForeground());
                        g2d.setFont(getFont());
                        
                FontMetrics fm = g2d.getFontMetrics();
                        String buttonText = getText();
                        int originalTextX = (width - fm.stringWidth(buttonText)) / 2;
                        
                        // Draw text on sliding portion
                        g2d.drawString(buttonText, originalTextX - slideOffset, textBaselineY);
                    }
                } else {
                    // No sliding - just draw the icon and text normally
                    // Draw icon if available
                    BufferedImage iconImage = null;
                    if (getText().equals("Search Candidate") && searchIconImage != null) {
                        iconImage = searchIconImage;
                    } else if (getText().equals("Compare Candidates") && compareIconImage != null) {
                        iconImage = compareIconImage;
                    } else if (getText().equals("Candidates Overview") && overviewIconImage != null) {
                        iconImage = overviewIconImage;
                    } else if (getText().equals("Gabáy Quiz Match") && quizIconImage != null) {
                        iconImage = quizIconImage;
                    }
                    
                    // If we have an icon for this button, draw it
                    if (iconImage != null) {
                        // Scale the icon based on button size
                        double iconScaleFactor = height / (double)buttonHeight;
                        int scaledIconWidth = (int)(iconImage.getWidth() * iconScaleFactor);
                        int scaledIconHeight = (int)(iconImage.getHeight() * iconScaleFactor);
                        
                        int iconY = (height - scaledIconHeight) / 2;
                        int iconX;
                        
                        // Position icons based on button type
                        if (getText().equals("Compare Candidates") || getText().equals("Gabáy Quiz Match")) {
                            // Position these icons on the left side
                            iconX = (int)(0 * iconScaleFactor); // 10px padding from left edge
                        } else {
                            // Position other icons on the right side
                            iconX = width - scaledIconWidth - (int)(40 * iconScaleFactor);
                        }
                        
                        // Make the icon semi-transparent to blend with the button background
                        AlphaComposite alphaComposite = AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER, 0.3f);
                        g2d.setComposite(alphaComposite);
                        
                        g2d.drawImage(iconImage, iconX, iconY, scaledIconWidth, scaledIconHeight, this);
                        
                        // Reset composite for text drawing
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    }
                    
                    // Draw button text
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                    
                    FontMetrics fm = g2d.getFontMetrics();
                String buttonText = getText();
                
                int x = (width - fm.stringWidth(buttonText)) / 2;
                
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                    g2d.drawString(buttonText, x, textBaselineY);
                }
                
                g2d.dispose();
            }
            
            // Set title for first line
            public void setHoverTitle(String title) {
                this.hoverTitle = title;
            }
            
            // Set description for second line
            public void setHoverDescription(String description) {
                this.hoverDescription = description;
            }

            // Set custom hover text for each button
            public void setHoverText(String text) {
                this.hoverText = text;
            }
            
            // Set page name for "Proceed to" text
            public void setPageName(String name) {
                this.pageName = name;
            }
        }
        
        // Create button instance
        StyledButton button = new StyledButton(text);
        
        // Enhance button appearance
        button.setForeground(Color.WHITE); // White text for better contrast
        button.setBackground(bgColor);
        button.setBorderPainted(false); // Remove button border
        button.setFocusPainted(false); // Remove focus border
        button.setContentAreaFilled(false); // Required for custom paint
        button.setOpaque(false); // Required for rounded corners to show
        
        // Set hover text depending on button
        if (text.equals("Search Candidate")) {
            button.setPageName("Search");
            button.setHoverTitle("Looking for a candidate who shares your views?");
            button.setHoverDescription("Type in an issue and instantly see where each candidate stands.");
        } else if (text.equals("Compare Candidates")) {
            button.setPageName("Compare");
            button.setHoverTitle("Still deciding between two candidates?");
            button.setHoverDescription("Compare their platforms, experience, and values side by side.");
        } else if (text.equals("Candidates Overview")) {
            button.setPageName("Overview");
            button.setHoverTitle("Get to know your future leaders.");
            button.setHoverDescription("Explore detailed profiles, platforms, and stances on the issues that matter to you.");
        } else if (text.equals("Gabáy Quiz Match")) {
            button.setPageName("Quiz Match");
            button.setHoverTitle("Not sure who to support?");
            button.setHoverDescription("Take our quick quiz to find the candidate that best matches your beliefs.");
        }
        
        return button;
    }
    
    private void setUIFont(Font font) {
        // Set default font for all UI elements
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
    }
    
    /**
     * Shows a window displaying all fonts in the lib/fonts directory
     */
    private void showFontWindow() {
        JFrame fontFrame = new JFrame("Inter Font Preview");
        fontFrame.setSize(800, 600);
        fontFrame.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Create header
        JLabel headerLabel = new JLabel("Inter Font Preview");
        headerLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Create panel for font previews
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        // Add information about the directory
        JLabel dirInfoLabel = new JLabel("Looking for Inter fonts in: " + new File("lib/fonts").getAbsolutePath());
        dirInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(dirInfoLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Get all Inter font files in lib/fonts directory
        File fontsDir = new File("lib/fonts");
        if (fontsDir.exists() && fontsDir.isDirectory()) {
            File[] fontFiles = fontsDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().contains("inter");
                }
            });
            
            if (fontFiles != null && fontFiles.length > 0) {
                for (final File fontFile : fontFiles) {
                    try {
                        // Try to load the font
                        Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                        
                        // Create a panel for this font
                        JPanel fontPanel = new JPanel();
                        fontPanel.setLayout(new BoxLayout(fontPanel, BoxLayout.Y_AXIS));
                        fontPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                        fontPanel.setBackground(Color.WHITE);
                        fontPanel.setMaximumSize(new Dimension(700, 150));
                        fontPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        
                        // Font file information
                        JLabel fileLabel = new JLabel("Font file: " + fontFile.getName() + " (" + fontFile.length() + " bytes)");
                        fileLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
                        fileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        fontPanel.add(fileLabel);
                        fontPanel.add(Box.createVerticalStrut(10));
                        
                        // Font family information
                        JLabel familyLabel = new JLabel("Font family: " + font.getFamily());
                        familyLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
                        familyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        fontPanel.add(familyLabel);
                        fontPanel.add(Box.createVerticalStrut(10));
                        
                        // Create preview label with various sizes
                        JPanel previewPanel = new JPanel(new GridLayout(3, 1, 5, 5));
                        previewPanel.setBackground(Color.WHITE);
                        previewPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                        
                        // Small size
                        JLabel smallLabel = new JLabel("The quick brown fox jumps over the lazy dog (18pt)");
                        smallLabel.setFont(font.deriveFont(18f));
                        previewPanel.add(smallLabel);
                        
                        // Medium size
                        JLabel mediumLabel = new JLabel("ABCDEFGHIJKLMNOPQRSTUVWXYZ (24pt)");
                        mediumLabel.setFont(font.deriveFont(24f));
                        previewPanel.add(mediumLabel);
                        
                        // Large size - matching button size
                        JLabel largeLabel = new JLabel("1234567890 (38pt)");
                        largeLabel.setFont(font.deriveFont(Font.BOLD, 38f));
                        previewPanel.add(largeLabel);
                        
                        fontPanel.add(previewPanel);
                        
                        // Add the font panel to the content panel
                        contentPanel.add(fontPanel);
                        contentPanel.add(Box.createVerticalStrut(20));
                        
                    } catch (IOException | FontFormatException ex) {
                        // Create an error panel for this font
                        JPanel errorPanel = new JPanel();
                        errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS));
                        errorPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
                        errorPanel.setBackground(new Color(255, 235, 235));
                        errorPanel.setMaximumSize(new Dimension(700, 100));
                        errorPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        
                        JLabel errorLabel = new JLabel("Error loading font: " + fontFile.getName());
                        errorLabel.setForeground(Color.RED);
                        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        errorPanel.add(errorLabel);
                        
                        JLabel errorMsgLabel = new JLabel(ex.getMessage());
                        errorMsgLabel.setForeground(Color.RED);
                        errorMsgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        errorPanel.add(errorMsgLabel);
                        
                        contentPanel.add(errorPanel);
                        contentPanel.add(Box.createVerticalStrut(20));
                    }
                }
            } else {
                JLabel noFontsLabel = new JLabel("No Inter fonts found in lib/fonts directory");
                noFontsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(noFontsLabel);
            }
        } else {
            JLabel noDirLabel = new JLabel("lib/fonts directory not found");
            noDirLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(noDirLabel);
        }
        
        // Create scrollable container
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add a close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fontFrame.dispose();
            }
        });
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        fontFrame.setContentPane(mainPanel);
        fontFrame.setVisible(true);
    }
    
    private void adjustLayoutForWindowSize() {
        // Only adjust if we have all the required components
        if (headerImage == null || buttonPanel == null) {
            return;
        }
        
        int windowHeight = getHeight();
        int windowWidth = getWidth();
        
        // Update admin button position when window size changes
        int margin = 20;
        int rightOffset = 40; // Increased distance from right edge
        int adminButtonWidth = 120;
        if (adminButton != null) {
            adminButton.setLocation(windowWidth - adminButtonWidth - margin - rightOffset, margin);
        }
        
        // Calculate scale factors based on both dimensions
        double widthScaleFactor = Math.min(1.0, windowWidth / (double)initialWindowWidth);
        double heightScaleFactor = Math.min(1.0, windowHeight / (double)initialWindowHeight);
        
        // Calculate maximum header height to avoid button overlap
        // Use 40% of screen height as max header height on small screens
        double maxHeaderHeightFactor = 0.4;
        if (windowHeight < 700) {
            // Reduce header size more aggressively on smaller screens
            maxHeaderHeightFactor = 0.3;
        }
        if (windowHeight < 500) {
            // Even smaller on very small screens
            maxHeaderHeightFactor = 0.25;
        }
        
        double maxHeightBasedSize = windowHeight * maxHeaderHeightFactor / headerImage.getHeight();
        
        // Use the smaller factor to maintain aspect ratio while ensuring it fits
        double scaleFactor = Math.min(Math.min(widthScaleFactor, heightScaleFactor), maxHeightBasedSize);
        
        // Apply minimum scale to ensure elements are never too small
        scaleFactor = Math.max(0.3, scaleFactor);
        
        int scaledHeaderHeight = (int)(headerImage.getHeight() * scaleFactor);
        
        // Scale button size and font based on window dimensions
        int scaledButtonWidth = (int)(buttonWidth * scaleFactor);
        int scaledButtonHeight = (int)(buttonHeight * scaleFactor);
        
        // Update button sizes
        button1.setPreferredSize(new Dimension(scaledButtonWidth, scaledButtonHeight));
        button2.setPreferredSize(new Dimension(scaledButtonWidth, scaledButtonHeight));
        button3.setPreferredSize(new Dimension(scaledButtonWidth, scaledButtonHeight));
        button4.setPreferredSize(new Dimension(scaledButtonWidth, scaledButtonHeight));
        
        // Scale font size for buttons
        float scaledFontSize = originalButtonFontSize * (float)scaleFactor;
        scaledFontSize = Math.max(16f, scaledFontSize); // Minimum font size of 16pt
        
        adjustButtonFont(button1, scaledFontSize);
        adjustButtonFont(button2, scaledFontSize);
        adjustButtonFont(button3, scaledFontSize);
        adjustButtonFont(button4, scaledFontSize);
        
        // Calculate position for header
        int adjustedHeaderY = (int)(headerY * heightScaleFactor);
        adjustedHeaderY = Math.max(10, adjustedHeaderY); // Ensure minimum visibility
        
        // Calculate dynamic space between header and buttons based on window height
        int minimumHeaderButtonSpace = (int)(baseMinimumHeaderButtonSpace * heightScaleFactor);
        minimumHeaderButtonSpace = Math.max(20, minimumHeaderButtonSpace); // At least 20px
        
        // On very small screens, reduce this space to prioritize button visibility
        if (windowHeight < 550) {
            minimumHeaderButtonSpace = 15;
        }
        
        // Calculate the available space using scaled header height
        int availableHeight = windowHeight - adjustedHeaderY - scaledHeaderHeight - minimumHeaderButtonSpace;
        
        // Get the GridBagLayout constraints for the button panel
        GridBagConstraints constraints = ((GridBagLayout) getContentPane().getLayout()).getConstraints(buttonPanel);
        
        // If available height is too small, adjust the button position
        if (availableHeight < buttonPanel.getPreferredSize().height) {
            // Calculate a suitable inset that ensures buttons are visible
            int topInset = Math.max(10, windowHeight / 10); // At least 10px from top or 10% of window height
            
            // For extremely small windows, position buttons near the top with minimal space for the header
            if (windowHeight < 400) {
                topInset = scaledHeaderHeight + 15; // Just enough space for the header plus a small gap
            } else if (windowHeight < 600) {
                // For small windows, ensure buttons are visible
                topInset = Math.min(topInset, windowHeight / 5);
            }
            
            // Set the Y position to be relative to the window size
            constraints.insets = new Insets(topInset, 0, 0, 0);
            
            System.out.println("Window is small, adjusting button position. Top inset: " + constraints.insets.top);
        } else {
            // Normal layout for large windows
            constraints.insets = new Insets(0, 0, 0, 0);
        }
        
        // Apply the updated constraints
        ((GridBagLayout) getContentPane().getLayout()).setConstraints(buttonPanel, constraints);
        
        // Update gaps between buttons
        int scaledHorizontalGap = Math.max(5, (int)(horizontalGap * scaleFactor));
        int scaledVerticalGap = Math.max(5, (int)(verticalGap * scaleFactor));
        ((GridLayout)buttonPanel.getLayout()).setHgap(scaledHorizontalGap);
        ((GridLayout)buttonPanel.getLayout()).setVgap(scaledVerticalGap);
        
        // For smaller screens, dynamically adjust divider position based on button position
        // This ensures the divider is always visible below the buttons
        int bottomOfButtons = constraints.insets.top + buttonPanel.getPreferredSize().height + 20;
        if (bottomOfButtons > 0 && dynamicDividerPosition) {
            // Calculate dynamic scaled position
            if (windowHeight < 700) {
                // On smaller screens, position the divider directly below the buttons
                dividerY = Math.max(703, bottomOfButtons + 50); // Use at least original position or below buttons
            } else {
                // On larger screens, maintain proportionate spacing
                dividerY = (int)(initialWindowHeight * 0.7); // Approximately 70% down the window
            }
            
            // Adjust the proportional spacing of the divider
            double dividerPositionRatio = dividerY / (double)initialWindowHeight;
            int newDividerY = (int)(windowHeight * dividerPositionRatio);
            newDividerY = Math.min(newDividerY, windowHeight - 200); // Keep at least 200px from bottom for text sections
            
            // Ensure divider is below buttons
            newDividerY = Math.max(newDividerY, bottomOfButtons + 50);
            
            // Update divider and about section position
            dividerY = newDividerY;
            aboutHeadingY = dividerY + 48 + 3; // Update based on new divider position with added 2px
            
            // Also adjust the divider width and X position for smaller screens
            double widthRatio = windowWidth / (double)initialWindowWidth;
            dividerLength = (int)(1093 * widthRatio);
            dividerX = (int)(windowWidth * 0.12); // Approximately 12% from left edge
            
            // Ensure minimum width for readability
            dividerLength = Math.max(dividerLength, 300);
            dividerLength = Math.min(dividerLength, windowWidth - 100); // Maximum width with padding
            
            // Center divider if window is very narrow
            if (windowWidth < 500) {
                dividerX = (windowWidth - dividerLength) / 2;
            }
        }
        
        // Force layout update
        buttonPanel.revalidate();
        
        // Trigger repaint to ensure divider and text sections get updated
        repaint();
    }
    
    private void adjustButtonFont(JButton button, float fontSize) {
        if (interBlack != null) {
            try {
                // Derive the font with new size
                Font buttonFont = interBlack.deriveFont(fontSize);
                
                // Apply letter spacing (-5%)
                Map<TextAttribute, Object> attributes = new HashMap<>();
                attributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
                buttonFont = buttonFont.deriveFont(attributes);
                
                button.setFont(buttonFont);
            } catch (Exception e) {
                // Fallback if any error occurs
                button.setFont(new Font("Dialog", Font.BOLD, (int)fontSize));
            }
        }
    }
    
    /**
     * Helper method to draw justified multi-line text
     * @param g2d Graphics2D context to draw with
     * @param text The text to draw
     * @param x X position to start drawing
     * @param y Y position for the first line
     * @param maxWidth Maximum width before wrapping
     */
    private void drawJustifiedText(Graphics2D g2d, String text, int x, int y, int maxWidth) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();
        
        String[] words = text.split("\\s+");
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        
        // First, break the text into lines
        for (String word : words) {
            String potentialLine = currentLine.length() > 0 ? currentLine + " " + word : word;
            if (fm.stringWidth(potentialLine) < maxWidth) {
                // Add the word to the current line
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                // End current line and start a new one
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    // This single word is too long
                    lines.add(word);
                }
            }
        }
        
        // Add the last line
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        // Now draw each line with justification
        int currentY = y;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] lineWords = line.split("\\s+");
            
            // Don't justify the last line or lines with only one word
            if (i == lines.size() - 1 || lineWords.length <= 1) {
                // Just draw left-aligned
                g2d.drawString(line, x, currentY);
            } else {
                // Calculate the space width and extra space needed for justification
                int textWidth = fm.stringWidth(line.replace(" ", ""));
                int totalSpaceWidth = maxWidth - textWidth;
                int spaces = lineWords.length - 1;
                int spaceWidth = spaces > 0 ? totalSpaceWidth / spaces : 0;
                
                // Draw each word with calculated spacing
                int currentX = x;
                for (int j = 0; j < lineWords.length; j++) {
                    g2d.drawString(lineWords[j], currentX, currentY);
                    if (j < lineWords.length - 1) {
                        currentX += fm.stringWidth(lineWords[j]) + spaceWidth;
                    }
                }
            }
            
            currentY += lineHeight;
        }
    }
    
    /**
     * Set the title for the information section
     * @param title The title text to display
     */
    public void setInfoSectionTitle(String title) {
        this.infoSectionTitle = title;
        repaint(); // Redraw to show the new text
    }
    
    /**
     * Set the content for the information section
     * @param content The content text to display in the info section
     */
    public void setInfoSectionContent(String content) {
        this.infoSectionContent = content;
        repaint(); // Redraw to show the new text
    }
    
    /**
     * Set the text for the About section paragraph
     * @param text The text to display in the about section
     */
    public void setAboutText(String text) {
        this.aboutText = text;
        repaint(); // Redraw to show the new text
    }
    
    /**
     * Set the title for the second information section
     * @param title The title text to display
     */
    public void setInfoSection2Title(String title) {
        this.infoSection2Title = title;
        repaint(); // Redraw to show the new text
    }
    
    /**
     * Set the content for the second information section
     * @param content The content text to display
     */
    public void setInfoSection2Content(String content) {
        this.infoSection2Content = content;
        repaint(); // Redraw to show the new text
    }
    
    /**
     * Set the title for the third information section
     * @param title The title text to display
     */
    public void setInfoSection3Title(String title) {
        this.infoSection3Title = title;
        repaint(); // Redraw to show the new text
    }
    
    /**
     * Set the content for the third information section
     * @param content The content text to display
     */
    public void setInfoSection3Content(String content) {
        this.infoSection3Content = content;
        repaint(); // Redraw to show the new text
    }
    
    public static void main(String[] args) {
        // Run UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LandingPage ui = new LandingPage();
                ui.setVisible(true);
            }
        });
    }

    // Add this new method after createStyledButton method
    /**
     * Creates a styled admin button with slide animation
     */
    private JButton createStyledAdminButton(String text, Color bgColor) {
        class StyledAdminButton extends JButton {
            // Animation parameters
            private boolean isHovered = false;
            private int slideOffset = 0;
            private final int MAX_SLIDE = 100; // Shorter slide for admin button
            private final int ANIMATION_DURATION = 300;
            private Timer animationTimer;
            private String hoverText = "Log in";
            private String buttonText = text; // Store the original button text
            
            // Animation timing variables
            private long animationStartTime = 0;
            private int animationStartOffset = 0;
            private int animationTargetOffset = 0;
            
            // Zoom animation parameters
            private float zoomFactor = 1.0f;
            private final float MAX_ZOOM = 1.08f;
            
            public StyledAdminButton(String text) {
                super(text);
                this.buttonText = text; // Store text to ensure it's used in the paint method
                
                // Initialize animation timer
                animationTimer = new Timer(16, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        long currentTime = System.currentTimeMillis();
                        float progress = Math.min(1.0f, (currentTime - animationStartTime) / (float)ANIMATION_DURATION);
                        
                        // Apply cubic bezier easing curve for smooth motion
                        float easedProgress = bezierEase(progress);
                        
                        // Calculate current position based on progress
                        slideOffset = animationStartOffset + (int)((animationTargetOffset - animationStartOffset) * easedProgress);
                        
                        // Apply zoom animation based on progress
                        if (isHovered) {
                            // Zoom in faster than sliding (complete in first 40% of animation)
                            float zoomProgress = Math.min(1.0f, progress / 0.4f);
                            zoomFactor = 1.0f + (MAX_ZOOM - 1.0f) * bezierEase(zoomProgress);
                        } else {
                            // Zoom out slightly faster than sliding (complete in first 40% of animation)
                            float zoomProgress = Math.min(1.0f, progress / 0.4f);
                            zoomFactor = MAX_ZOOM - (MAX_ZOOM - 1.0f) * bezierEase(zoomProgress);
                        }
                        
                        // Stop the timer when animation completes
                        if (progress >= 1.0f) {
                            slideOffset = animationTargetOffset;
                            zoomFactor = isHovered ? MAX_ZOOM : 1.0f; // Ensure final state is correct
                            animationTimer.stop();
                        }
                        
                        repaint();
                    }
                });
                
                // Add mouse listeners for hover effect
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        startAnimation(true);
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        startAnimation(false);
                    }
                });
            }
            
            /**
             * Applies a cubic bezier curve easing function
             */
            private float bezierEase(float t) {
                // Cubic bezier parameters for a natural motion curve
                if (t <= 0) return 0;
                if (t >= 1) return 1;
                
                // Opening/closing curve
                return cubicBezier(t, 0.33f, 0.0f, 0.67f, 1.0f);
            }
            
            /**
             * Calculate a point on a cubic bezier curve
             */
            private float cubicBezier(float t, float p1x, float p1y, float p2x, float p2y) {
                // Newton-Raphson iteration to solve for t parameter given x
                float cx = 3.0f * p1x;
                float bx = 3.0f * (p2x - p1x) - cx;
                float ax = 1.0f - cx - bx;
                
                float cy = 3.0f * p1y;
                float by = 3.0f * (p2y - p1y) - cy;
                float ay = 1.0f - cy - by;
                
                // Solve for y given t
                return ((ay * t + by) * t + cy) * t;
            }
            
            /**
             * Start an animation with bezier easing
             */
            private void startAnimation(boolean hovering) {
                isHovered = hovering;
                animationStartTime = System.currentTimeMillis();
                animationStartOffset = slideOffset;
                animationTargetOffset = hovering ? MAX_SLIDE : 0;
                
                if (!animationTimer.isRunning()) {
                    animationTimer.start();
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int cornerRadius = 8; // Corner radius for admin button
                
                // Apply zoom transformation
                if (zoomFactor > 1.0f) {
                    // Calculate scale parameters to zoom from center
                    float scaleX = zoomFactor;
                    float scaleY = zoomFactor;
                    
                    // Calculate translation to keep button centered during zoom
                    float transX = width * (1 - scaleX) / 2;
                    float transY = height * (1 - scaleY) / 2;
                    
                    // Apply the transformation
                    g2d.translate(transX, transY);
                    g2d.scale(scaleX, scaleY);
                }
                
                // Paint background with rounded corners - original button background
                g2d.setColor(getBackground());
                
                // When zoomed, use a slightly brighter color to enhance the effect
                if (zoomFactor > 1.01f) {
                    Color bgColor = getBackground();
                    float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
                    float brightnessFactor = Math.min(1.0f, hsb[2] * 1.15f);
                    Color brighterBg = Color.getHSBColor(hsb[0], hsb[1], brightnessFactor);
                    g2d.setColor(brighterBg);
                }
                
                g2d.fill(new RoundRectangle2D.Double(0, 0, width, height, cornerRadius, cornerRadius));
                
                // Draw the sliding effect - main button slides left to reveal content underneath
                if (slideOffset > 0) {
                    // Draw button portion that's sliding away
                    g2d.setColor(getBackground());
                    g2d.setClip(new RoundRectangle2D.Double(-slideOffset, 0, width, height, cornerRadius, cornerRadius));
                    g2d.fillRect(-slideOffset, 0, width, height);
                    g2d.setClip(null);
                    
                    // Draw the revealed area with darker shade of the same color
                    Color darkerBg = getBackground().darker();
                    g2d.setColor(darkerBg);
                    
                    // Create a custom shape that's only rounded on the right side for the revealed area
                    RoundRectangle2D.Double revealedArea = new RoundRectangle2D.Double(
                        0, 0, width, height, cornerRadius, cornerRadius);
                    g2d.fill(revealedArea);
                    
                    // Draw the hover text in the revealed area
                    g2d.setColor(Color.WHITE);
                    
                    // Only draw text once button has slid enough
                    if (slideOffset > MAX_SLIDE/3) {
                        Font loginFont = interMedium;
                        if (loginFont != null) {
                            loginFont = loginFont.deriveFont(14f);
                        } else {
                            loginFont = new Font("SansSerif", Font.PLAIN, 14);
                        }
                        
                        g2d.setFont(loginFont);
                        FontMetrics fm = g2d.getFontMetrics();
                        int textWidth = fm.stringWidth(hoverText);
                        int textX = (width - textWidth) / 2;
                        int textY = (height + fm.getAscent() - fm.getDescent()) / 2;
                        
                        g2d.drawString(hoverText, textX, textY);
                    }
                    
                    // Draw the sliding button on top
                    g2d.setColor(getBackground());
                    g2d.fill(new RoundRectangle2D.Double(-slideOffset, 0, width, height, cornerRadius, cornerRadius));
                    
                    // Only draw main button text if button is not fully slid away
                    if (slideOffset < width) {
                        // Draw button text
                        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        
                        FontMetrics fm = g2d.getFontMetrics();
                        String buttonText = getText();
                        
                        int x = (width - fm.stringWidth(buttonText)) / 2;
                        int y = (height + fm.getAscent() - fm.getDescent()) / 2;
                        
                        // Draw the text with a slight drop shadow for better visibility
                        g2d.setColor(new Color(0, 0, 0, 50)); // Semi-transparent black for shadow
                        g2d.drawString(buttonText, x - slideOffset + 1, y + 1); // Shadow offset by 1px
                        
                        // Draw the actual text
                        g2d.setColor(getForeground());
                        g2d.setFont(getFont());
                        g2d.drawString(buttonText, x - slideOffset, y);
                    }
                } else {
                    // No sliding - just draw text normally
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                    
                    FontMetrics fm = g2d.getFontMetrics();
                    String buttonText = getText();
                    
                    int x = (width - fm.stringWidth(buttonText)) / 2;
                    int y = (height + fm.getAscent() - fm.getDescent()) / 2;
                    
                    g2d.setColor(getForeground());
                    g2d.setFont(getFont());
                    // Draw the text with a slight drop shadow for better visibility
                    g2d.setColor(new Color(0, 0, 0, 50)); // Semi-transparent black for shadow
                    g2d.drawString(buttonText, x + 1, y + 1); // Shadow offset by 1px
                    
                    // Draw the actual text
                    g2d.setColor(getForeground());
                    g2d.drawString(buttonText, x, y);
                    
                    // Debug output if text isn't showing
                    if (buttonText.isEmpty()) {
                        System.out.println("WARNING: Admin button text is empty!");
                        g2d.setColor(Color.RED);
                        g2d.drawString("Admin", x, y); // Force display the text
                    }
                }
                
                g2d.dispose();
            }
        }
        
        // Create button instance
        StyledAdminButton button = new StyledAdminButton(text);
        
        // Enhance button appearance
        button.setForeground(Color.WHITE); // White text for better contrast
        button.setBackground(bgColor);
        button.setBorderPainted(false); // Remove button border
        button.setFocusPainted(false); // Remove focus border
        button.setContentAreaFilled(false); // Required for custom paint
        button.setOpaque(false); // Required for rounded corners to show
        
        // Set a stronger, more visible font
        if (interSemiBold != null) {
            // Use SemiBold for stronger visibility
            Font adminFont = interSemiBold.deriveFont(Font.BOLD, 14f);
            button.setFont(adminFont);
        } else if (interBold != null) {
            // Fallback to Bold
            button.setFont(interBold.deriveFont(14f));
        } else {
            // System font fallback
            button.setFont(new Font("Sans-Serif", Font.BOLD, 14));
        }
        
        // Add action listener to navigate to AdminPage
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Navigate to Admin Page
                Dimension currentSize = getSize();
                dispose();
                AdminPage adminPage = new AdminPage();
                adminPage.setSize(currentSize); // Set the same size as current window
                adminPage.setLocationRelativeTo(null); // Center on screen
                adminPage.setVisible(true);
            }
        });
        
        return button;
    }
} 