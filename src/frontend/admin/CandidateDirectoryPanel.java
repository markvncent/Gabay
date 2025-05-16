package frontend.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * A simplified rectangle component for the admin panel.
 * This component is a placeholder with specific dimensions and position.
 */
public class CandidateDirectoryPanel extends JPanel {
    // Fixed dimensions
    private final int PANEL_WIDTH = 461;
    private final int PANEL_HEIGHT = 581;
    
    // Fixed position relative to starting window
    private final int PANEL_X = -2558; // Original position as requested
    private final int PANEL_Y = 270;
    
    // For testing purposes, set this to true to make the panel visible on screen
    private final boolean TEST_MODE = true;
    
    // Test mode positions (when visible on screen)
    private final int TEST_X = 140; // Adjusted for the centering logic
    private final int TEST_Y = 180; // Shifted 20px upward from 200 to increase visibility
    
    // Corner radius for rounded corners
    private final int CORNER_RADIUS = 10;
    
    // Shadow properties
    private final int SHADOW_SIZE = 5;
    private final int SHADOW_OFFSET_X = 1;
    private final int SHADOW_OFFSET_Y = 2;
    private final float SHADOW_OPACITY = 0.1f;
    private final Color shadowColor = new Color(0, 0, 0); // Add explicit shadow color that will be used for border too
    private final float BORDER_WIDTH = 1.0f; // Add border width constant
    
    // Color
    private final Color panelColor = Color.WHITE; // Changed to white background
    private final Color headerColor = Color.WHITE; // Updated to white for the header area
    private final Color headerTextColor = new Color(0x47, 0x55, 0x69); // #475569 for header text
    private final Color searchBorderColor = new Color(0xCB, 0xD5, 0xE1); // #CBD5E1
    private final Color dividerColor = new Color(0xE2, 0xE8, 0xF0); // #E2E8F0 light gray for divider
    private final Color profileSectionColor = new Color(0x2B, 0x37, 0x80); // #2B3780 blue for profile section
    private final Color profileTextColor = Color.WHITE; // White text for profile count
    private final Color profileListBgColor = new Color(0xF8, 0xFA, 0xFC); // #F8FAFC very light blue-gray for profile list
    private final Color profileListBorderColor = new Color(0xE2, 0xE8, 0xF0); // #E2E8F0 light gray for profile list border
    private final Color profileListLabelColor = new Color(0x64, 0x74, 0x8B); // #64748B slate gray for label
    
    // UI Components
    private Font interSemiBold;
    private Font interRegular;
    private Font interBlack;
    private JTextField searchField;
    private JLabel clearIconLabel;
    private BufferedImage searchIconImage;
    private BufferedImage clearIconImage;
    private boolean clearIconVisible = false;
    private ProfileListPanel profileListPanel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private int profileCount = 0;
    
    // Reference to the details panel
    private CandidateDetailsPanel detailsPanel;
    
    // Selected candidate index
    private int selectedCandidateIndex = -1;
    
    // Timer for delayed search
    private Timer searchTimer;
    
    /**
     * Creates a new CandidateDirectoryPanel as a simple rectangle
     */
    public CandidateDirectoryPanel() {
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
        
        // Initialize search timer with 300ms delay (adjust as needed)
        searchTimer = new Timer(300, e -> performSearch());
        searchTimer.setRepeats(false); // Only fire once when triggered
        
        // Load fonts
        loadFonts();
        
        // Load icons
        loadIcons();
        
        // Use null layout for absolute positioning
        setLayout(null);
        
        // Create search components
        createSearchComponents();
        
        // Create profile list panel
        createProfileListPanel();
    }
    
