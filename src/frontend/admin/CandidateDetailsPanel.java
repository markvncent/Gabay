package frontend.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A simplified rectangle component for the admin panel.
 * This component is a placeholder for candidate details, positioned beside the directory panel.
 */
public class CandidateDetailsPanel extends JPanel {
    // Fixed dimensions
    private final int PANEL_WIDTH = 655;
    private final int PANEL_HEIGHT = 581;
    
    // Fixed position relative to starting window
    private final int PANEL_X = -2058; // Original position as requested
    private final int PANEL_Y = 270;
    
    // For testing purposes, set this to true to make the panel visible on screen
    private final boolean TEST_MODE = true;
    
    // Test mode positions (when visible on screen)
    private final int TEST_X = 615; // Positioned to the right of the directory panel
    private final int TEST_Y = 180; // Shifted 20px upward, aligned with directory panel
    
    // Corner radius for rounded corners
    private final int CORNER_RADIUS = 10;
    
    // Shadow properties
    private final int SHADOW_SIZE = 5;
    private final int SHADOW_OFFSET_X = 1;
    private final int SHADOW_OFFSET_Y = 2;
    private final float SHADOW_OPACITY = 0.1f;
    private final Color shadowColor = new Color(0, 0, 0);
    private final float BORDER_WIDTH = 1.0f;
    
    // Color
    private final Color panelColor = Color.WHITE;
    private final Color headerTextColor = new Color(0x47, 0x55, 0x69); // #475569
    private final Color dividerColor = new Color(0xE2, 0xE8, 0xF0); // #E2E8F0 light gray for divider
    private final Color searchBorderColor = new Color(0xCB, 0xD5, 0xE1); // #CBD5E1
    private final Color labelColor = new Color(0x64, 0x74, 0x8B); // #64748B slate gray for labels
    private final Color filterBlue = new Color(0x2B, 0x37, 0x80); // #2B3780 - blue for dropdown
    
    // Font
    private Font interBlack;
    private Font interRegular;
    private Font interSemiBold;
    private Font interMedium;
    
    // UI Components
    private JTextField nameField;
    private JTextField ageField;
    private JTextField partyField;
    private JTextField yearsField;
    private JTextField sloganField;
    private JTextField platformsField;
    private JTextField supportedField;
    private JTextField lawsField;
    private JTextField opposedField;
    private RegionDropdown regionDropdown;
    
    // Pagination components
    private JPanel pageButtonsPanel;
    private JButton page1Button;
    private JButton page2Button;
    private int currentPage = 1; // Default to first page
    private final int TOTAL_PAGES = 2;
    
    // Track if we're editing an existing candidate or creating a new one
    private boolean isEditMode = false;
    private int editCandidateIndex = -1;
    
    // References to fields should be saved in the createPage1Panel() method:
    private String selectedRegion = "Select Region";
    private String selectedPosition = "Select Position";
    private SocialIssuesPanel socialIssuesPanel;
    
    // Position dropdown reference
    private PositionsDropdown positionsDropdown;
    
    /**
     * Creates a new CandidateDetailsPanel as a simple rectangle
     */
    public CandidateDetailsPanel() {
        // Make the panel transparent to only show our custom painting
        setOpaque(false);
        
        // Set preferred size to match the specified dimensions
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        
        // Set bounds based on test mode
        if (TEST_MODE) {
            // Use test position for visibility
            setBounds(TEST_X, TEST_Y, PANEL_WIDTH, PANEL_HEIGHT);
        } else {
            // Use original specified position
            setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
        }
        
        // Load fonts
        loadFonts();
        
        // Use null layout for absolute positioning
        setLayout(null);
        
        // Create text fields
        createTextFields();
    }
    
    /**
     * Load required fonts
     */
    private void loadFonts() {
        try {
            // Load Inter fonts
            File interBlackFile = new File("lib/fonts/Inter_18pt-Black.ttf");
            File interRegularFile = new File("lib/fonts/Inter_18pt-Regular.ttf");
            File interSemiBoldFile = new File("lib/fonts/Inter_18pt-SemiBold.ttf");
            File interMediumFile = new File("lib/fonts/Inter_18pt-Medium.ttf");
            
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            
            if (interBlackFile.exists()) {
                interBlack = Font.createFont(Font.TRUETYPE_FONT, interBlackFile);
                ge.registerFont(interBlack);
            } else {
                interBlack = new Font("Sans-Serif", Font.BOLD, 30);
            }
            
            if (interRegularFile.exists()) {
                interRegular = Font.createFont(Font.TRUETYPE_FONT, interRegularFile);
                ge.registerFont(interRegular);
            } else {
                interRegular = new Font("Sans-Serif", Font.PLAIN, 13);
            }
            
            if (interSemiBoldFile.exists()) {
                interSemiBold = Font.createFont(Font.TRUETYPE_FONT, interSemiBoldFile);
                ge.registerFont(interSemiBold);
            } else {
                interSemiBold = new Font("Sans-Serif", Font.BOLD, 13);
            }
            
            if (interMediumFile.exists()) {
                interMedium = Font.createFont(Font.TRUETYPE_FONT, interMediumFile);
                ge.registerFont(interMedium);
            } else {
                interMedium = new Font("Sans-Serif", Font.PLAIN, 13);
            }
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            interBlack = new Font("Sans-Serif", Font.BOLD, 30);
            interRegular = new Font("Sans-Serif", Font.PLAIN, 13);
            interSemiBold = new Font("Sans-Serif", Font.BOLD, 13);
            interMedium = new Font("Sans-Serif", Font.PLAIN, 13);
        }
    }
    
    /**
     * Create text fields for name and age
     */
    private void createTextFields() {
        // Calculate positions and sizes
        int margin = 20;
        int fieldHeight = 35;
        int labelHeight = 20;
        int totalWidth = PANEL_WIDTH - (2 * margin);
        int dividerY = 60; // Position for divider line below header
        int fieldSpacing = 15; // Reduced vertical spacing between field groups (was 20)
        
        // Create a two-column layout with image on left, fields on right for the first section
        int imageBoxWidth = 120; // Width for the image preview box
        int imageBoxHeight = fieldHeight * 2 + labelHeight + fieldSpacing + 25; // Height to encompass name and age fields
        int fieldsWidth = totalWidth - imageBoxWidth - 10; // Width for fields minus spacing
        int fieldsX = margin + imageBoxWidth + 10; // X position for fields after image box
        
        // Calculate positions for side-by-side fields for the rows after first section
        int halfWidth = (totalWidth - 10) / 2; // Half width minus 5px gap on each side
        int rightColumnX = margin + halfWidth + 10; // Position after left field with 10px gap
        
        // Current Y position tracker
        int currentY = dividerY + 20; // Reduced spacing from divider (was 25)
        
        // Create page 1 content panel (basic details)
        JPanel page1Panel = createPage1Panel(margin, currentY, fieldHeight, labelHeight, 
                                           imageBoxWidth, imageBoxHeight, fieldsWidth, 
                                           fieldsX, halfWidth, rightColumnX, fieldSpacing, dividerY);
        add(page1Panel);
        
        // Create page 2 content panel (social stance selections)
        JPanel page2Panel = createPage2Panel(margin, currentY, fieldHeight, labelHeight, 
                                           totalWidth, halfWidth, rightColumnX, fieldSpacing, dividerY);
        add(page2Panel);
        
        // Show page 1 by default, hide page 2
        page1Panel.setVisible(true);
        page2Panel.setVisible(false);
        
        // Create save button aligned to the right
        int buttonWidth = 120;
        currentY = PANEL_HEIGHT - margin - fieldHeight;
        JButton saveButton = createStyledButton("Save", new Color(0x2B, 0x37, 0x80));
        saveButton.setBounds(PANEL_WIDTH - margin - buttonWidth, currentY, buttonWidth, fieldHeight);
        saveButton.addActionListener(e -> saveCandidate());
        add(saveButton);
        
        // Create page buttons panel
        createPageButtons(margin, currentY, fieldHeight);
    }
    
