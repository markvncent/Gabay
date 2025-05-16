package frontend.comparison;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.Map;

/**
 * Component for displaying social stances comparison between two candidates
 */
public class SocialStancesCompare extends JPanel {
    // Colors
    private Color bgColor = new Color(0xF7, 0xF9, 0xFC); // Light background color
    private Color textColor = new Color(0x47, 0x55, 0x69); // Text color
    private Color borderColor = new Color(0xE5, 0xE7, 0xEB); // Border color
    private Color orangeAccent = new Color(0xF9, 0xB4, 0x47); // Orange accent #F9B447
    
    // Define stance colors as static final to ensure consistency
    private static final Color AGREE_COLOR = new Color(0x10, 0xB9, 0x81); // Green for agree
    private static final Color DISAGREE_COLOR = new Color(0xE9, 0x45, 0x40); // Red for disagree - exact color E94540
    private static final Color NEUTRAL_COLOR = new Color(0x64, 0x74, 0x8B); // Gray for neutral
    private static final Color NO_DATA_COLOR = new Color(0xF0, 0xF0, 0xF0); // Light gray/almost white for no data - F0F0F0
    
    // Debug mode - set to true to print color information
    private static final boolean DEBUG_MODE = true;
    
    // Fonts
    private Font interRegular;
    private Font interMedium;
    private Font interSemiBold;
    
    // Candidate data
    private String leftCandidateName = "";
    private String rightCandidateName = "";
    private BufferedImage leftCandidateImage;
    private BufferedImage rightCandidateImage;
    
    // Content areas
    private JPanel leftContentPanel;
    private JPanel rightContentPanel;
    
    // Profile image dimensions (adjustable)
    private int profileImageWidth = 250;
    private int profileImageHeight = 120;
    
    // Predefined list of social stance topics
    private static final String[] SOCIAL_STANCE_TOPICS = {
        "Legalization of Divorce",
        "Passing the SOGIE Equality Bill",
        "Reinstating the Death Penalty",
        "Lowering the Age of Criminal Responsibility",
        "Federalism",
        "Mandatory ROTC for Senior High Students",
        "Same-Sex Marriage",
        "Anti-Terror Law",
        "Jeepney Modernization Program",
        "Foreign Investment in Land Ownership",
        "Universal Healthcare Funding",
        "Mandatory Sex Education",
        "Minimum Wage Standardization"
    };
    
    /**
     * Creates the social stances comparison component
     * @param interRegular Regular font
     * @param interMedium Medium font
     * @param interSemiBold SemiBold font
     */
    public SocialStancesCompare(Font interRegular, Font interMedium, Font interSemiBold) {
        this.interRegular = interRegular;
        this.interMedium = interMedium;
        this.interSemiBold = interSemiBold;
        
        setLayout(new BorderLayout(20, 0));
        setOpaque(false);
        
        // Create the side-by-side panels
        createComparisonPanels();
        
        // Load placeholder images
        loadPlaceholderImages();
    }
    
    /**
     * Create the side-by-side comparison panels
     */
    private void createComparisonPanels() {
        // Create container for the two panels with some spacing
        JPanel container = new JPanel(new GridLayout(1, 2, 20, 0));
        container.setOpaque(false);
        
        // Create left candidate panel
        leftContentPanel = createCandidatePanel(0); // Left candidate index
        container.add(leftContentPanel);
        
        // Create right candidate panel
        rightContentPanel = createCandidatePanel(1); // Right candidate index
        container.add(rightContentPanel);
        
        // Add to this panel
        add(container, BorderLayout.CENTER);
    }
    
    /**
     * Create an individual candidate panel
     * @param candidateIndex Index (0 = left, 1 = right) to determine which data to show
     */
    private JPanel createCandidatePanel(int candidateIndex) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw subtle border
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        
        panel.setLayout(new BorderLayout(0, 0));
        panel.setOpaque(false);
        