    /**
     * Load required fonts
     */
    private void loadFonts() {
        try {
            // Load Inter fonts
            File interSemiBoldFile = new File("lib/fonts/Inter_18pt-SemiBold.ttf");
            File interRegularFile = new File("lib/fonts/Inter_18pt-Regular.ttf");
            File interBlackFile = new File("lib/fonts/Inter_18pt-Black.ttf");
            
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            
            if (interSemiBoldFile.exists()) {
                interSemiBold = Font.createFont(Font.TRUETYPE_FONT, interSemiBoldFile);
                ge.registerFont(interSemiBold);
            } else {
                interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
            }
            
            if (interRegularFile.exists()) {
                interRegular = Font.createFont(Font.TRUETYPE_FONT, interRegularFile);
                ge.registerFont(interRegular);
            } else {
                interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
            }
            
            if (interBlackFile.exists()) {
                interBlack = Font.createFont(Font.TRUETYPE_FONT, interBlackFile);
                ge.registerFont(interBlack);
            } else {
                interBlack = new Font("Sans-Serif", Font.BOLD, 30);
            }
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
            interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
            interBlack = new Font("Sans-Serif", Font.BOLD, 30);
        }
    }
    
    /**
     * Load search icon and clear icon
     */
    private void loadIcons() {
        try {
            // Try to load the search icon
            File searchFile = new File("resources/images/Candidate Search/search.png");
            if (searchFile.exists()) {
                searchIconImage = ImageIO.read(searchFile);
            }
            
            // Load clear icon (x.png)
            File clearFile = new File("resources/images/Candidate Search/x.png");
            if (clearFile.exists()) {
                clearIconImage = ImageIO.read(clearFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Create search box and button components
     */
    private void createSearchComponents() {
        // Calculate sizes and positions proportional to panel size
        int margin = 20;
        int headerHeight = 40;
        int searchBoxHeight = 35;
        int searchButtonWidth = 70;
        int searchBoxWidth = PANEL_WIDTH - (2 * margin) - searchButtonWidth - 5; // 5px gap
        
        // Create search panel
        JPanel searchBarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill with white background using rounded corners
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Draw border with color #CBD5E1, 1px width with rounded corners
                g2d.setColor(searchBorderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                
                // Draw search icon if available
                if (searchIconImage != null) {
                    // Set icon to exactly 16px height
                    int iconHeight = 16;
                    int iconWidth = (int)((double)searchIconImage.getWidth() / searchIconImage.getHeight() * iconHeight);
                    
                    // Draw icon on left side with padding
                    g2d.drawImage(searchIconImage, 10, (getHeight() - iconHeight) / 2, 
                                iconWidth, iconHeight, this);
                }
            }
        };
        searchBarPanel.setLayout(new BorderLayout());
        searchBarPanel.setBounds(margin, margin + headerHeight + 10, searchBoxWidth, searchBoxHeight);
        searchBarPanel.setOpaque(false);
        
        // Create text field
        searchField = new JTextField("Search candidates...");
        searchField.setForeground(Color.GRAY);
        searchField.setFont(interRegular.deriveFont(13f));
        searchField.setOpaque(false);
        searchField.setBorder(null);
        
        // Create padding panel to position text field correctly
        JPanel paddingPanel = new JPanel(new BorderLayout());
        paddingPanel.setOpaque(false);
        
        // Calculate padding based on icon size
        int leftPadding = 35; // Icon width + spacing
        paddingPanel.setBorder(BorderFactory.createEmptyBorder(0, leftPadding, 0, 10));
        paddingPanel.add(searchField, BorderLayout.CENTER);
        
        // Add clear icon if available
        if (clearIconImage != null) {
            clearIconLabel = new JLabel();
            int clearIconHeight = 14;
            int clearIconWidth = (int)((double)clearIconImage.getWidth() / clearIconImage.getHeight() * clearIconHeight);
            
            // Create scaled icon
            Image scaledImage = clearIconImage.getScaledInstance(clearIconWidth, clearIconHeight, Image.SCALE_SMOOTH);
            clearIconLabel.setIcon(new ImageIcon(scaledImage));
            clearIconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            clearIconLabel.setVisible(false); // Initially hidden
            
            // Add click handler to clear the search text
            clearIconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    searchField.setText("");
                    searchField.requestFocus();
                    clearIconLabel.setVisible(false);
                    clearIconVisible = false;
                    
                    // When cleared, reload all candidates
                    loadCandidatesFromProfiles();
                }
            });
            
            JPanel clearIconPanel = new JPanel(new BorderLayout());
            clearIconPanel.setOpaque(false);
            clearIconPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); // Right padding
            clearIconPanel.add(clearIconLabel, BorderLayout.CENTER);
            paddingPanel.add(clearIconPanel, BorderLayout.EAST);
        }
        