    /**
     * Creates page 1 panel with basic candidate details
     */
    private JPanel createPage1Panel(int margin, int startY, int fieldHeight, int labelHeight,
                                  int imageBoxWidth, int imageBoxHeight, int fieldsWidth, 
                                  int fieldsX, int halfWidth, int rightColumnX, 
                                  int fieldSpacing, int dividerY) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setBounds(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        panel.setName("page1Panel");
        
        int currentY = startY;
        
        // Create the image preview box first (positioned on the left)
        JPanel imagePreviewBox = createImagePreviewBox(imageBoxWidth, imageBoxHeight);
        imagePreviewBox.setBounds(margin, currentY, imageBoxWidth, imageBoxHeight);
        panel.add(imagePreviewBox);
        
        // Create name label
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(fieldsX, currentY, fieldsWidth, labelHeight);
        nameLabel.setForeground(new Color(0x47, 0x55, 0x69)); // #475569
        nameLabel.setFont(interSemiBold != null ? 
                        interSemiBold.deriveFont(15f) : 
                        new Font("Sans-Serif", Font.BOLD, 15));
        panel.add(nameLabel);
        
        currentY += labelHeight + 3; // Reduced spacing (was 5)
        
        // Create name text field with styling
        nameField = createStyledTextField("Enter candidate name");
        nameField.setBounds(fieldsX, currentY, fieldsWidth, fieldHeight);
        panel.add(nameField);
        
        currentY += fieldHeight + fieldSpacing;
        
        // Create age label
        JLabel ageLabel = new JLabel("Age");
        ageLabel.setBounds(fieldsX, currentY, fieldsWidth / 2 - 5, labelHeight);
        ageLabel.setForeground(new Color(0x47, 0x55, 0x69)); // #475569
        ageLabel.setFont(interSemiBold != null ? 
                       interSemiBold.deriveFont(15f) : 
                       new Font("Sans-Serif", Font.BOLD, 15));
        panel.add(ageLabel);
        
        // Create region label
        JLabel regionLabel = new JLabel("Region");
        regionLabel.setBounds(fieldsX + fieldsWidth / 2 + 5, currentY, fieldsWidth / 2 - 5, labelHeight);
        regionLabel.setForeground(new Color(0x47, 0x55, 0x69)); // #475569
        regionLabel.setFont(interSemiBold != null ? 
                          interSemiBold.deriveFont(15f) : 
                          new Font("Sans-Serif", Font.BOLD, 15));
        panel.add(regionLabel);
        
        currentY += labelHeight + 3; // Reduced spacing (was 5)
        
        // Create age text field with styling
        ageField = createStyledTextField("Enter age");
        ageField.setBounds(fieldsX, currentY, fieldsWidth / 2 - 5, fieldHeight);
        panel.add(ageField);
        
        // Create region dropdown
        regionDropdown = new RegionDropdown(this, interMedium, interRegular, this::handleRegionSelection);
        JPanel regionRectangle = regionDropdown.getRegionRectangle();
        regionRectangle.setBounds(fieldsX + fieldsWidth / 2 + 5, currentY, fieldsWidth / 2 - 5, fieldHeight);
        panel.add(regionRectangle);
        
        // Advance Y position past the image box and fields
        currentY = dividerY + 20 + imageBoxHeight + fieldSpacing;
        
        // ===== PARTY AFFILIATION AND YEARS OF EXPERIENCE SECTION =====
        
        // Create party affiliation label
        JLabel partyLabel = new JLabel("Party Affiliation");
        partyLabel.setBounds(margin, currentY, halfWidth, labelHeight);
        partyLabel.setForeground(new Color(0x47, 0x55, 0x69)); // #475569
        partyLabel.setFont(interSemiBold != null ? 
                          interSemiBold.deriveFont(15f) : 
                          new Font("Sans-Serif", Font.BOLD, 15));
        panel.add(partyLabel);
        
        // Create years of experience label
        JLabel yearsLabel = new JLabel("Years of Experience");
        yearsLabel.setBounds(rightColumnX, currentY, halfWidth, labelHeight);
        yearsLabel.setForeground(new Color(0x47, 0x55, 0x69)); // #475569
        yearsLabel.setFont(interSemiBold != null ? 
                          interSemiBold.deriveFont(15f) : 
                          new Font("Sans-Serif", Font.BOLD, 15));
        panel.add(yearsLabel);
        
        currentY += labelHeight + 3; // Reduced spacing (was 5)
        
        // Create party affiliation text field
        partyField = createStyledTextField("Enter party affiliation");
        partyField.setBounds(margin, currentY, halfWidth, fieldHeight);
        panel.add(partyField);
        
        // Create years of experience text field
        yearsField = createStyledTextField("Enter years of experience");
        yearsField.setBounds(rightColumnX, currentY, halfWidth, fieldHeight);
        panel.add(yearsField);
        
        currentY += fieldHeight + fieldSpacing;
        
        // ===== CAMPAIGN SLOGAN AND POSITIONS SECTION =====
        
        // Create campaign slogan label
        JLabel sloganLabel = new JLabel("Campaign Slogan");
        sloganLabel.setBounds(margin, currentY, halfWidth, labelHeight);
        sloganLabel.setForeground(new Color(0x47, 0x55, 0x69)); // #475569
        sloganLabel.setFont(interSemiBold != null ? 
                           interSemiBold.deriveFont(15f) : 
                           new Font("Sans-Serif", Font.BOLD, 15));
        panel.add(sloganLabel);
        
        // Create positions label
        JLabel positionsLabel = new JLabel("Position");
        positionsLabel.setBounds(rightColumnX, currentY, halfWidth, labelHeight);
        positionsLabel.setForeground(new Color(0x47, 0x55, 0x69)); // #475569
        positionsLabel.setFont(interSemiBold != null ? 
                              interSemiBold.deriveFont(15f) : 
                              new Font("Sans-Serif", Font.BOLD, 15));
        panel.add(positionsLabel);
        
        currentY += labelHeight + 3; // Reduced spacing (was 5)
        
        // Create campaign slogan text field
        sloganField = createStyledTextField("Enter campaign slogan");
        sloganField.setBounds(margin, currentY, halfWidth, fieldHeight);
        panel.add(sloganField);
        
        // Create positions dropdown
        positionsDropdown = new PositionsDropdown(this, interMedium, interRegular, this::handlePositionSelection);
        JPanel positionsRectangle = positionsDropdown.getPositionsRectangle();
        positionsRectangle.setBounds(rightColumnX, currentY, halfWidth, fieldHeight);
        panel.add(positionsRectangle);
        
        currentY += fieldHeight + fieldSpacing;
        
        // ===== PLATFORMS AND SUPPORTED ISSUES SECTION =====
        
        // Create platforms label with smaller comma separated text
        JPanel platformsLabelPanel = createSplitLabel("Platforms", "comma separated", halfWidth);
        platformsLabelPanel.setBounds(margin, currentY, halfWidth, labelHeight);
        panel.add(platformsLabelPanel);
        
        // Create supported issues label with smaller comma separated text
        JPanel supportedLabelPanel = createSplitLabel("Supported Issues", "comma separated", halfWidth);
        supportedLabelPanel.setBounds(rightColumnX, currentY, halfWidth, labelHeight);
        panel.add(supportedLabelPanel);
        
        currentY += labelHeight + 3; // Reduced spacing (was 5)
        
        // Create platforms text field
        platformsField = createStyledTextField("Enter platforms");
        platformsField.setBounds(margin, currentY, halfWidth, fieldHeight);
        panel.add(platformsField);
        
        // Create supported issues text field
        supportedField = createStyledTextField("Enter supported issues");
        supportedField.setBounds(rightColumnX, currentY, halfWidth, fieldHeight);
        panel.add(supportedField);
        
        currentY += fieldHeight + fieldSpacing;
        
        // ===== NOTABLE LAWS AND OPPOSED ISSUES SECTION =====
        
        // Create notable laws label with smaller comma separated text
        JPanel lawsLabelPanel = createSplitLabel("Notable Laws", "comma separated", halfWidth);
        lawsLabelPanel.setBounds(margin, currentY, halfWidth, labelHeight);
        panel.add(lawsLabelPanel);
        
        // Create opposed issues label with smaller comma separated text
        JPanel opposedLabelPanel = createSplitLabel("Opposed Issues", "comma separated", halfWidth);
        opposedLabelPanel.setBounds(rightColumnX, currentY, halfWidth, labelHeight);
        panel.add(opposedLabelPanel);
        
        currentY += labelHeight + 3; // Reduced spacing (was 5)
        
        // Create notable laws text field
        lawsField = createStyledTextField("Enter notable laws");
        lawsField.setBounds(margin, currentY, halfWidth, fieldHeight);
        panel.add(lawsField);
        
        // Create opposed issues text field
        opposedField = createStyledTextField("Enter opposed issues");
        opposedField.setBounds(rightColumnX, currentY, halfWidth, fieldHeight);
        panel.add(opposedField);
        
        return panel;
    }
    