        // Create image panel (left side) with rounded borders
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);
        
        // Create a panel to contain the image with margins
        JPanel imageBorderPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // This panel will be responsible for drawing the rounded rectangle around the image
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background for image
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        imageBorderPanel.setOpaque(false);
        
        // Set the size of the image panel
        imageBorderPanel.setPreferredSize(new Dimension(profileImageWidth, profileImageHeight));
        
        // Add the image border panel to the image panel with padding
        imagePanel.add(imageBorderPanel, BorderLayout.CENTER);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 0));
        
        // Create content panel (right side)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Create a panel for the header with name
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        
        // Add name label
        JLabel nameLabel = new JLabel("Select a candidate");
        nameLabel.setFont(interSemiBold != null ? 
                          interSemiBold.deriveFont(16f) : 
                          new Font("Sans-Serif", Font.BOLD, 16));
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(nameLabel);
        
        // Add tagline
        JLabel tagLine = new JLabel("Positions on social issues:");
        tagLine.setFont(interRegular != null ? 
                          interRegular.deriveFont(12f) : 
                          new Font("Sans-Serif", Font.ITALIC, 12));
        tagLine.setForeground(new Color(0x64, 0x64, 0x64));
        tagLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(tagLine);
        
        // Create a panel for scrollable content
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        scrollContent.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        // Add stances panel to scrollContent
        JPanel stancesPanel = createStancesPanel(candidateIndex);
        scrollContent.add(stancesPanel);
        
        // Create scrollpane for content
        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Apply minimal scrollbar UI
        scrollPane.getVerticalScrollBar().setUI(new MinimalScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        
        // Add the header and scrollpane to the content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to panel
        panel.add(imagePanel, BorderLayout.WEST);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create a panel with social stance positions
     */
    private JPanel createStancesPanel(int candidateIndex) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setName("stances_panel_" + candidateIndex);
        
        // Create a legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        legendPanel.setOpaque(false);
        legendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Support legend item - renamed to Agree for consistency
        JPanel supportLegend = createLegendItem("Agree", AGREE_COLOR);
        // Neutral legend item
        JPanel neutralLegend = createLegendItem("Neutral", NEUTRAL_COLOR);
        // Oppose legend item - renamed to Disagree for consistency
        JPanel opposeLegend = createLegendItem("Disagree", DISAGREE_COLOR);
        // No Data legend item
        JPanel noDataLegend = createLegendItem("No Data", NEUTRAL_COLOR);
        
        legendPanel.add(supportLegend);
        legendPanel.add(neutralLegend);
        legendPanel.add(opposeLegend);
        legendPanel.add(noDataLegend);
        
        panel.add(legendPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Create a container for stance items
        JPanel stancesContainer = new JPanel();
        stancesContainer.setLayout(new BoxLayout(stancesContainer, BoxLayout.Y_AXIS));
        stancesContainer.setOpaque(false);
        stancesContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        stancesContainer.setName("stances_container");
        
        // Add a placeholder message
        JLabel placeholderLabel = new JLabel("Select candidates to view their social stances");
        placeholderLabel.setFont(interRegular != null ? 
                              interRegular.deriveFont(14f) : 
                              new Font("Sans-Serif", Font.ITALIC, 14));
        placeholderLabel.setForeground(textColor);
        placeholderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        stancesContainer.add(placeholderLabel);
        
        panel.add(stancesContainer);
        
        return panel;
    }
    
    /**
     * Create a stance item panel for a social issue
     */
    private JPanel createStanceItem(String issue, String stance) {
        // Force garbage collection to clear any cached colors
        System.gc();
        
        JPanel issuePanel = new JPanel();
        issuePanel.setLayout(new BoxLayout(issuePanel, BoxLayout.Y_AXIS));
        issuePanel.setOpaque(false);
        issuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        issuePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // Increased height to accommodate additional text
        
        // Get position color and text
        Color positionColor;
        String positionText = stance;
        String originalStance = stance; // Store the original stance text
        
        // Normalize stance text for consistent handling
        String normalizedStance = stance.toLowerCase().trim();
        
        // SPECIAL CASE: FORCE ALL DISAGREE to the exact color E94540
        if (normalizedStance.contains("disagree") || normalizedStance.contains("oppose") || 
            normalizedStance.contains("against") || normalizedStance.equals("no")) {
            System.out.println("==== CREATING DISAGREE ITEM FOR: " + issue + " ====");
            positionColor = new Color(0xE9, 0x45, 0x40); // Exact E94540 color
            positionText = "Disagree";
        } else if (normalizedStance.contains("agree") || normalizedStance.contains("support") || 
            normalizedStance.contains("favor") || normalizedStance.equals("yes")) {
            positionColor = AGREE_COLOR;
            positionText = "Agree";
        } else if (normalizedStance.contains("neutral") || normalizedStance.contains("nuetral") || 
                   normalizedStance.contains("undecided") || normalizedStance.contains("partial")) {
            positionColor = NEUTRAL_COLOR;
            positionText = "Neutral";
        } else if (normalizedStance.equals("no data") || normalizedStance.isEmpty()) {
            positionColor = new Color(0xF0, 0xF0, 0xF0); // Light gray/almost white - F0F0F0
            positionText = "No Data"; // This text will be displayed in F0F0F0 color
            originalStance = ""; // No original text to show
            System.out.println("==== CREATING NO DATA ITEM FOR: " + issue + " ====");
        } else {
            // Try to infer based on common patterns
            if (normalizedStance.contains("pro") && !normalizedStance.contains("prohibit")) {
                positionColor = AGREE_COLOR;
                positionText = "Agree";
            } else if (normalizedStance.contains("anti") || normalizedStance.contains("prohibit")) {
                // Make sure we use the red color for disagree
                positionColor = DISAGREE_COLOR;
                positionText = "Disagree";
                
                if (DEBUG_MODE) {
                    System.out.println("DISAGREE COLOR (anti/prohibit): R=" + DISAGREE_COLOR.getRed() + 
                        ", G=" + DISAGREE_COLOR.getGreen() + ", B=" + DISAGREE_COLOR.getBlue());
                }
            } else {
                // Default case
                positionColor = textColor;
                positionText = stance; // Keep original text if we can't categorize it
                originalStance = ""; // No need to repeat text
            }
        }
        
        // Store the final color and text for the anonymous class
        final String finalPositionText = positionText;
        final String finalOriginalStance = originalStance;
        
        // Create the main item row with position indicator and topic
        JPanel mainRow = new JPanel(new BorderLayout(10, 0));
        mainRow.setOpaque(false);
        mainRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        // Position indicator
        JPanel positionIndicator = new JPanel() {
            {
                // Force explicit repaint schedule
                setDoubleBuffered(false);
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw filled circle with position color - use switch for absolutely explicit control
                switch (finalPositionText) {
                    case "Disagree":
                        // Use E94540 color
                        Color disagreeColor = new Color(0xE9, 0x45, 0x40);
                        g2d.setColor(disagreeColor);
                        
                        System.out.println("➡️ DRAWING CIRCLE: " + finalPositionText + 
                            " R=" + disagreeColor.getRed() + 
                            ", G=" + disagreeColor.getGreen() + 
                            ", B=" + disagreeColor.getBlue());
                        break;
                    case "Agree":
                        g2d.setColor(AGREE_COLOR);
                        break;
                    case "Neutral":
                        g2d.setColor(NEUTRAL_COLOR);
                        break;
                    default:
                        // For No Data, draw a horizontal line instead of a circle
                        g2d.setColor(NEUTRAL_COLOR);
                        g2d.setStroke(new BasicStroke(2.0f));
                        g2d.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
                        return; // Skip the default fillOval since we're drawing a line
                }
                
                g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };
        positionIndicator.setPreferredSize(new Dimension(16, 16));
        positionIndicator.setOpaque(false);
        
        // Issue text
        JLabel issueLabel = new JLabel(issue);
        issueLabel.setFont(interMedium != null ? 
                         interMedium.deriveFont(14f) : 
                         new Font("Sans-Serif", Font.BOLD, 14));
        issueLabel.setForeground(textColor);
        
        // Position text - Ensure text color matches the indicator color
        JLabel positionLabel = new JLabel(finalPositionText);
        positionLabel.setFont(interRegular != null ? 
                            interRegular.deriveFont(14f) : 
                            new Font("Sans-Serif", Font.PLAIN, 14));
        
        // Force the color assignment for the position label using switch for explicit control
        switch (finalPositionText) {
            case "Disagree":
                // Use E94540 color
                Color disagreeTextColor = new Color(0xE9, 0x45, 0x40);
                positionLabel.setForeground(disagreeTextColor);
                
                System.out.println("➡️ SETTING LABEL COLOR: " + finalPositionText + 
                    " R=" + disagreeTextColor.getRed() + 
                    ", G=" + disagreeTextColor.getGreen() + 
                    ", B=" + disagreeTextColor.getBlue());
                break;
            case "Agree":
                positionLabel.setForeground(AGREE_COLOR);
                break;
            case "Neutral":
                positionLabel.setForeground(NEUTRAL_COLOR);
                break;
            case "No Data":
                // For No Data, use NEUTRAL_COLOR like the legend
                positionLabel.setForeground(NEUTRAL_COLOR);
                break;
            default:
                positionLabel.setForeground(textColor);
                break;
        }
        
        // Layout the main row panel
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(positionIndicator);
        leftPanel.add(issueLabel);
        
        mainRow.add(leftPanel, BorderLayout.WEST);
        mainRow.add(positionLabel, BorderLayout.EAST);
        
        // Add the main row to the issue panel
        issuePanel.add(mainRow);
        
        // Add original stance text if it's different from the interpreted stance and not empty
        if (!finalOriginalStance.isEmpty() && !finalOriginalStance.equals(finalPositionText)) {
            // Create a panel for the original text with proper indentation
            JPanel originalTextPanel = new JPanel(new BorderLayout());
            originalTextPanel.setOpaque(false);
            originalTextPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Original text label with smaller font and color that matches the stance
            JLabel originalTextLabel = new JLabel("\"" + finalOriginalStance + "\"");
            originalTextLabel.setFont(interRegular != null ? 
                                   interRegular.deriveFont(12f) : 
                                   new Font("Sans-Serif", Font.ITALIC, 12));
            
            // Set the color of the original text to match the stance color, but with some transparency
            Color textColor;
            switch (finalPositionText) {
                case "Disagree":
                    // Use E94540 color with transparency
                    textColor = new Color(0xE9, 0x45, 0x40, 180); // Semi-transparent E94540
                    break;
                case "Agree":
                    textColor = new Color(AGREE_COLOR.getRed(), AGREE_COLOR.getGreen(), 
                                          AGREE_COLOR.getBlue(), 180); // Semi-transparent green
                    break;
                case "Neutral":
                    textColor = new Color(NEUTRAL_COLOR.getRed(), NEUTRAL_COLOR.getGreen(), 
                                          NEUTRAL_COLOR.getBlue(), 180); // Semi-transparent gray
                    break;
                default:
                    textColor = new Color(0x64, 0x74, 0x8B, 180); // Default semi-transparent gray
                    break;
            }
            
            originalTextLabel.setForeground(textColor);
            
            // Create a panel with left indent
            JPanel indentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            indentPanel.setOpaque(false);
            indentPanel.add(Box.createRigidArea(new Dimension(26, 0))); // Indent to align with the text
            indentPanel.add(originalTextLabel);
            
            originalTextPanel.add(indentPanel, BorderLayout.WEST);
            
            // Add the original text panel to the main panel
            issuePanel.add(originalTextPanel);
        }
        
        return issuePanel;
    }
    
    /**
     * Create a legend item with color indicator and label
     */
    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        
        // For disagree, force the exact color
        final Color finalColor;
        if (text.equals("Disagree")) {
            // Use E94540 color
            finalColor = new Color(0xE9, 0x45, 0x40);
            System.out.println("➡️ CREATING LEGEND ITEM: " + text + 
                " R=" + finalColor.getRed() + 
                ", G=" + finalColor.getGreen() + 
                ", B=" + finalColor.getBlue());
        } else if (text.equals("No Data")) {
            // Use NEUTRAL_COLOR for No Data
            finalColor = NEUTRAL_COLOR;
            System.out.println("➡️ CREATING NO DATA LEGEND ITEM using NEUTRAL_COLOR");
        } else {
            finalColor = color;
        }
        
        // Color indicator
        JPanel colorIndicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(finalColor);
                
                // For No Data, draw a horizontal line instead of a circle
                if (text.equals("No Data")) {
                    g2d.setStroke(new BasicStroke(2.0f));
                    g2d.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
                } else {
                    g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                }
            }
        };
        colorIndicator.setPreferredSize(new Dimension(12, 12));
        colorIndicator.setOpaque(false);
        
        // Text label
        JLabel label = new JLabel(text);
        label.setFont(interRegular != null ? 
                     interRegular.deriveFont(12f) : 
                     new Font("Sans-Serif", Font.PLAIN, 12));
        
        // Set the label color to match the indicator for better visibility
        if (text.equals("No Data")) {
            // For No Data legend text, use regular styling like other legend items
            label.setForeground(finalColor);
        } else {
            label.setForeground(finalColor);
        }
        
        panel.add(colorIndicator);
        panel.add(label);
        
        return panel;
    }
    
    /**
     * Load placeholder images
     */
    private void loadPlaceholderImages() {
        // Get default profile image from CandidateDataManager
        leftCandidateImage = CandidateDataManager.getDefaultProfileImage();
        rightCandidateImage = CandidateDataManager.getDefaultProfileImage();
    }
    
    /**
     * Set the profile image dimensions
     * @param width Width of the profile image
     * @param height Height of the profile image
     */
    public void setProfileImageDimensions(int width, int height) {
        this.profileImageWidth = width;
        this.profileImageHeight = height;
        
        // Update the panels with new dimensions
        updateImagePanelSizes(leftContentPanel);
        updateImagePanelSizes(rightContentPanel);
        
        revalidate();
        repaint();
    }
    
    /**
     * Update the size of the image panel in a content panel
     * @param contentPanel The panel containing the image panel to update
     */
    private void updateImagePanelSizes(JPanel contentPanel) {
        if (contentPanel != null) {
            Component[] components = contentPanel.getComponents();
            
            // Find the image panel (should be the first component)
            if (components.length > 0 && components[0] instanceof JPanel) {
                JPanel imagePanel = (JPanel) components[0];
                
                // Find the image border panel within the image panel
                Component[] imagePanelComps = imagePanel.getComponents();
                for (Component comp : imagePanelComps) {
                    if (comp instanceof JPanel) {
                        JPanel imageBorderPanel = (JPanel) comp;
                        imageBorderPanel.setPreferredSize(new Dimension(profileImageWidth, profileImageHeight));
                    }
                }
            }
        }
    }
    
    /**
     * Set the left candidate data
     * @param name Candidate name
     * @param image Candidate image
     */
    public void setLeftCandidate(String name, BufferedImage image) {
        this.leftCandidateName = name;
        if (image != null) {
            this.leftCandidateImage = image;
        }
        updateLeftPanel();
    }
    
    /**
     * Set the right candidate data
     * @param name Candidate name
     * @param image Candidate image
     */
    public void setRightCandidate(String name, BufferedImage image) {
        this.rightCandidateName = name;
        if (image != null) {
            this.rightCandidateImage = image;
        }
        updateRightPanel();
    }
    
    /**
     * Update the left panel content
     */
    private void updateLeftPanel() {
        if (leftContentPanel != null) {
            Component[] components = leftContentPanel.getComponents();
            
            // Update image panel
            if (components.length > 0 && components[0] instanceof JPanel) {
                JPanel imagePanel = (JPanel) components[0];
                
                // Find the image border panel
                Component[] imagePanelComps = imagePanel.getComponents();
                for (Component comp : imagePanelComps) {
                    if (comp instanceof JPanel) {
                        final JPanel imageBorderPanel = (JPanel) comp;
                        imageBorderPanel.removeAll();
                        
                        // Create image draw panel
                        if (leftCandidateImage != null) {
                            JPanel imageDrawPanel = new JPanel() {
                                @Override
                                protected void paintComponent(Graphics g) {
                                    super.paintComponent(g);
                                    if (leftCandidateImage != null) {
                                        Graphics2D g2d = (Graphics2D) g;
                                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                        
                                        // Create a rounded rectangle clip
                                        Shape roundRect = new java.awt.geom.RoundRectangle2D.Float(
                                            0, 0, getWidth(), getHeight(), 8, 8);
                                        g2d.setClip(roundRect);
                                        
                                        // Scale image to fill the box while maintaining aspect ratio
                                        float imageRatio = (float) leftCandidateImage.getWidth() / leftCandidateImage.getHeight();
                                        float boxRatio = (float) getWidth() / getHeight();
                                        
                                        int targetWidth, targetHeight;
                                        
                                        if (imageRatio > boxRatio) {
                                            // Image is wider than the box (relative to height)
                                            // Scale based on height, width will overflow
                                            targetHeight = getHeight();
                                            targetWidth = Math.round(targetHeight * imageRatio);
                                        } else {
                                            // Image is taller than the box (relative to width)
                                            // Scale based on width, height will overflow
                                            targetWidth = getWidth();
                                            targetHeight = Math.round(targetWidth / imageRatio);
                                        }
                                        
                                        // Center the image (may be partially off-screen due to filling)
                                        int x = (getWidth() - targetWidth) / 2;
                                        int y = (getHeight() - targetHeight) / 2;
                                        
                                        // Draw the image
                                        g2d.drawImage(leftCandidateImage, x, y, targetWidth, targetHeight, this);
                                    }
                                }
                            };
                            imageDrawPanel.setOpaque(false);
                            imageBorderPanel.add(imageDrawPanel, BorderLayout.CENTER);
                        }
                    }
                }
                
                imagePanel.revalidate();
                imagePanel.repaint();
            }
            
            // Update content panel - name label and social stances
            if (components.length > 1 && components[1] instanceof JPanel) {
                JPanel contentPanel = (JPanel) components[1];
                
                // Find the header panel with name label
                if (contentPanel.getLayout() instanceof BorderLayout) {
                    Component headerComp = ((BorderLayout) contentPanel.getLayout()).getLayoutComponent(BorderLayout.NORTH);
                    if (headerComp instanceof JPanel) {
                        JPanel headerPanel = (JPanel) headerComp;
                        if (headerPanel.getComponentCount() > 0 && headerPanel.getComponent(0) instanceof JLabel) {
                            JLabel nameLabel = (JLabel) headerPanel.getComponent(0);
                            nameLabel.setText(leftCandidateName.isEmpty() ? "Select a candidate" : leftCandidateName);
                        }
                    }
                    
                    // Find the scroll pane
                    Component centerComp = ((BorderLayout) contentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    if (centerComp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) centerComp;
                        Component viewComp = scrollPane.getViewport().getView();
                        if (viewComp instanceof JPanel) {
                            JPanel scrollContent = (JPanel) viewComp;
                            
                            // Update social stances if a candidate is selected
                            if (!leftCandidateName.isEmpty()) {
                                updateSocialStances(scrollContent, leftCandidateName);
                            }
                        }
                    }
                }
                
                contentPanel.revalidate();
                contentPanel.repaint();
            }
            
            leftContentPanel.revalidate();
            leftContentPanel.repaint();
        }
    }
    
    /**
     * Update the right panel content
     */
    private void updateRightPanel() {
        if (rightContentPanel != null) {
            Component[] components = rightContentPanel.getComponents();
            
            // Update image panel
            if (components.length > 0 && components[0] instanceof JPanel) {
                JPanel imagePanel = (JPanel) components[0];
                
                // Find the image border panel
                Component[] imagePanelComps = imagePanel.getComponents();
                for (Component comp : imagePanelComps) {
                    if (comp instanceof JPanel) {
                        final JPanel imageBorderPanel = (JPanel) comp;
                        imageBorderPanel.removeAll();
                        
                        // Create image draw panel
                        if (rightCandidateImage != null) {
                            JPanel imageDrawPanel = new JPanel() {
                                @Override
                                protected void paintComponent(Graphics g) {
                                    super.paintComponent(g);
                                    if (rightCandidateImage != null) {
                                        Graphics2D g2d = (Graphics2D) g;
                                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                        
                                        // Create a rounded rectangle clip
                                        Shape roundRect = new java.awt.geom.RoundRectangle2D.Float(
                                            0, 0, getWidth(), getHeight(), 8, 8);
                                        g2d.setClip(roundRect);
                                        
                                        // Scale image to fill the box while maintaining aspect ratio
                                        float imageRatio = (float) rightCandidateImage.getWidth() / rightCandidateImage.getHeight();
                                        float boxRatio = (float) getWidth() / getHeight();
                                        
                                        int targetWidth, targetHeight;
                                        
                                        if (imageRatio > boxRatio) {
                                            // Image is wider than the box (relative to height)
                                            // Scale based on height, width will overflow
                                            targetHeight = getHeight();
                                            targetWidth = Math.round(targetHeight * imageRatio);
                                        } else {
                                            // Image is taller than the box (relative to width)
                                            // Scale based on width, height will overflow
                                            targetWidth = getWidth();
                                            targetHeight = Math.round(targetWidth / imageRatio);
                                        }
                                        
                                        // Center the image (may be partially off-screen due to filling)
                                        int x = (getWidth() - targetWidth) / 2;
                                        int y = (getHeight() - targetHeight) / 2;
                                        
                                        // Draw the image
                                        g2d.drawImage(rightCandidateImage, x, y, targetWidth, targetHeight, this);
                                    }
                                }
                            };
                            imageDrawPanel.setOpaque(false);
                            imageBorderPanel.add(imageDrawPanel, BorderLayout.CENTER);
                        }
                    }
                }
                
                imagePanel.revalidate();
                imagePanel.repaint();
            }
            
            // Update content panel - name label and social stances
            if (components.length > 1 && components[1] instanceof JPanel) {
                JPanel contentPanel = (JPanel) components[1];
                
                // Find the header panel with name label
                if (contentPanel.getLayout() instanceof BorderLayout) {
                    Component headerComp = ((BorderLayout) contentPanel.getLayout()).getLayoutComponent(BorderLayout.NORTH);
                    if (headerComp instanceof JPanel) {
                        JPanel headerPanel = (JPanel) headerComp;
                        if (headerPanel.getComponentCount() > 0 && headerPanel.getComponent(0) instanceof JLabel) {
                            JLabel nameLabel = (JLabel) headerPanel.getComponent(0);
                            nameLabel.setText(rightCandidateName.isEmpty() ? "Select a candidate" : rightCandidateName);
                        }
                    }
                    
                    // Find the scroll pane
                    Component centerComp = ((BorderLayout) contentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    if (centerComp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) centerComp;
                        Component viewComp = scrollPane.getViewport().getView();
                        if (viewComp instanceof JPanel) {
                            JPanel scrollContent = (JPanel) viewComp;
                            
                            // Update social stances if a candidate is selected
                            if (!rightCandidateName.isEmpty()) {
                                updateSocialStances(scrollContent, rightCandidateName);
                            }
                        }
                    }
                }
                
                contentPanel.revalidate();
                contentPanel.repaint();
            }
            
            rightContentPanel.revalidate();
            rightContentPanel.repaint();
        }
    }
    
    /**
     * Update social stances for a candidate
     * @param contentPanel The content panel containing social stance components
     * @param candidateName The name of the candidate
     */
    private void updateSocialStances(JPanel contentPanel, String candidateName) {
        // Force UI refresh
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Find the stances panel within the scroll content
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JPanel && ((JPanel) comp).getName() != null 
                && ((JPanel) comp).getName().startsWith("stances_panel_")) {
                JPanel stancesPanel = (JPanel) comp;
                
                // Find the container for stance items
                for (Component stanceComp : stancesPanel.getComponents()) {
                    if (stanceComp instanceof JPanel && 
                        "stances_container".equals(((JPanel) stanceComp).getName())) {
                        
                        JPanel stancesContainer = (JPanel) stanceComp;
                        stancesContainer.removeAll();
                        
                        // Debug output 
                        System.out.println("\n======== Loading stances for " + candidateName + " ========");
                        
                        // If no candidate is selected, show a message
                        if (candidateName.isEmpty()) {
                            JLabel noDataLabel = new JLabel("Select a candidate to view their social stances");
                            noDataLabel.setFont(interRegular != null ? 
                                             interRegular.deriveFont(14f) : 
                                             new Font("Sans-Serif", Font.ITALIC, 14));
                            noDataLabel.setForeground(textColor);
                            noDataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                            stancesContainer.add(noDataLabel);
                        } else {
                            Map<String, String> candidateData = CandidateDataManager.getCandidateByName(candidateName);
                            if (candidateData == null) {
                                JLabel noDataLabel = new JLabel("No data found for this candidate");
                                noDataLabel.setForeground(textColor);
                                stancesContainer.add(noDataLabel);
                            } else {
                                boolean hasAnyStance = false;
                                
                                // Add a panel for each predefined stance topic
                                for (String topic : SOCIAL_STANCE_TOPICS) {
                                    String stance = "No Data";
                                    
                                    // Method 1: Try exact match with the full key
                                    String exactKey = "Social Stance: " + topic;
                                    if (candidateData.containsKey(exactKey)) {
                                        stance = candidateData.get(exactKey).trim();
                                        hasAnyStance = true;
                                        System.out.println("Found stance for " + topic + ": " + stance);
                                    }
                                    // Method 2: Try partial matches by checking each key that starts with Social Stance:
                                    else {
                                        for (Map.Entry<String, String> entry : candidateData.entrySet()) {
                                            String key = entry.getKey();
                                            
                                            // Check if it's a social stance key that contains this topic
                                            if (key.startsWith("Social Stance:") && key.substring("Social Stance:".length()).trim().equals(topic)) {
                                                stance = entry.getValue().trim();
                                                hasAnyStance = true;
                                                System.out.println("Found stance for " + topic + ": " + stance);
                                                break;
                                            }
                                            // Check if it contains the topic and has the stance embedded in the key
                                            else if (key.startsWith("Social Stance:") && key.contains(topic)) {
                                                // Parse stance from the key if it contains " - Stance"
                                                if (key.contains(" - ")) {
                                                    stance = key.substring(key.lastIndexOf(" - ") + 3).trim();
                                                    hasAnyStance = true;
                                                    System.out.println("Found stance for " + topic + ": " + stance + " (parsed from key)");
                                                } else {
                                                    // Otherwise get stance from the value
                                                    stance = entry.getValue().trim();
                                                    hasAnyStance = true;
                                                    System.out.println("Found stance for " + topic + ": " + stance + " (from value)");
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    
                                    // Create panel for this stance with hard-coded colors to ensure they display correctly
                                    JPanel stancePanel = createStanceItem(topic, stance);
                                    stancesContainer.add(stancePanel);
                                    stancesContainer.add(Box.createRigidArea(new Dimension(0, 10)));
                                }
                                
                                // If no stances were found, show a message
                                if (!hasAnyStance) {
                                    JLabel noStancesLabel = new JLabel("No stance data found for this candidate");
                                    noStancesLabel.setFont(interRegular != null ? 
                                                      interRegular.deriveFont(14f) : 
                                                      new Font("Sans-Serif", Font.ITALIC, 14));
                                    noStancesLabel.setForeground(textColor);
                                    noStancesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                                    stancesContainer.add(noStancesLabel);
                                }
                            }
                        }
                        
                        stancesContainer.revalidate();
                        stancesContainer.repaint();
                        
                        // Force a full redraw of the UI
                        SwingUtilities.updateComponentTreeUI(stancesContainer);
                        
                        break;
                    }
                }
                break;
            }
        }
    }
} 