        searchBarPanel.add(paddingPanel, BorderLayout.CENTER);
        
        // Create "Find" button
        JPanel findButton = createFindButton(searchButtonWidth, searchBoxHeight);
        findButton.setBounds(margin + searchBoxWidth + 5, margin + headerHeight + 10, 
                           searchButtonWidth, searchBoxHeight);
        
        // Add focus listener for placeholder behavior
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search candidates...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                    updateClearIconVisibility(false);
                } else if (!searchField.getText().isEmpty()) {
                    updateClearIconVisibility(true);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search candidates...");
                    searchField.setForeground(Color.GRAY);
                    updateClearIconVisibility(false);
                    
                    // When search text is cleared (by losing focus with empty field), reload all candidates
                    loadCandidatesFromProfiles();
                }
            }
        });
        
        // Add document listener to handle text changes
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateClearIconVisibility(!searchField.getText().equals("Search candidates..."));
                // Schedule search after a delay
                if (!searchField.getText().equals("Search candidates...")) {
                    // Restart the timer on each keystroke
                    searchTimer.restart();
                }
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateClearIconVisibility(!searchField.getText().isEmpty());
                // If empty, load all candidates
                if (searchField.getText().isEmpty()) {
                    searchTimer.stop(); // Stop any pending search
                    loadCandidatesFromProfiles();
                } else if (!searchField.getText().equals("Search candidates...")) {
                    // Otherwise schedule a new search
                    searchTimer.restart();
                }
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateClearIconVisibility(!searchField.getText().isEmpty() && 
                                         !searchField.getText().equals("Search candidates..."));
                // Schedule search after a delay
                if (!searchField.getText().equals("Search candidates...")) {
                    searchTimer.restart();
                }
            }
        });
        
        // Add enter key listener to perform search when Enter is pressed
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
        
        // Add components to panel
        add(searchBarPanel);
        add(findButton);
    }
    
    /**
     * Creates a "Find" button with blue background
     */
    private JPanel createFindButton(int width, int height) {
        // Create darker shades for hover and click effects
        final Color filterBlue = new Color(0x2B, 0x37, 0x80); // #2B3780
        final Color hoverBlue = new Color(0x22, 0x2C, 0x66); // Darker shade for hover
        final Color clickBlue = new Color(0x1A, 0x21, 0x4D); // Even darker for click
        
        // Track mouse states with boolean flags
        final boolean[] isHovering = {false};
        final boolean[] isClicking = {false};
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Choose color based on mouse state
                if (isClicking[0]) {
                    g2d.setColor(clickBlue);
                } else if (isHovering[0]) {
                    g2d.setColor(hoverBlue);
                } else {
                    g2d.setColor(filterBlue);
                }
                
                // Draw rounded rectangle with 8px corner radius
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Draw "Find" text in white, centered
                g2d.setColor(Color.WHITE);
                
                // Use available font
                Font findFont = interRegular != null ? 
                    interRegular.deriveFont(Font.BOLD, 13f) : 
                    new Font("Sans-Serif", Font.BOLD, 13);
                g2d.setFont(findFont);
                
                // Center the text - shift down 1px when clicking for "push" effect
                FontMetrics fm = g2d.getFontMetrics();
                String text = "Find";
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = ((getHeight() - textHeight) / 2) + fm.getAscent();
                
                // Apply 1px downward shift when clicking for push effect
                if (isClicking[0]) {
                    y += 1;
                }
                
                g2d.drawString(text, x, y);
            }
        };
        
        // Set cursor to hand to indicate it's clickable
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setPreferredSize(new Dimension(width, height));
        panel.setOpaque(false);
        
        // Add mouse listeners for interaction effects
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
                // Perform search action
                performSearch();
            }
        });
        
        return panel;
    }
    
    /**
     * Update clear icon visibility based on search field content
     */
    private void updateClearIconVisibility(boolean visible) {
        if (clearIconLabel != null && clearIconVisible != visible) {
            clearIconLabel.setVisible(visible);
            clearIconVisible = visible;
        }
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
    
    /**
     * Create the profile list panel
     */
    private void createProfileListPanel() {
        // Calculate position for profile list panel
        int dividerY = 117; // Position below search components
        int profileSectionHeight = 40;
        int profileSectionY = dividerY + 15; // Position below divider with 15px gap
        int profileListY = profileSectionY + profileSectionHeight + 20; // 20px gap after profile count section
        int margin = 20;
        int profileListWidth = PANEL_WIDTH - (2 * margin);
        
        // Reserve space for buttons (40px height + 15px margin)
        int buttonHeight = 40;
        int buttonMargin = 15;
        int profileListHeight = PANEL_HEIGHT - profileListY - buttonHeight - buttonMargin - 20; // 20px bottom margin
        
        // Create and position the profile list panel
        profileListPanel = new ProfileListPanel();
        profileListPanel.setBounds(margin, profileListY, profileListWidth, profileListHeight);
        
        // Load candidates
        loadCandidatesFromProfiles();
        
        // Add listener to update profile count when candidates are added or removed
        profileListPanel.addProfileCountListener(count -> {
            profileCount = count;
            repaint(); // Trigger repaint to update the profile count display
        });
        
        add(profileListPanel);
        
        // Create buttons
        createActionButtons(margin, profileListY + profileListHeight + buttonMargin, profileListWidth, buttonHeight);
    }
    
    /**
     * Create action buttons (Add, Edit, Delete)
     */
    private void createActionButtons(int x, int y, int totalWidth, int height) {
        // Button properties
        int buttonGap = 10;
        int buttonWidth = (totalWidth - (2 * buttonGap)) / 3;
        
        // Button colors as specified
        Color defaultColor = new Color(0xD9, 0xD9, 0xD9); // #D9D9D9 - Light gray
        Color defaultTextColor = new Color(0x47, 0x55, 0x69); // #475569 - Dark slate gray
        Color hoverColor = new Color(0x2B, 0x37, 0x80);   // #2B3780 - Blue
        Color hoverTextColor = Color.WHITE;
        
        // Create Add button
        addButton = createStyledButton("Add", defaultColor, hoverColor, defaultTextColor, hoverTextColor);
        addButton.setBounds(x, y, buttonWidth, height);
        addButton.addActionListener(e -> handleAddButton());
        add(addButton);
        
        // Create Edit button
        editButton = createStyledButton("Edit", defaultColor, hoverColor, defaultTextColor, hoverTextColor);
        editButton.setBounds(x + buttonWidth + buttonGap, y, buttonWidth, height);
        editButton.addActionListener(e -> handleEditButton());
        add(editButton);
        
        // Create Delete button
        deleteButton = createStyledButton("Delete", defaultColor, hoverColor, defaultTextColor, hoverTextColor);
        deleteButton.setBounds(x + (buttonWidth * 2) + (buttonGap * 2), y, buttonWidth, height);
        deleteButton.addActionListener(e -> handleDeleteButton());
        add(deleteButton);
    }
    
    /**
     * Create a styled button with the specified text and background color
     */
    private JButton createStyledButton(String text, Color defaultColor, Color hoverColor, 
                                      Color defaultTextColor, Color hoverTextColor) {
        // Create a custom button with rounded corners
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw text
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() - textHeight) / 2 + fm.getAscent();
                
                g2d.setColor(getForeground());
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        
        button.setBackground(defaultColor);
        button.setForeground(defaultTextColor);
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
        
        // Add hover effect to change to blue with white text
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.setForeground(hoverTextColor);
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultColor);
                button.setForeground(defaultTextColor);
                button.repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(darken(hoverColor, 0.2f));
                button.setForeground(hoverTextColor);
                button.repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.contains(e.getPoint())) {
                    button.setBackground(hoverColor);
                    button.setForeground(hoverTextColor);
                } else {
                    button.setBackground(defaultColor);
                    button.setForeground(defaultTextColor);
                }
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
     * Handle Add button click
     */
    private void handleAddButton() {
        if (detailsPanel == null) {
            System.err.println("Details panel not set");
            return;
        }
        
        // Clear the form for a new candidate and enable editing
        detailsPanel.clearForm();
    }
    
    /**
     * Handle Edit button click
     */
    private void handleEditButton() {
        if (detailsPanel == null) {
            System.err.println("Details panel not set");
            return;
        }
        
        if (selectedCandidateIndex < 0) {
            AdminPanelUI.showNotification(this, "Please select a candidate to edit", "No Selection", "info");
            return;
        }
        
        // Load the candidate data for editing
        detailsPanel.loadCandidate(selectedCandidateIndex);
    }
    
    /**
     * Handle Delete button click
     */
    private void handleDeleteButton() {
        if (selectedCandidateIndex < 0) {
            AdminPanelUI.showNotification(this, "Please select a candidate to delete", "No Selection", "info");
            return;
        }
        
        boolean confirmed = AdminPanelUI.showConfirmDialog(this, "Are you sure you want to delete this candidate?", "Confirm Deletion");
        if (confirmed) {
            // Delete the candidate
            boolean success = CandidateProfiles.deleteCandidate(selectedCandidateIndex);
            
            if (success) {
                AdminPanelUI.showNotification(this, "Candidate deleted successfully", "Success", "info");
                
                // Update the profile list
                profileListPanel.clearCandidates();
                loadCandidatesFromProfiles();
                
                // Reset selection
                selectedCandidateIndex = -1;
            } else {
                AdminPanelUI.showNotification(this, "Failed to delete candidate", "Error", "error");
            }
        }
    }
    
    /**
     * Clear all candidates from the profile list panel
     */
    public void clearCandidates() {
        if (profileListPanel != null) {
            profileListPanel.clearCandidates();
        }
    }
    
    /**
     * Load candidates from CandidateProfiles
     */
    public void loadCandidatesFromProfiles() {
        // Clear any existing candidates first to avoid duplicates
        profileListPanel.clearCandidates();
        
        // Load all candidates
        List<Map<String, String>> candidates = CandidateProfiles.loadCandidates();
        
        // If no candidates found, load sample data
        if (candidates.isEmpty()) {
            profileListPanel.loadSampleData();
            return;
        }
        
        // Add each candidate to the profile list panel
        for (int i = 0; i < candidates.size(); i++) {
            Map<String, String> candidate = candidates.get(i);
            final int index = i; // Final copy for use in lambda
            
            String name = candidate.getOrDefault("Name", "Unknown");
            String position = candidate.getOrDefault("Position", "");
            String party = candidate.getOrDefault("Party Affiliation", "");
            String imagePath = candidate.getOrDefault("Image", "resources/images/candidates/default.png");
            
            // Add candidate to the list with click handler
            profileListPanel.addCandidate(name, position, party, imagePath, e -> {
                selectedCandidateIndex = index;
                selectCandidate(index);
            });
        }
    }
    
    /**
     * Sets the details panel reference
     * @param detailsPanel The CandidateDetailsPanel to associate with this directory
     */
    public void setDetailsPanel(CandidateDetailsPanel detailsPanel) {
        this.detailsPanel = detailsPanel;
    }
    
    /**
     * Update the selected candidate
     * @param index Index of the selected candidate
     */
    public void selectCandidate(int index) {
        selectedCandidateIndex = index;
        
        // Update the selection in the profile list panel
        profileListPanel.setSelectedCardIndex(index);
        
        // Update the details panel if it's connected
        if (detailsPanel != null) {
            if (index >= 0) {
                // Use displayCandidate instead of loadCandidate to enable view-only mode
                detailsPanel.displayCandidate(index);
            } else {
                detailsPanel.clearForm();
            }
        } else {
            System.out.println("Details panel not connected");
        }
    }
    
    /**
     * Perform search based on the current search text
     */
    private void performSearch() {
        String searchQuery = searchField.getText();
        
        // Don't search if text is empty, placeholder, or very short
        if (searchQuery.isEmpty() || searchQuery.equals("Search candidates...") || searchQuery.length() < 2) {
            if (searchQuery.length() < 2 && searchQuery.length() > 0) {
                // If search query is too short, show message but don't search yet
                profileListPanel.clearCandidates();
                profileListPanel.setPlaceholderText("Type at least 2 characters to search.");
                return;
            }
            
            // If empty or placeholder, show all candidates
            loadCandidatesFromProfiles();
            return;
        }
        
        // Make search case-insensitive
        searchQuery = searchQuery.toLowerCase();
        
        // Get all candidates
        List<Map<String, String>> allCandidates = CandidateProfiles.loadCandidates();
        List<Map<String, String>> filteredCandidates = new ArrayList<>();
        List<Integer> filteredIndices = new ArrayList<>();
        
        // Filter candidates based on search query
        for (int i = 0; i < allCandidates.size(); i++) {
            Map<String, String> candidate = allCandidates.get(i);
            
            // Check various fields for matches
            boolean matches = false;
            
            // Check name
            String name = candidate.getOrDefault("Name", "").toLowerCase();
            if (name.contains(searchQuery)) {
                matches = true;
            }
            
            // Check position
            String position = candidate.getOrDefault("Position", "").toLowerCase();
            if (position.contains(searchQuery)) {
                matches = true;
            }
            
            // Check party
            String party = candidate.getOrDefault("Party Affiliation", "").toLowerCase();
            if (party.contains(searchQuery)) {
                matches = true;
            }
            
            // Check region
            String region = candidate.getOrDefault("Region", "").toLowerCase();
            if (region.contains(searchQuery)) {
                matches = true;
            }
            
            // Add to filtered list if there's a match
            if (matches) {
                filteredCandidates.add(candidate);
                filteredIndices.add(i);
            }
        }
        
        // Display the filtered candidates
        displayFilteredCandidates(filteredCandidates, filteredIndices);
    }
    
    /**
     * Display the filtered candidates in the profile list panel
     * @param filteredCandidates List of candidate data that match the search
     * @param originalIndices List of original indices for the filtered candidates
     */
    private void displayFilteredCandidates(List<Map<String, String>> filteredCandidates, List<Integer> originalIndices) {
        // Clear the current profile list
        profileListPanel.clearCandidates();
        
        // If no matches, show a message
        if (filteredCandidates.isEmpty()) {
            profileListPanel.setPlaceholderText("No candidates match your search criteria.");
            return;
        }
        
        // Add each filtered candidate to the profile list
        for (int i = 0; i < filteredCandidates.size(); i++) {
            Map<String, String> candidate = filteredCandidates.get(i);
            final int originalIndex = originalIndices.get(i); // Get the original index for selection
            
            String name = candidate.getOrDefault("Name", "Unknown");
            String position = candidate.getOrDefault("Position", "");
            String party = candidate.getOrDefault("Party Affiliation", "");
            String imagePath = candidate.getOrDefault("Image", "resources/images/candidates/default.png");
            
            // Add candidate to the list with click handler
            profileListPanel.addCandidate(name, position, party, imagePath, e -> {
                selectedCandidateIndex = originalIndex; // Use the original index for proper selection
                selectCandidate(originalIndex);
            });
        }
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
        
        // No longer drawing colored header area - entire panel is white
        
        // Draw header text with Inter-Black font and #475569 color
        g2d.setColor(headerTextColor);
        if (interBlack != null) {
            g2d.setFont(interBlack.deriveFont(30f));
        } else {
            g2d.setFont(new Font("Sans-Serif", Font.BOLD, 28));
        }
        g2d.drawString("Candidate Directory", 20, 45);
        
        // Draw divider line under search
        int dividerY = 117; // Position below search components
        g2d.setColor(dividerColor);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawLine(20, dividerY, contentWidth - 20, dividerY);
        
        // Draw blue profile section rectangle
        int profileSectionHeight = 40;
        int profileSectionY = dividerY + 15; // Position below divider with 15px gap
        g2d.setColor(profileSectionColor);
        g2d.fillRoundRect(20, profileSectionY, contentWidth - 40, profileSectionHeight, 8, 8);
        
        // Draw profile count text
        g2d.setColor(profileTextColor);
        if (interSemiBold != null) {
            g2d.setFont(interSemiBold.deriveFont(16f));
        } else {
            g2d.setFont(new Font("Sans-Serif", Font.BOLD, 16));
        }
        
        // Format the profile count with leading zeros
        String profileCountText = String.format("%02d Profiles", profileCount);
        g2d.drawString(profileCountText, 35, profileSectionY + 25); // 25 = approximate vertical center
        
        g2d.dispose();
    }
} 