    /**
     * Creates page 2 panel with social stance selections
     */
    private JPanel createPage2Panel(int margin, int startY, int fieldHeight, int labelHeight,
                                 int totalWidth, int halfWidth, int rightColumnX, 
                                 int fieldSpacing, int dividerY) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setBounds(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        panel.setName("page2Panel");
        
        // Start Y position after the header (same as page 1)
        int currentY = startY;
        
        // Create a heading for page 2
        JLabel page2HeaderLabel = new JLabel("Social Stance Positions");
        page2HeaderLabel.setBounds(margin, currentY, totalWidth, 30);
        page2HeaderLabel.setForeground(new Color(0x47, 0x55, 0x69)); // #475569
        page2HeaderLabel.setFont(interSemiBold != null ? 
                                interSemiBold.deriveFont(18f) : 
                                new Font("Sans-Serif", Font.BOLD, 18));
        panel.add(page2HeaderLabel);
        
        currentY += 35;
        
        // Create subheader for page 2
        JLabel page2SubheaderLabel = new JLabel("Select where this candidate stands on key social issues");
        page2SubheaderLabel.setBounds(margin, currentY, totalWidth, 20);
        page2SubheaderLabel.setForeground(new Color(0x8D, 0x8D, 0x8D)); // Gray color
        page2SubheaderLabel.setFont(interRegular != null ? 
                                   interRegular.deriveFont(14f) : 
                                   new Font("Sans-Serif", Font.PLAIN, 14));
        panel.add(page2SubheaderLabel);
        
        currentY += 30;
        
        // Create column headers
        JLabel issueHeader = new JLabel("Social Issue");
        issueHeader.setBounds(margin, currentY, totalWidth / 2, labelHeight);
        issueHeader.setForeground(new Color(0x47, 0x55, 0x69)); // #475569
        issueHeader.setFont(interSemiBold != null ? 
                           interSemiBold.deriveFont(15f) : 
                           new Font("Sans-Serif", Font.BOLD, 15));
        panel.add(issueHeader);
        
        JLabel stanceHeader = new JLabel("Candidate Stance");
        stanceHeader.setBounds(margin + totalWidth / 2, currentY, totalWidth / 2, labelHeight);
        stanceHeader.setForeground(new Color(0x47, 0x55, 0x69)); // #475569
        stanceHeader.setFont(interSemiBold != null ? 
                            interSemiBold.deriveFont(15f) : 
                            new Font("Sans-Serif", Font.BOLD, 15));
        panel.add(stanceHeader);
        
        currentY += labelHeight + 10;
        
        // Create divider line
        JPanel dividerLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0xE2, 0xE8, 0xF0)); // Light gray for divider
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine(0, 0, getWidth(), 0);
            }
        };
        dividerLine.setBounds(margin, currentY, totalWidth - 2 * margin, 1);
        dividerLine.setOpaque(false);
        panel.add(dividerLine);
        
        currentY += 10;
        
        // Create the social issues panel - Now using the external SocialIssuesPanel class
        socialIssuesPanel = new SocialIssuesPanel(
            totalWidth - 2 * margin,
            interRegular,
            interMedium
        );
        
        // Calculate the height of the scroll pane (from current position to bottom of panel with margin)
        int scrollPaneY = currentY;
        int scrollPaneHeight = PANEL_HEIGHT - scrollPaneY - 80; // Leave space at bottom for buttons
        
        // Set bounds for the social issues panel
        socialIssuesPanel.setBounds(margin, scrollPaneY, totalWidth - 2 * margin, scrollPaneHeight);
        panel.add(socialIssuesPanel);
        
        return panel;
    }
    
    /**
     * Creates page buttons panel for navigation between pages
     */
    private void createPageButtons(int margin, int currentY, int fieldHeight) {
        // Create panel to hold the page buttons
        pageButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pageButtonsPanel.setOpaque(false);
        
        // Create page 1 button (selected by default)
        page1Button = createPageButton("1", true);
        page1Button.addActionListener(e -> switchToPage(1));
        
        // Create page 2 button (not selected by default)
        page2Button = createPageButton("2", false);
        page2Button.addActionListener(e -> switchToPage(2));
        
        // Add buttons to the panel
        pageButtonsPanel.add(page1Button);
        pageButtonsPanel.add(page2Button);
        
        // Position the panel opposite to the save button
        int panelWidth = 80;
        int panelHeight = fieldHeight;
        pageButtonsPanel.setBounds(margin, currentY, panelWidth, panelHeight);
        
        add(pageButtonsPanel);
    }
    
    /**
     * Creates a styled page button with the specified text and selection state
     */
    private JButton createPageButton(String text, boolean isSelected) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background
                if (isSelected()) {
                    g2d.setColor(new Color(0x2B, 0x37, 0x80)); // Blue when selected
                } else if (getModel().isPressed()) {
                    g2d.setColor(new Color(0x94, 0xA3, 0xB8)); // Darker gray when pressed
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(0xA1, 0xA1, 0xA1)); // Medium gray when hovered
                } else {
                    g2d.setColor(new Color(0xE2, 0xE8, 0xF0)); // Light gray when idle
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Draw text
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() - textHeight) / 2 + fm.getAscent();
                
                if (isSelected()) {
                    g2d.setColor(Color.WHITE); // White text when selected
                } else {
                    g2d.setColor(new Color(0x47, 0x55, 0x69)); // Slate gray text when not selected
                }
                
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
            
            // Store selection state
            private boolean selected = isSelected;
            
            public boolean isSelected() {
                return selected;
            }
            
            public void setSelected(boolean selected) {
                this.selected = selected;
                repaint();
            }
        };
        
        // Button styling
        button.setPreferredSize(new Dimension(30, 30));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Set font
        if (interMedium != null) {
            button.setFont(interMedium.deriveFont(13f));
        } else {
            button.setFont(new Font("Sans-Serif", Font.PLAIN, 13));
        }
        
        return button;
    }
    
    /**
     * Switch to the specified page
     */
    private void switchToPage(int pageNumber) {
        if (pageNumber < 1 || pageNumber > TOTAL_PAGES || pageNumber == currentPage) {
            return; // Invalid page or already on this page
        }
        
        // Update current page
        currentPage = pageNumber;
        
        // Update button states
        page1Button.setSelected(pageNumber == 1);
        page2Button.setSelected(pageNumber == 2);
        
        // Find and show the appropriate page panel
        for (Component component : getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                String panelName = panel.getName();
                
                if (panelName != null) {
                    if (panelName.equals("page1Panel")) {
                        panel.setVisible(pageNumber == 1);
                    } else if (panelName.equals("page2Panel")) {
                        panel.setVisible(pageNumber == 2);
                        
                        // If switching to page 2, ensure social stances are properly displayed
                        if (pageNumber == 2 && socialIssuesPanel != null) {
                            // Get the current stances again to ensure they're properly displayed
                            Map<String, String> currentStances = socialIssuesPanel.getSelectedStances();
                            socialIssuesPanel.setSelectedStances(currentStances);
                            socialIssuesPanel.revalidate();
                            socialIssuesPanel.repaint();
                        }
                    }
                }
            }
        }
        
        // Repaint the panel
        revalidate();
        repaint();
    }
    
    /**
     * Creates a label with a main text and smaller, lighter secondary text
     */
    private JPanel createSplitLabel(String mainText, String secondaryText, int width) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setOpaque(false);
        
        // Create main label
        JLabel mainLabel = new JLabel(mainText);
        mainLabel.setForeground(new Color(0x47, 0x55, 0x69)); // #475569
        
        // Calculate font sizes
        float mainFontSize = 15f;
        float secondaryFontSize = mainFontSize * 7f/8f; // 7/8 of the original size (reduced by 1/8)
        
        mainLabel.setFont(interSemiBold != null ? 
                        interSemiBold.deriveFont(mainFontSize) : 
                        new Font("Sans-Serif", Font.BOLD, (int)mainFontSize));
        
        // Create secondary label (comma separated)
        JLabel secondaryLabel = new JLabel(" (" + secondaryText + ")");
        secondaryLabel.setForeground(new Color(0xA1, 0xA1, 0xA1)); // #A1A1A1
        secondaryLabel.setFont(interRegular != null ? 
                             interRegular.deriveFont(secondaryFontSize) : // 7/8 the size of main label
                             new Font("Sans-Serif", Font.PLAIN, (int)secondaryFontSize));
        
        // Add both labels to panel
        labelPanel.add(mainLabel);
        labelPanel.add(secondaryLabel);
        
        return labelPanel;
    }
    
    /**
     * Handle region selection changes
     */
    private void handleRegionSelection(String selectedRegion) {
        // Set the class variable to store the selected region
        this.selectedRegion = selectedRegion;
        System.out.println("Selected region: " + (selectedRegion != null ? selectedRegion : "None"));
    }
    
    /**
     * Handle position selection changes
     */
    private void handlePositionSelection(String selectedPosition) {
        // Set the class variable to store the selected position
        this.selectedPosition = selectedPosition;
        System.out.println("Selected position: " + (selectedPosition != null ? selectedPosition : "None"));
    }
    
    /**
     * Create a styled text field with placeholder text
     */
    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                // Paint custom rounded background
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill with white background using rounded corners
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Draw border with color #CBD5E1, 1px width with rounded corners
                g2d.setColor(searchBorderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                
                g2d.dispose();
                
                // Call parent to paint the text
                super.paintComponent(g);
            }
        };
        
        // Set initial text properties
        textField.setForeground(Color.GRAY);
        textField.setFont(interRegular != null ? 
                       interRegular.deriveFont(13f) : 
                       new Font("Sans-Serif", Font.PLAIN, 13));
        textField.setOpaque(false);
        textField.setBorder(new EmptyBorder(0, 10, 0, 10)); // Add left/right padding
        
        // Add focus listener for placeholder behavior
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
        
        return textField;
    }
    
    /**
     * Create a styled button with the specified text and color
     */
    private JButton createStyledButton(String text, Color buttonColor) {
        // Create a custom button with rounded corners
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background
                if (getModel().isPressed()) {
                    // Darker shade when pressed
                    g2d.setColor(darken(buttonColor, 0.2f));
                } else if (getModel().isRollover()) {
                    // Original color when hovered
                    g2d.setColor(buttonColor);
                } else {
                    // Slightly lighter when idle
                    g2d.setColor(buttonColor);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Draw text
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() - textHeight) / 2 + fm.getAscent();
                
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        
        button.setBackground(buttonColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Use Inter SemiBold font if available
        if (interSemiBold != null) {
            button.setFont(interSemiBold.deriveFont(14f));
        } else {
            button.setFont(new Font("Sans-Serif", Font.BOLD, 14));
        }
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });
        
        return button;
    }
    
    /**
     * Darken a color by the specified factor
     */
    private Color darken(Color color, float factor) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0, hsb[2] - factor));
    }
    
    /**
     * Draw a shadow effect beneath the panel
     */
    private void drawShadow(Graphics2D g2d, int x, int y, int width, int height) {
        // Create shadow effect
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Save original composite and set shadow opacity
        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, SHADOW_OPACITY));
        
        // Draw multiple layers of shadow with decreasing opacity for better effect
        for (int i = 0; i < SHADOW_SIZE; i++) {
            float opacity = SHADOW_OPACITY * (1.0f - (float)i / SHADOW_SIZE);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            
            // Draw shadow with offset
            g2d.setColor(shadowColor);
            g2d.fillRoundRect(
                x + SHADOW_OFFSET_X - (SHADOW_SIZE - i) / 2,
                y + SHADOW_OFFSET_Y - (SHADOW_SIZE - i) / 2 + i,
                width + (SHADOW_SIZE - i),
                height + (SHADOW_SIZE - i),
                CORNER_RADIUS + (SHADOW_SIZE - i),
                CORNER_RADIUS + (SHADOW_SIZE - i)
            );
        }
        
        // Restore original composite
        g2d.setComposite(originalComposite);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Use Graphics2D for better rendering
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Calculate panel area accounting for shadow
        int contentX = 0;
        int contentY = 0;
        int contentWidth = getWidth() - Math.abs(SHADOW_OFFSET_X) - SHADOW_SIZE;
        int contentHeight = getHeight() - Math.abs(SHADOW_OFFSET_Y) - SHADOW_SIZE;
        
        // First draw the shadow
        drawShadow(g2d, contentX, contentY, contentWidth, contentHeight);
        
        // Draw the panel with rounded corners
        g2d.setColor(panelColor);
        g2d.fillRoundRect(contentX, contentY, contentWidth, contentHeight, CORNER_RADIUS, CORNER_RADIUS);
        
        // Draw the thin 0.5px outline with the same color as shadow but with 0.1f opacity
        g2d.setColor(shadowColor);
        g2d.setStroke(new BasicStroke(BORDER_WIDTH));
        
        // Save the original composite and set the opacity for the outline
        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
        
        g2d.drawRoundRect(contentX, contentY, contentWidth, contentHeight, CORNER_RADIUS, CORNER_RADIUS);
        
        // Restore original composite
        g2d.setComposite(originalComposite);
        
        // Draw header text with Inter-Black font and #475569 color
        g2d.setColor(headerTextColor);
        
        // Use Inter-Black font if available, otherwise fall back to Sans-Serif Bold
        if (interBlack != null) {
            g2d.setFont(interBlack.deriveFont(30f));
        } else {
            g2d.setFont(new Font("Sans-Serif", Font.BOLD, 30));
        }
        
        g2d.drawString("Candidate Details", 20, 45);
        
        // Draw divider line under header text
        int dividerY = 60; // Position below header text
        g2d.setColor(dividerColor);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawLine(20, dividerY, contentWidth - 20, dividerY);
        
        g2d.dispose();
    }
    
    /**
     * Region dropdown component
     */
    class RegionDropdown {
        private final JPanel parentPanel;
        private final JPanel regionRectangle;
        private JPanel dropdownContent;
        private JScrollPane scrollPane;
        private boolean isOpen = false;
        private final Font mediumFont;
        private final Font regularFont;
        private String selectedRegion = "Select Region";
        private final Consumer<String> selectionCallback;
        
        // List of Philippine regions
        private final String[] regions = {
            "NCR (National Capital Region)",
            "CAR (Cordillera Administrative Region)",
            "Region I (Ilocos Region)",
            "Region II (Cagayan Valley)",
            "Region III (Central Luzon)",
            "Region IV-A (CALABARZON)",
            "MIMAROPA Region",
            "Region V (Bicol Region)",
            "Region VI (Western Visayas)",
            "Region VII (Central Visayas)",
            "Region VIII (Eastern Visayas)",
            "Region IX (Zamboanga Peninsula)",
            "Region X (Northern Mindanao)",
            "Region XI (Davao Region)",
            "Region XII (SOCCSKSARGEN)",
            "Region XIII (Caraga)",
            "BARMM (Bangsamoro)",
        };
        
        /**
         * Creates a new RegionDropdown
         */
        public RegionDropdown(JPanel parentPanel, Font mediumFont, Font regularFont, Consumer<String> selectionCallback) {
            this.parentPanel = parentPanel;
            this.mediumFont = mediumFont;
            this.regularFont = regularFont;
            this.selectionCallback = selectionCallback;
            
            // Create the rectangle for the dropdown
            regionRectangle = createRegionRectangle();
        }
        
        /**
         * Get the region rectangle panel
         */
        public JPanel getRegionRectangle() {
            return regionRectangle;
        }
        
        /**
         * Set the selected region and update the dropdown display
         * @param region The region to set as selected
         */
        public void setSelectedRegion(String region) {
            if (region != null && !region.isEmpty()) {
                selectedRegion = region;
                // Repaint to show the selected region
                regionRectangle.repaint();
            }
        }
        
        /**
         * Create the region dropdown rectangle
         */
        private JPanel createRegionRectangle() {
            // Use same border color as text fields
            final Color borderColor = searchBorderColor;
            
            // Track mouse states with boolean flags
            final boolean[] isHovering = {false};
            final boolean[] isClicking = {false};
            
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Fill with white background using rounded corners (same as text fields)
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    
                    // Draw border with same color as text fields
                    g2d.setColor(borderColor);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                    
                    // Draw the selected text in default text color, not white
                    g2d.setColor(isHovering[0] ? Color.BLACK : Color.GRAY);
                    
                    // Use provided font or fall back
                    Font font = regularFont != null ? 
                        regularFont.deriveFont(13f) : 
                        new Font("Sans-Serif", Font.PLAIN, 13);
                    g2d.setFont(font);
                    
                    // Determine text to display
                    String displayText = selectedRegion;
                    
                    // Draw the text with left padding
                    FontMetrics fm = g2d.getFontMetrics();
                    int textHeight = fm.getHeight();
                    int textY = (getHeight() - textHeight) / 2 + fm.getAscent();
                    g2d.drawString(displayText, 10, textY);
                    
                    // Draw dropdown arrow in gray
                    int arrowSize = 5;
                    int arrowX = getWidth() - arrowSize * 3;
                    int arrowY = getHeight() / 2;
                    
                    // Draw triangle pointing down (or up if open)
                    g2d.setColor(new Color(0x94, 0xA3, 0xB8)); // Gray arrow
                    int[] xPoints = {arrowX - arrowSize, arrowX + arrowSize, arrowX};
                    int[] yPoints;
                    if (isOpen) {
                        // Arrow pointing up when open
                        yPoints = new int[]{arrowY + arrowSize / 2, arrowY + arrowSize / 2, arrowY - arrowSize / 2};
                    } else {
                        // Arrow pointing down when closed
                        yPoints = new int[]{arrowY - arrowSize / 2, arrowY - arrowSize / 2, arrowY + arrowSize / 2};
                    }
                    g2d.fillPolygon(xPoints, yPoints, 3);
                }
            };
            
            // Set cursor to hand to indicate it's clickable
            panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            panel.setOpaque(false);
            panel.setName("region-rectangle");
            
            // Add mouse listeners for interaction
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovering[0] = true;
                    panel.repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovering[0] = false;
                    isClicking[0] = false; // Reset clicking state when mouse exits
                    panel.repaint();
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    isClicking[0] = true;
                    panel.repaint();
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    isClicking[0] = false;
                    panel.repaint();
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggleDropdown();
                }
            });
            
            return panel;
        }
        
        /**
         * Toggle the dropdown visibility
         */
        public void toggleDropdown() {
            if (isOpen) {
                closeDropdown();
            } else {
                openDropdown();
            }
        }
        
        /**
         * Open the dropdown
         */
        private void openDropdown() {
            if (isOpen) return;
            isOpen = true;
            
            // Get the rectangle position
            Rectangle rectBounds = regionRectangle.getBounds();
            
            // Create dropdown content panel
            dropdownContent = new JPanel();
            dropdownContent.setLayout(new BoxLayout(dropdownContent, BoxLayout.Y_AXIS));
            dropdownContent.setBackground(Color.WHITE);
            dropdownContent.setName("region-dropdown-content");
            
            // Add region options
            for (String region : regions) {
                JPanel optionPanel = createRegionOption(region);
                dropdownContent.add(optionPanel);
            }
            
            // Create scrollable panel
            scrollPane = new JScrollPane(dropdownContent);
            scrollPane.setName("region-dropdown-scrollpane");
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setUnitIncrement(10);
            
            // Customize scrollbar appearance
            scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                @Override
                protected void configureScrollBarColors() {
                    this.thumbColor = new Color(0xE2, 0xE8, 0xF0);
                    this.trackColor = Color.WHITE;
                }
                
                @Override
                protected JButton createDecreaseButton(int orientation) {
                    return createZeroButton();
                }
                
                @Override
                protected JButton createIncreaseButton(int orientation) {
                    return createZeroButton();
                }
                
                private JButton createZeroButton() {
                    JButton button = new JButton();
                    button.setPreferredSize(new Dimension(0, 0));
                    return button;
                }
            });
            
            // Set bounds for dropdown - same width as the rectangle, height based on content
            int dropdownHeight = Math.min(250, regions.length * 35); // Limit height
            scrollPane.setBounds(rectBounds.x, rectBounds.y + rectBounds.height, rectBounds.width, dropdownHeight);
            
            // Add to parent panel
            parentPanel.add(scrollPane);
            parentPanel.setComponentZOrder(scrollPane, 0); // Put on top
            parentPanel.revalidate();
            parentPanel.repaint();
            
            // Repaint the rectangle to show the open state
            regionRectangle.repaint();
            
            // Add click listener to close when clicking outside
            addGlobalClickListener();
        }
        
        /**
         * Close the dropdown
         */
        private void closeDropdown() {
            if (!isOpen) return;
            isOpen = false;
            
            // Remove dropdown content
            if (scrollPane != null) {
                parentPanel.remove(scrollPane);
                scrollPane = null;
                dropdownContent = null;
                parentPanel.revalidate();
                parentPanel.repaint();
            }
            
            // Repaint the rectangle to show the closed state
            regionRectangle.repaint();
            
            // Remove the global click listener
            removeGlobalClickListener();
        }
        
        /**
         * Create a region option panel
         */
        private JPanel createRegionOption(String region) {
            JPanel option = new JPanel(new BorderLayout());
            option.setBackground(Color.WHITE);
            option.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            option.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            
            JLabel label = new JLabel(region);
            if (regularFont != null) {
                label.setFont(regularFont.deriveFont(13f));
            } else {
                label.setFont(new Font("Sans-Serif", Font.PLAIN, 13));
            }
            label.setForeground(new Color(0x47, 0x55, 0x69));
            option.add(label, BorderLayout.WEST);
            
            // Add hover effect
            option.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    option.setBackground(new Color(0xF1, 0xF5, 0xF9));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    option.setBackground(Color.WHITE);
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedRegion = region;
                    selectionCallback.accept(region);
                    closeDropdown();
                    regionRectangle.repaint();
                }
            });
            
            return option;
        }
        
        /**
         * Global click listener to close dropdown when clicking outside
         */
        private MouseAdapter globalClickListener;
        
        private void addGlobalClickListener() {
            if (globalClickListener == null) {
                globalClickListener = new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Component clicked = e.getComponent();
                        boolean clickedOnDropdown = false;
                        
                        // Check if the click was on the dropdown or its components
                        if (clicked == regionRectangle) {
                            clickedOnDropdown = true;
                        } else if (scrollPane != null) {
                            Component clickedComponent = SwingUtilities.getDeepestComponentAt(scrollPane, e.getX(), e.getY());
                            if (clickedComponent != null) {
                                clickedOnDropdown = true;
                            }
                        }
                        
                        if (!clickedOnDropdown) {
                            closeDropdown();
                        }
                    }
                };
                
                // Add listener to parent panel
                parentPanel.addMouseListener(globalClickListener);
            }
        }
        
        private void removeGlobalClickListener() {
            if (globalClickListener != null) {
                parentPanel.removeMouseListener(globalClickListener);
                globalClickListener = null;
            }
        }
        
        /**
         * Enables or disables the dropdown
         */
        public void setEnabled(boolean enabled) {
            regionRectangle.setEnabled(enabled);
            Component[] components = regionRectangle.getComponents();
            for (Component component : components) {
                component.setEnabled(enabled);
            }
            
            // If disabled and open, close the dropdown
            if (!enabled && isOpen) {
                closeDropdown();
            }
        }
    }
    
    /**
     * Positions dropdown component
     */
    class PositionsDropdown {
        private final JPanel parentPanel;
        private final JPanel positionsRectangle;
        private JPanel dropdownContent;
        private JScrollPane scrollPane;
        private boolean isOpen = false;
        private final Font mediumFont;
        private final Font regularFont;
        private String selectedPosition = "Select Position";
        private final Consumer<String> selectionCallback;
        
        // List of political positions
        private final String[] positions = {
            "President",
            "Vice President",
            "Senator",
            "Representative",
            "Governor",
            "Vice Governor",
            "Mayor",
            "Vice Mayor",
            "Councilor",
            "Party-list Representative",
            "Cabinet Secretary",
            "Diplomat",
        };
        
        /**
         * Creates a new PositionsDropdown
         */
        public PositionsDropdown(JPanel parentPanel, Font mediumFont, Font regularFont, Consumer<String> selectionCallback) {
            this.parentPanel = parentPanel;
            this.mediumFont = mediumFont;
            this.regularFont = regularFont;
            this.selectionCallback = selectionCallback;
            
            // Create the rectangle for the dropdown
            positionsRectangle = createPositionsRectangle();
        }
        
        /**
         * Get the positions rectangle panel
         */
        public JPanel getPositionsRectangle() {
            return positionsRectangle;
        }
        
        /**
         * Set the selected position and update the dropdown display
         * @param position The position to set as selected
         */
        public void setSelectedPosition(String position) {
            if (position != null && !position.isEmpty()) {
                selectedPosition = position;
                // Repaint to show the selected position
                positionsRectangle.repaint();
            }
        }
        
        /**
         * Create the positions dropdown rectangle
         */
        private JPanel createPositionsRectangle() {
            // Use same border color as text fields
            final Color borderColor = searchBorderColor;
            
            // Track mouse states with boolean flags
            final boolean[] isHovering = {false};
            final boolean[] isClicking = {false};
            
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Fill with white background using rounded corners (same as text fields)
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    
                    // Draw border with same color as text fields
                    g2d.setColor(borderColor);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                    
                    // Draw the selected text in default text color, not white
                    g2d.setColor(isHovering[0] ? Color.BLACK : Color.GRAY);
                    
                    // Use provided font or fall back
                    Font font = regularFont != null ? 
                        regularFont.deriveFont(13f) : 
                        new Font("Sans-Serif", Font.PLAIN, 13);
                    g2d.setFont(font);
                    
                    // Determine text to display
                    String displayText = selectedPosition;
                    
                    // Draw the text with left padding
                    FontMetrics fm = g2d.getFontMetrics();
                    int textHeight = fm.getHeight();
                    int textY = (getHeight() - textHeight) / 2 + fm.getAscent();
                    g2d.drawString(displayText, 10, textY);
                    
                    // Draw dropdown arrow in gray
                    int arrowSize = 5;
                    int arrowX = getWidth() - arrowSize * 3;
                    int arrowY = getHeight() / 2;
                    
                    // Draw triangle pointing down (or up if open)
                    g2d.setColor(new Color(0x94, 0xA3, 0xB8)); // Gray arrow
                    int[] xPoints = {arrowX - arrowSize, arrowX + arrowSize, arrowX};
                    int[] yPoints;
                    if (isOpen) {
                        // Arrow pointing up when open
                        yPoints = new int[]{arrowY + arrowSize / 2, arrowY + arrowSize / 2, arrowY - arrowSize / 2};
                    } else {
                        // Arrow pointing down when closed
                        yPoints = new int[]{arrowY - arrowSize / 2, arrowY - arrowSize / 2, arrowY + arrowSize / 2};
                    }
                    g2d.fillPolygon(xPoints, yPoints, 3);
                }
            };
            
            // Set cursor to hand to indicate it's clickable
            panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            panel.setOpaque(false);
            panel.setName("positions-rectangle");
            
            // Add mouse listeners for interaction
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovering[0] = true;
                    panel.repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovering[0] = false;
                    isClicking[0] = false; // Reset clicking state when mouse exits
                    panel.repaint();
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    isClicking[0] = true;
                    panel.repaint();
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    isClicking[0] = false;
                    panel.repaint();
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggleDropdown();
                }
            });
            
            return panel;
        }
        
        /**
         * Toggle the dropdown visibility
         */
        public void toggleDropdown() {
            if (isOpen) {
                closeDropdown();
            } else {
                openDropdown();
            }
        }
        
        /**
         * Open the dropdown
         */
        private void openDropdown() {
            if (isOpen) return;
            isOpen = true;
            
            // Get the rectangle position
            Rectangle rectBounds = positionsRectangle.getBounds();
            
            // Create dropdown content panel
            dropdownContent = new JPanel();
            dropdownContent.setLayout(new BoxLayout(dropdownContent, BoxLayout.Y_AXIS));
            dropdownContent.setBackground(Color.WHITE);
            dropdownContent.setName("positions-dropdown-content");
            
            // Add position options
            for (String position : positions) {
                JPanel optionPanel = createPositionOption(position);
                dropdownContent.add(optionPanel);
            }
            
            // Create scrollable panel
            scrollPane = new JScrollPane(dropdownContent);
            scrollPane.setName("positions-dropdown-scrollpane");
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setUnitIncrement(10);
            
            // Customize scrollbar appearance
            scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                @Override
                protected void configureScrollBarColors() {
                    this.thumbColor = new Color(0xE2, 0xE8, 0xF0);
                    this.trackColor = Color.WHITE;
                }
                
                @Override
                protected JButton createDecreaseButton(int orientation) {
                    return createZeroButton();
                }
                
                @Override
                protected JButton createIncreaseButton(int orientation) {
                    return createZeroButton();
                }
                
                private JButton createZeroButton() {
                    JButton button = new JButton();
                    button.setPreferredSize(new Dimension(0, 0));
                    return button;
                }
            });
            
            // Set bounds for dropdown - same width as the rectangle, height based on content
            int dropdownHeight = Math.min(250, positions.length * 35); // Limit height
            scrollPane.setBounds(rectBounds.x, rectBounds.y + rectBounds.height, rectBounds.width, dropdownHeight);
            
            // Add to parent panel
            parentPanel.add(scrollPane);
            parentPanel.setComponentZOrder(scrollPane, 0); // Put on top
            parentPanel.revalidate();
            parentPanel.repaint();
            
            // Repaint the rectangle to show the open state
            positionsRectangle.repaint();
            
            // Add click listener to close when clicking outside
            addGlobalClickListener();
        }
        
        /**
         * Close the dropdown
         */
        private void closeDropdown() {
            if (!isOpen) return;
            isOpen = false;
            
            // Remove dropdown content
            if (scrollPane != null) {
                parentPanel.remove(scrollPane);
                scrollPane = null;
                dropdownContent = null;
                parentPanel.revalidate();
                parentPanel.repaint();
            }
            
            // Repaint the rectangle to show the closed state
            positionsRectangle.repaint();
            
            // Remove the global click listener
            removeGlobalClickListener();
        }
        
        /**
         * Create a position option panel
         */
        private JPanel createPositionOption(String position) {
            JPanel option = new JPanel(new BorderLayout());
            option.setBackground(Color.WHITE);
            option.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            option.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            
            JLabel label = new JLabel(position);
            if (regularFont != null) {
                label.setFont(regularFont.deriveFont(13f));
            } else {
                label.setFont(new Font("Sans-Serif", Font.PLAIN, 13));
            }
            label.setForeground(new Color(0x47, 0x55, 0x69));
            option.add(label, BorderLayout.WEST);
            
            // Add hover effect
            option.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    option.setBackground(new Color(0xF1, 0xF5, 0xF9));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    option.setBackground(Color.WHITE);
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedPosition = position;
                    selectionCallback.accept(position);
                    closeDropdown();
                    positionsRectangle.repaint();
                }
            });
            
            return option;
        }
        
        /**
         * Global click listener to close dropdown when clicking outside
         */
        private MouseAdapter globalClickListener;
        
        private void addGlobalClickListener() {
            if (globalClickListener == null) {
                globalClickListener = new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Component clicked = e.getComponent();
                        boolean clickedOnDropdown = false;
                        
                        // Check if the click was on the dropdown or its components
                        if (clicked == positionsRectangle) {
                            clickedOnDropdown = true;
                        } else if (scrollPane != null) {
                            Component clickedComponent = SwingUtilities.getDeepestComponentAt(scrollPane, e.getX(), e.getY());
                            if (clickedComponent != null) {
                                clickedOnDropdown = true;
                            }
                        }
                        
                        if (!clickedOnDropdown) {
                            closeDropdown();
                        }
                    }
                };
                
                // Add listener to parent panel
                parentPanel.addMouseListener(globalClickListener);
            }
        }
        
        private void removeGlobalClickListener() {
            if (globalClickListener != null) {
                parentPanel.removeMouseListener(globalClickListener);
                globalClickListener = null;
            }
        }
        
        /**
         * Enables or disables the dropdown
         */
        public void setEnabled(boolean enabled) {
            positionsRectangle.setEnabled(enabled);
            Component[] components = positionsRectangle.getComponents();
            for (Component component : components) {
                component.setEnabled(enabled);
            }
            
            // If disabled and open, close the dropdown
            if (!enabled && isOpen) {
                closeDropdown();
            }
        }
    }
    
    /**
     * Creates an image preview box with placeholder and styling
     */
    private JPanel createImagePreviewBox(int width, int height) {
        JPanel imageBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background with rounded corners using specified gray color #94A3B8
                g2d.setColor(new Color(0xECECEC));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Draw border
                g2d.setColor(searchBorderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                
                // Calculate center position
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2 - 10; // Shift up slightly to make room for text
                
                // Draw plus icon - 50% smaller and color #475569
                int plusSize = 12; // Was 24, now 12 (50% smaller)
                int strokeWidth = 1; // Was 2, now 1 (50% smaller)
                g2d.setColor(new Color(0x47, 0x55, 0x69)); // #475569 instead of white
                g2d.setStroke(new BasicStroke(strokeWidth));
                
                // Horizontal line of plus
                g2d.drawLine(
                    centerX - plusSize/2, 
                    centerY, 
                    centerX + plusSize/2, 
                    centerY
                );
                
                // Vertical line of plus
                g2d.drawLine(
                    centerX, 
                    centerY - plusSize/2, 
                    centerX, 
                    centerY + plusSize/2
                );
                
                // Draw "Add Photo" text below plus icon - 50% smaller and color #475569
                // Always use Inter-SemiBold font if available, otherwise fallback
                if (interSemiBold != null) {
                    g2d.setFont(interSemiBold.deriveFont(7f));
                } else {
                    g2d.setFont(new Font("Sans-Serif", Font.BOLD, 7));
                }
                String labelText = "Add Photo";
                FontMetrics metrics = g2d.getFontMetrics();
                int textWidth = metrics.stringWidth(labelText);
                g2d.drawString(labelText, 
                            centerX - textWidth / 2, 
                            centerY + plusSize/2 + 12); // Adjusted spacing (was 24, now 12)
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(width, height);
            }
        };
        
        // Apply the same shadow effect as the panel
        imageBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Make it semi-transparent and add click listener for selecting an image
        imageBox.setOpaque(false);
        imageBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add mouse listener to simulate image selection
        imageBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Image box clicked - would open file chooser here");
                // Would implement file chooser here in a real application
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                // Darken the color slightly on hover
                imageBox.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Restore original color
                imageBox.repaint();
            }
        });
        
        return imageBox;
    }
    
    /**
     * Custom JToggleButton for social stance selection
     */
    private class StanceButton extends JToggleButton {
        private final Color stanceColor;
        private final String stanceText;
        
        public StanceButton(String text, Color color) {
            super(text);
            this.stanceText = text;
            this.stanceColor = color;
            
            // Set button styling
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            
            // Set font
            if (interMedium != null) {
                setFont(interMedium.deriveFont(12f));
            } else {
                setFont(new Font("Sans-Serif", Font.PLAIN, 12));
            }
            
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Calculate dimensions
            int width = getWidth();
            int height = getHeight();
            int cornerRadius = 8;
            
            // Draw backgrounds
            if (isSelected()) {
                // Selected state - full color
                g2d.setColor(stanceColor);
                g2d.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
                
                // Draw text
                g2d.setColor(Color.WHITE);
            } else if (getModel().isPressed()) {
                // Pressed state - lighter color
                g2d.setColor(new Color(
                    Math.min(255, stanceColor.getRed() + 20),
                    Math.min(255, stanceColor.getGreen() + 20),
                    Math.min(255, stanceColor.getBlue() + 20)
                ));
                g2d.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
                
                // Draw text
                g2d.setColor(Color.WHITE);
            } else if (getModel().isRollover()) {
                // Hover state - very light tint
                g2d.setColor(new Color(
                    stanceColor.getRed(),
                    stanceColor.getGreen(),
                    stanceColor.getBlue(),
                    50
                ));
                g2d.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
                
                // Draw border
                g2d.setColor(stanceColor);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
                
                // Draw text
                g2d.setColor(stanceColor);
            } else {
                // Normal state - just outline
                g2d.setColor(new Color(
                    stanceColor.getRed(),
                    stanceColor.getGreen(),
                    stanceColor.getBlue(),
                    40
                ));
                g2d.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
                
                // Draw text
                g2d.setColor(new Color(0x47, 0x55, 0x69)); // Slate gray
            }
            
            // Draw text
            FontMetrics metrics = g2d.getFontMetrics(getFont());
            int textWidth = metrics.stringWidth(stanceText);
            int textHeight = metrics.getHeight();
            g2d.setFont(getFont());
            g2d.drawString(stanceText, 
                          (width - textWidth) / 2, 
                          (height - textHeight) / 2 + metrics.getAscent());
            
            g2d.dispose();
        }
    }
    
    /**
     * Save the candidate data
     */
    private void saveCandidate() {
        // Validate all required fields
        if (!validateField(nameField, "Enter candidate name", "name")) return;
        if (!validateField(ageField, "Enter age", "age")) return;
        if (!validateField(partyField, "Enter party affiliation", "party affiliation")) return;
        if (!validateField(yearsField, "Enter years of experience", "years of experience")) return;
        if (!validateField(sloganField, "Enter campaign slogan", "campaign slogan")) return;
        if (!validateField(platformsField, "Enter platforms", "platforms")) return;
        if (!validateField(supportedField, "Enter supported issues", "supported issues")) return;
        if (!validateField(lawsField, "Enter notable laws", "notable laws")) return;
        if (!validateField(opposedField, "Enter opposed issues", "opposed issues")) return;
        
        // Validate dropdown selections
        if (selectedRegion.equals("Select Region")) {
            AdminPanelUI.showNotification(this, "Please select a region", "Missing Data", "warning");
            switchToPage(1);
            return;
        }
        
        if (selectedPosition.equals("Select Position")) {
            AdminPanelUI.showNotification(this, "Please select a position", "Missing Data", "warning");
            switchToPage(1);
            return;
        }
        
        // Validate social stances on page 2
        Map<String, String> stances = socialIssuesPanel.getSelectedStances();
        if (stances.isEmpty()) {
            AdminPanelUI.showNotification(this, "Please select at least one social stance position", "Missing Data", "warning");
            switchToPage(2); // Switch to page 2 where social stances are
            return;
        }
        
        // Create a map to store all candidate data
        Map<String, String> candidateData = new HashMap<>();
        
        // Collect data from form fields
        candidateData.put("Name", getFieldValue(nameField, "Enter candidate name"));
        candidateData.put("Age", getFieldValue(ageField, "Enter age"));
        candidateData.put("Region", selectedRegion);
        candidateData.put("Position", selectedPosition); // Changed back to "Position"
        candidateData.put("Party Affiliation", getFieldValue(partyField, "Enter party affiliation"));
        candidateData.put("Years of Experience", getFieldValue(yearsField, "Enter years of experience"));
        candidateData.put("Campaign Slogan", getFieldValue(sloganField, "Enter campaign slogan"));
        candidateData.put("Platforms", getFieldValue(platformsField, "Enter platforms"));
        candidateData.put("Supported Issues", getFieldValue(supportedField, "Enter supported issues"));
        candidateData.put("Notable Laws", getFieldValue(lawsField, "Enter notable laws"));
        candidateData.put("Opposed Issues", getFieldValue(opposedField, "Enter opposed issues"));
        
        // Add social stances as a formatted string
        candidateData.put("Social Stances", CandidateProfiles.formatSocialStances(stances));
        
        // For now, set a default image path
        if (!candidateData.containsKey("Image") || candidateData.get("Image").isEmpty()) {
            candidateData.put("Image", "resources/images/candidates/default.png");
        }
        
        // Save the candidate data
        boolean success;
        if (isEditMode) {
            success = CandidateProfiles.updateCandidate(editCandidateIndex, candidateData);
        } else {
            success = CandidateProfiles.addCandidate(candidateData);
        }
        
        // Show result message
        if (success) {
            AdminPanelUI.showNotification(this, "Candidate " + (isEditMode ? "updated" : "added") + " successfully", "Success", "info");
            
            // Get reference to the directory panel and refresh it directly
            CandidateDirectoryPanel directoryPanel = findDirectoryPanel();
            if (directoryPanel != null) {
                // Clear and reload the candidate list
                directoryPanel.clearCandidates();
                directoryPanel.loadCandidatesFromProfiles();
                
                // Force a repaint to show changes immediately
                directoryPanel.revalidate();
                directoryPanel.repaint();
            }
            
            clearForm(); // Reset form for next entry
        } else {
            AdminPanelUI.showNotification(this, "Failed to " + (isEditMode ? "update" : "add") + " candidate", "Error", "error");
        }
    }
    
    /**
     * Helper method to find the CandidateDirectoryPanel in the parent hierarchy
     */
    private CandidateDirectoryPanel findDirectoryPanel() {
        // Start from parent and search upward until we find a JLayeredPane
        Container parent = getParent();
        JLayeredPane layeredPane = null;
        
        while (parent != null) {
            if (parent instanceof JLayeredPane) {
                layeredPane = (JLayeredPane) parent;
                break;
            }
            parent = parent.getParent();
        }
        
        // If we found a layered pane, look for the directory panel among its components
        if (layeredPane != null) {
            // Search through all components in the layered pane
            Component[] components = layeredPane.getComponents();
            for (Component comp : components) {
                if (comp instanceof CandidateDirectoryPanel) {
                    return (CandidateDirectoryPanel) comp;
                }
            }
        }
        
        // Fallback: try to get it through the JFrame top level container
        parent = getParent();
        while (parent != null && !(parent instanceof JFrame)) {
            parent = parent.getParent();
        }
        
        if (parent instanceof JFrame) {
            JFrame frame = (JFrame) parent;
            
            // If it's AdminPanelUI, use the getter method
            if (frame instanceof AdminPanelUI) {
                return ((AdminPanelUI) frame).getDirectoryPanel();
            }
            
            // Try to access it directly through the content pane
            Container contentPane = frame.getContentPane();
            for (Component comp : contentPane.getComponents()) {
                if (comp instanceof JLayeredPane) {
                    JLayeredPane pane = (JLayeredPane) comp;
                    for (Component c : pane.getComponents()) {
                        if (c instanceof CandidateDirectoryPanel) {
                            return (CandidateDirectoryPanel) c;
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Validate a field to ensure it's not empty or containing placeholder text
     * @param field The field to validate
     * @param placeholder The placeholder text to check against
     * @param fieldName The human-readable name of the field for error messages
     * @return true if valid, false if invalid
     */
    private boolean validateField(JTextField field, String placeholder, String fieldName) {
        if (field.getText().isEmpty() || field.getText().equals(placeholder)) {
            AdminPanelUI.showNotification(this, "Please enter a " + fieldName + " for the candidate", "Missing Data", "warning");
            switchToPage(1); // Go to page 1 where most fields are
            field.requestFocus();
            return false;
        }
        return true;
    }
    
    /**
     * Helper to get field value, returning empty string if it matches the placeholder
     */
    private String getFieldValue(JTextField field, String placeholder) {
        String value = field.getText();
        return value.equals(placeholder) ? "" : value;
    }
    
    /**
     * Clear the form for a new candidate
     */
    public void clearForm() {
        // Reset form fields
        nameField.setText("Enter candidate name");
        nameField.setForeground(Color.GRAY);
        
        ageField.setText("Enter age");
        ageField.setForeground(Color.GRAY);
        
        partyField.setText("Enter party affiliation");
        partyField.setForeground(Color.GRAY);
        
        yearsField.setText("Enter years of experience");
        yearsField.setForeground(Color.GRAY);
        
        sloganField.setText("Enter campaign slogan");
        sloganField.setForeground(Color.GRAY);
        
        platformsField.setText("Enter platforms");
        platformsField.setForeground(Color.GRAY);
        
        supportedField.setText("Enter supported issues");
        supportedField.setForeground(Color.GRAY);
        
        lawsField.setText("Enter notable laws");
        lawsField.setForeground(Color.GRAY);
        
        opposedField.setText("Enter opposed issues");
        opposedField.setForeground(Color.GRAY);
        
        // Reset dropdowns
        selectedRegion = "Select Region";
        selectedPosition = "Select Position";
        
        // Reset edit mode to false since we're creating a new candidate
        isEditMode = false;
        editCandidateIndex = -1;
        
        // Reset social stances (by creating a new empty map)
        Map<String, String> emptyStances = new HashMap<>();
        socialIssuesPanel.setSelectedStances(emptyStances);
        
        // Reset page 2 button indicator
        if (page2Button != null) {
            page2Button.setText("2");
            page2Button.setToolTipText(null);
            page2Button.repaint();
        }
        
        // Switch back to page 1
        switchToPage(1);
        
        // Enable all fields since we're in create mode
        setFieldsEnabled(true);
    }
    
    /**
     * Load candidate data for editing
     * @param index The index of the candidate to edit
     */
    public void loadCandidate(int index) {
        Map<String, String> candidateData = CandidateProfiles.getCandidate(index);
        if (candidateData == null) {
            JOptionPane.showMessageDialog(this, 
                                         "Failed to load candidate data", 
                                         "Error", 
                                         JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Set edit mode
        isEditMode = true;
        editCandidateIndex = index;
        
        // Set form field values
        setFieldText(nameField, candidateData.get("Name"));
        setFieldText(ageField, candidateData.get("Age"));
        setFieldText(partyField, candidateData.get("Party Affiliation"));
        setFieldText(yearsField, candidateData.get("Years of Experience"));
        setFieldText(sloganField, candidateData.get("Campaign Slogan"));
        setFieldText(platformsField, candidateData.get("Platforms"));
        setFieldText(supportedField, candidateData.get("Supported Issues"));
        setFieldText(lawsField, candidateData.get("Notable Laws"));
        setFieldText(opposedField, candidateData.get("Opposed Issues"));
        
        // Set dropdown values - check multiple possible keys for region and position
        String regionValue = null;
        if (candidateData.containsKey("Region")) {
            regionValue = candidateData.get("Region");
        } else if (candidateData.containsKey("Hometown Region")) {
            regionValue = candidateData.get("Hometown Region");
        }
        selectedRegion = regionValue != null ? regionValue : "Select Region";
        
        String positionValue = null;
        if (candidateData.containsKey("Position")) {
            positionValue = candidateData.get("Position");
        } else if (candidateData.containsKey("Running Position")) {
            positionValue = candidateData.get("Running Position");
        } else if (candidateData.containsKey("Positions")) {
            positionValue = candidateData.get("Positions");
        }
        selectedPosition = positionValue != null ? positionValue : "Select Position";
        
        // Update the dropdowns to show selected values
        updateDropdownDisplays();
        
        // Load social stances
        String stancesStr = candidateData.getOrDefault("Social Stances", "");
        Map<String, String> stances = CandidateProfiles.parseSocialStances(stancesStr);
        socialIssuesPanel.setSelectedStances(stances);
        
        // Update page 2 button to indicate if there are defined stances
        updatePage2ButtonIndicator(stances);
        
        // Switch to page 1
        switchToPage(1);
        
        // Enable all fields since we're in edit mode
        setFieldsEnabled(true);
    }
    
    /**
     * Helper to set text field value with proper color
     */
    private void setFieldText(JTextField field, String value) {
        if (value == null || value.isEmpty()) {
            // If no value, set to placeholder text in gray
            field.setText(field.getText()); // Keep placeholder
            field.setForeground(Color.GRAY);
        } else {
            // Set actual value in black
            field.setText(value);
            field.setForeground(Color.BLACK);
        }
    }
    
    /**
     * Updates the UI to display the selected region and position
     * This method is called when a candidate is loaded or when dropdowns are changed
     */
    private void updateDropdownDisplays() {
        // Update the region dropdown to display the selected region
        if (regionDropdown != null && selectedRegion != null) {
            regionDropdown.setSelectedRegion(selectedRegion);
        }
        
        // Update the position dropdown to display the selected position
        if (positionsDropdown != null && selectedPosition != null) {
            positionsDropdown.setSelectedPosition(selectedPosition);
        }
    }
    
    /**
     * Display a candidate's data without enabling edit mode
     * This method shows the candidate details but doesn't allow editing
     * @param index The index of the candidate to display
     */
    public void displayCandidate(int index) {
        Map<String, String> candidateData = CandidateProfiles.getCandidate(index);
        if (candidateData == null) {
            System.err.println("Failed to load candidate data for display");
            return;
        }
        
        // Set view-only mode (not edit mode)
        isEditMode = false;
        editCandidateIndex = -1;
        
        // Set form field values
        setFieldText(nameField, candidateData.get("Name"));
        setFieldText(ageField, candidateData.get("Age"));
        setFieldText(partyField, candidateData.get("Party Affiliation"));
        setFieldText(yearsField, candidateData.get("Years of Experience"));
        setFieldText(sloganField, candidateData.get("Campaign Slogan"));
        setFieldText(platformsField, candidateData.get("Platforms"));
        setFieldText(supportedField, candidateData.get("Supported Issues"));
        setFieldText(lawsField, candidateData.get("Notable Laws"));
        setFieldText(opposedField, candidateData.get("Opposed Issues"));
        
        // Set dropdown values - check multiple possible keys for region and position
        String regionValue = null;
        if (candidateData.containsKey("Region")) {
            regionValue = candidateData.get("Region");
        } else if (candidateData.containsKey("Hometown Region")) {
            regionValue = candidateData.get("Hometown Region");
        }
        selectedRegion = regionValue != null ? regionValue : "Select Region";
        
        String positionValue = null;
        if (candidateData.containsKey("Position")) {
            positionValue = candidateData.get("Position");
        } else if (candidateData.containsKey("Running Position")) {
            positionValue = candidateData.get("Running Position");
        } else if (candidateData.containsKey("Positions")) {
            positionValue = candidateData.get("Positions");
        }
        selectedPosition = positionValue != null ? positionValue : "Select Position";
        
        // Update the dropdowns to show selected values
        updateDropdownDisplays();
        
        // Load social stances
        String stancesStr = candidateData.getOrDefault("Social Stances", "");
        Map<String, String> stances = CandidateProfiles.parseSocialStances(stancesStr);
        socialIssuesPanel.setSelectedStances(stances);
        
        // Update page 2 button to indicate if there are defined stances
        updatePage2ButtonIndicator(stances);
        
        // Switch to page 1
        switchToPage(1);
        
        // Disable all fields since we're in view-only mode
        setFieldsEnabled(false);
    }
    
    /**
     * Updates the page 2 button to indicate if a candidate has defined social stances
     * @param stances The social stance map for the candidate
     */
    private void updatePage2ButtonIndicator(Map<String, String> stances) {
        if (page2Button != null) {
            boolean hasDefinedStances = false;
            
            // Check if any stance is not "No Data"
            for (String stance : stances.values()) {
                if (!stance.equals("No Data")) {
                    hasDefinedStances = true;
                    break;
                }
            }
            
            // If the page button is a custom JButton with our indicator support
            if (page2Button instanceof JButton) {
                // Update tooltip to indicate stance data
                if (hasDefinedStances) {
                    page2Button.setToolTipText("Social stances defined");
                    page2Button.setText("2"); // Add an asterisk to visually indicate data
                } else {
                    page2Button.setToolTipText("No social stances defined");
                    page2Button.setText("2");
                }
                
                page2Button.repaint();
            }
        }
    }
    
    /**
     * Enable or disable all input fields based on edit mode
     * @param enabled Whether fields should be enabled
     */
    private void setFieldsEnabled(boolean enabled) {
        // Enable/disable all text fields
        nameField.setEnabled(enabled);
        ageField.setEnabled(enabled);
        partyField.setEnabled(enabled);
        yearsField.setEnabled(enabled);
        sloganField.setEnabled(enabled);
        platformsField.setEnabled(enabled);
        supportedField.setEnabled(enabled);
        lawsField.setEnabled(enabled);
        opposedField.setEnabled(enabled);
        
        // Enable/disable dropdowns
        if (regionDropdown != null) {
            regionDropdown.setEnabled(enabled);
        }
        
        if (positionsDropdown != null) {
            positionsDropdown.setEnabled(enabled);
        }
        
        // Enable/disable social issues panel
        if (socialIssuesPanel != null) {
            socialIssuesPanel.setEnabled(enabled);
        }
        
        // Show or hide save button based on edit mode
        for (Component component : getComponents()) {
            if (component instanceof JButton && ((JButton) component).getText().equals("Save")) {
                component.setVisible(enabled);
            }
        }
        
        // Refresh the panel
        revalidate();
        repaint();
    }
} 