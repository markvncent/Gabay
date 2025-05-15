package frontend.comparison;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

/**
 * Modular component for side-by-side candidate search in comparison page
 */
public class SearchCandidateCompare extends JPanel {
    // Colors
    private Color searchBorderColor = new Color(0xCB, 0xD5, 0xE1); // #CBD5E1
    private Color buttonOrange = new Color(0xF8, 0xB3, 0x48); // #F8B348
    private Color paragraphColor = new Color(0x8D, 0x8D, 0x8D); // #8D8D8D
    
    // Fonts
    private Font interRegular;
    private Font interMedium;
    private Font interSemiBold;
    
    // UI Components
    private JTextField leftSearchField;
    private JTextField rightSearchField;
    private JPanel leftSearchPanel;
    private JPanel rightSearchPanel;
    private JPanel compareButton;
    
    // Suggestion components
    private JPopupMenu leftSuggestionPopup;
    private JPopupMenu rightSuggestionPopup;
    private List<String> candidateNames;
    
    // Icons
    private BufferedImage searchIconImage;
    private BufferedImage clearIconImage;
    
    // Callback
    private Consumer<String[]> onCompareCallback;
    
    /**
     * Constructor for the search comparison component
     * @param interRegular Regular font
     * @param interMedium Medium font
     * @param interSemiBold SemiBold font
     * @param onCompareCallback Callback when compare is clicked, passes array with [leftCandidate, rightCandidate]
     */
    public SearchCandidateCompare(Font interRegular, Font interMedium, Font interSemiBold, Consumer<String[]> onCompareCallback) {
        this.interRegular = interRegular;
        this.interMedium = interMedium;
        this.interSemiBold = interSemiBold;
        this.onCompareCallback = onCompareCallback;
        
        // Load candidate names
        loadCandidateNames();
        
        // Load icons
        loadSearchIcon();
        loadClearIcon();
        
        // Setup panel
        setLayout(null); // Use null layout for precise positioning
        setOpaque(false);
        
        // Create suggestion popups
        leftSuggestionPopup = new JPopupMenu();
        leftSuggestionPopup.setBorder(BorderFactory.createEmptyBorder());
        leftSuggestionPopup.setOpaque(false);
        
        rightSuggestionPopup = new JPopupMenu();
        rightSuggestionPopup.setBorder(BorderFactory.createEmptyBorder());
        rightSuggestionPopup.setOpaque(false);
        
        // Create components
        createComponents();
    }
    
    /**
     * Loads candidate names from the candidates.txt file
     */
    private void loadCandidateNames() {
        candidateNames = new ArrayList<>();
        
        try {
            File candidatesFile = new File("resources/data/candidates.txt");
            BufferedReader reader = new BufferedReader(new FileReader(candidatesFile));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Name:")) {
                    // Extract the name part after "Name: "
                    String name = line.substring(6).trim();
                    candidateNames.add(name);
                }
            }
            
            reader.close();
            System.out.println("Loaded " + candidateNames.size() + " candidate names");
        } catch (IOException e) {
            System.out.println("Error loading candidate names: " + e.getMessage());
            // Add some sample names as fallback
            candidateNames.add("Fernando Reyes");
            candidateNames.add("Maria Villanueva-Santos");
            candidateNames.add("Roberto Gonzales Jr.");
        }
    }
    
    /**
     * Create and arrange all components
     */
    private void createComponents() {
        // Create left search panel
        leftSearchPanel = createSearchPanel("Search for first candidate...");
        leftSearchPanel.setBounds(0, 25, 400, 45);
        leftSearchField = (JTextField) ((JPanel)((JPanel)leftSearchPanel.getComponent(0)).getComponent(0)).getComponent(0);
        setupSuggestionListeners(leftSearchField, leftSuggestionPopup, true);
        
        // Create right search panel
        rightSearchPanel = createSearchPanel("Search for second candidate...");
        rightSearchPanel.setBounds(410, 25, 400, 45);
        rightSearchField = (JTextField) ((JPanel)((JPanel)rightSearchPanel.getComponent(0)).getComponent(0)).getComponent(0);
        setupSuggestionListeners(rightSearchField, rightSuggestionPopup, false);
        
        // Create the Compare button
        compareButton = createCompareButton(120, 45);
        compareButton.setBounds(820, 25, 120, 45);
        
        // Add label above left search box with letter spacing
        JLabel leftLabel = new JLabel("Choose a candidate to compare:");
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
        Font labelFont = interSemiBold != null ? 
            interSemiBold.deriveFont(14f).deriveFont(attributes) : 
            new Font("Sans-Serif", Font.PLAIN, 14);
        leftLabel.setFont(labelFont);
        leftLabel.setForeground(paragraphColor);
        leftLabel.setBounds(0, 0, 400, 20);
        
        // Add label above right search box
        JLabel rightLabel = new JLabel("Choose a candidate to compare:");
        rightLabel.setFont(labelFont);
        rightLabel.setForeground(paragraphColor);
        rightLabel.setBounds(410, 0, 400, 20);
        
        // Add components to panel
        add(leftLabel);
        add(rightLabel);
        add(leftSearchPanel);
        add(rightSearchPanel);
        add(compareButton);
        
        // Set preferred size based on components
        setPreferredSize(new Dimension(940, 70));
    }
    
    /**
     * Set up suggestion functionality for a search field
     * @param searchField The text field to add suggestions to
     * @param suggestionPopup The popup menu to show suggestions
     * @param isLeft Whether this is the left or right search field (for positioning)
     */
    private void setupSuggestionListeners(JTextField searchField, JPopupMenu suggestionPopup, boolean isLeft) {
        // Add key listener to show suggestions as the user types
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Don't show suggestions for special keys like arrows
                if (e.getKeyCode() == KeyEvent.VK_UP || 
                    e.getKeyCode() == KeyEvent.VK_DOWN || 
                    e.getKeyCode() == KeyEvent.VK_ENTER) {
                    return;
                }
                
                String text = searchField.getText().trim();
                // Don't show for placeholder or empty text
                if (text.isEmpty() || 
                    text.equals("Search for first candidate...") || 
                    text.equals("Search for second candidate...")) {
                    suggestionPopup.setVisible(false);
                    return;
                }
                
                // Filter suggestions and update popup
                updateSuggestions(text, suggestionPopup, searchField, isLeft);
            }
        });
        
        // Prevent hiding popup immediately when focus is lost
        // Remove the previous focus listener that was causing quick disappearance
        for (FocusListener listener : searchField.getFocusListeners()) {
            if (listener instanceof FocusAdapter) {
                // Keep the original focus listener for placeholder text,
                // but remove our custom one that might be causing issues
                // We'll check the behavior in the code to differentiate
                FocusAdapter adapter = (FocusAdapter) listener;
                // We can't directly check the behavior, so we'll just keep it
            }
        }
    }
    
    /**
     * Update suggestions in the popup based on input text
     * @param searchText Text to search for
     * @param suggestionPopup The popup to update
     * @param searchField The text field suggestions are for
     * @param isLeft Whether this is for the left or right search field
     */
    private void updateSuggestions(String searchText, JPopupMenu suggestionPopup, JTextField searchField, boolean isLeft) {
        suggestionPopup.removeAll();
        
        // Get suggestions matching the search text
        String lowerSearchText = searchText.toLowerCase();
        List<String> suggestions = new ArrayList<>();
        
        for (String name : candidateNames) {
            if (name.toLowerCase().contains(lowerSearchText)) {
                suggestions.add(name);
                // Limit to 10 suggestions for better UX
                if (suggestions.size() >= 10) {
                    break;
                }
            }
        }
        
        // If no suggestions, hide popup
        if (suggestions.isEmpty()) {
            suggestionPopup.setVisible(false);
            return;
        }
        
        // Add suggestions to popup
        for (String suggestion : suggestions) {
            // Custom JPanel for each suggestion for better mouse handling
            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            JLabel itemLabel = new JLabel(suggestion);
            // Style suggestion item
            if (interRegular != null) {
                itemLabel.setFont(interRegular.deriveFont(14f));
            }
            itemLabel.setForeground(new Color(0x47, 0x55, 0x69)); // Match text color
            
            itemPanel.add(itemLabel, BorderLayout.CENTER);
            
            // Add hover effect
            itemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    itemPanel.setBackground(new Color(0xF0, 0xF7, 0xFF)); // Light blue hover
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    itemPanel.setBackground(Color.WHITE);
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Update search field with the selected suggestion
                    searchField.setText(suggestion);
                    searchField.setForeground(Color.BLACK);
                    
                    // Hide popup after selection
                    SwingUtilities.invokeLater(() -> {
                        suggestionPopup.setVisible(false);
                    });
                }
            });
            
            // Add the panel to the popup
            suggestionPopup.add(itemPanel);
        }
        
        // Configure popup
        suggestionPopup.setFocusable(false); // Don't take focus, so click events work properly
        
        // Show popup below the search field
        try {
            // Account for offset due to the text field being inside nested panels
            int xOffset = (isLeft) ? 0 : 410;
            int yOffset = 25; // Height of labels above search fields
            
            // Show suggestions below the search field
            suggestionPopup.show(this, xOffset, yOffset + searchField.getHeight());
            suggestionPopup.setPopupSize(searchField.getWidth(), Math.min(suggestions.size() * 36, 250));
            
            // Keep popup visible until explicitly closed
            suggestionPopup.setLightWeightPopupEnabled(true);
            // Make sure popup doesn't take focus from the text field
            suggestionPopup.setRequestFocusEnabled(false);
        } catch (Exception e) {
            System.out.println("Error showing suggestion popup: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a search panel with search icon and placeholder text
     */
    private JPanel createSearchPanel(String placeholder) {
        // Create a panel with BorderLayout
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setPreferredSize(new Dimension(400, 45));
        
        // Create a custom search bar with border and icon
        JPanel searchBarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill with white background using rounded corners
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border with color #CBD5E1
                g2d.setColor(searchBorderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                
                // Draw search icon if available
                if (searchIconImage != null) {
                    // Set icon to exactly 20px height
                    int iconHeight = 20;
                    int iconWidth = (int)((double)searchIconImage.getWidth() / searchIconImage.getHeight() * iconHeight);
                    
                    // Draw icon on left side with padding
                    g2d.drawImage(searchIconImage, 15, (getHeight() - iconHeight) / 2, 
                                iconWidth, iconHeight, this);
                }
            }
        };
        searchBarPanel.setLayout(new BorderLayout());
        
        // Create text field
        JTextField textField = new JTextField(placeholder);
        textField.setForeground(Color.GRAY);
        if (interRegular != null) {
            textField.setFont(interRegular.deriveFont(14f));
        } else {
            textField.setFont(new Font("Sans-Serif", Font.PLAIN, 14));
        }
        textField.setOpaque(false);
        textField.setBorder(null);
        
        // Calculate icon width for proper positioning
        int iconWidth = 0;
        if (searchIconImage != null) {
            int iconHeight = 20;
            iconWidth = (int)((double)searchIconImage.getWidth() / searchIconImage.getHeight() * iconHeight);
        }
        
        // Create clear icon label
        JLabel clearIconLabel = new JLabel();
        if (clearIconImage != null) {
            int clearIconHeight = 16;
            int clearIconWidth = (int)((double)clearIconImage.getWidth() / clearIconImage.getHeight() * clearIconHeight);
            
            Image scaledImage = clearIconImage.getScaledInstance(clearIconWidth, clearIconHeight, Image.SCALE_SMOOTH);
            clearIconLabel.setIcon(new ImageIcon(scaledImage));
            clearIconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            clearIconLabel.setVisible(false);
            
            final JTextField finalTextField = textField;
            clearIconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    finalTextField.setText("");
                    finalTextField.requestFocus();
                    clearIconLabel.setVisible(false);
                }
            });
        }
        
        // Add padding panel
        JPanel paddingPanel = new JPanel(new BorderLayout());
        paddingPanel.setOpaque(false);
        paddingPanel.setBorder(BorderFactory.createEmptyBorder(0, 15 + iconWidth + 10, 0, 10));
        paddingPanel.add(textField, BorderLayout.CENTER);
        
        // Add clear icon to the right side with padding
        if (clearIconLabel != null) {
            JPanel clearIconPanel = new JPanel(new BorderLayout());
            clearIconPanel.setOpaque(false);
            clearIconPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
            clearIconPanel.add(clearIconLabel, BorderLayout.CENTER);
            paddingPanel.add(clearIconPanel, BorderLayout.EAST);
        }
        
        // Add components
        searchBarPanel.add(paddingPanel, BorderLayout.CENTER);
        searchPanel.add(searchBarPanel, BorderLayout.CENTER);
        
        // Add focus listener for placeholder
        final String placeholderText = placeholder;
        final JLabel finalClearLabel = clearIconLabel;
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholderText)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
                
                // Show suggestions if there's text
                String text = textField.getText().trim();
                if (!text.isEmpty() && !text.equals(placeholderText)) {
                    // Determine which popup to show
                    JPopupMenu popup = (textField == leftSearchField) ? 
                                      leftSuggestionPopup : rightSuggestionPopup;
                    boolean isLeft = (textField == leftSearchField);
                    
                    // Show suggestions
                    if (popup != null) {
                        updateSuggestions(text, popup, textField, isLeft);
                    }
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                // Don't hide suggestions immediately when clicking on them
                // We'll rely on the click handler to hide when appropriate
                
                // Only update placeholder if text is empty
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholderText);
                    textField.setForeground(Color.GRAY);
                    if (finalClearLabel != null) {
                        finalClearLabel.setVisible(false);
                    }
                }
            }
        });
        
        // Add document listener for text changes
        textField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateClearIcon();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateClearIcon();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateClearIcon();
            }
            
            private void updateClearIcon() {
                if (finalClearLabel != null) {
                    boolean shouldShow = !textField.getText().isEmpty() && 
                                       !textField.getText().equals(placeholderText);
                    finalClearLabel.setVisible(shouldShow);
                }
            }
        });
        
        return searchPanel;
    }
    
    /**
     * Creates a "Compare" button with orange background
     */
    private JPanel createCompareButton(int width, int height) {
        // Create darker shades for hover and click effects
        final Color hoverOrange = new Color(0xE6, 0xA0, 0x30); // Darker shade for hover
        final Color clickOrange = new Color(0xD0, 0x8E, 0x20); // Even darker for click
        
        // Track mouse states
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
                    g2d.setColor(clickOrange);
                } else if (isHovering[0]) {
                    g2d.setColor(hoverOrange);
                } else {
                    g2d.setColor(buttonOrange);
                }
                
                // Draw rounded rectangle
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw "Compare" text in white, centered
                g2d.setColor(Color.WHITE);
                
                // Use Inter Medium font if available
                Font compareFont = interMedium != null ? 
                    interMedium.deriveFont(16f) : 
                    new Font("Sans-Serif", Font.BOLD, 16);
                g2d.setFont(compareFont);
                
                // Center the text
                FontMetrics fm = g2d.getFontMetrics();
                String text = "Compare";
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
                isClicking[0] = false;
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
                // Get candidate names from the search fields
                String candidate1 = getLeftCandidateName();
                String candidate2 = getRightCandidateName();
                
                // Call the compare callback
                if (onCompareCallback != null) {
                    onCompareCallback.accept(new String[]{candidate1, candidate2});
                }
                
                System.out.println("Compare button clicked: Comparing " + candidate1 + " and " + candidate2);
            }
        });
        
        return panel;
    }
    
    /**
     * Load the search icon image from resources
     */
    private void loadSearchIcon() {
        try {
            // Try to load the search icon from multiple paths
            File searchFile = new File("resources/images/Candidate Search/search-icon.png");
            if (searchFile.exists()) {
                searchIconImage = ImageIO.read(searchFile);
                System.out.println("Search icon loaded successfully from: " + searchFile.getAbsolutePath());
            } else {
                // Try alternative locations
                String[] alternativePaths = {
                    "resources/images/search-icon.png",
                    "search-icon.png",
                    "images/search-icon.png",
                    "images/Candidate Search/search-icon.png",
                    "../resources/images/Candidate Search/search-icon.png"
                };
                
                for (String path : alternativePaths) {
                    File altFile = new File(path);
                    if (altFile.exists()) {
                        searchIconImage = ImageIO.read(altFile);
                        System.out.println("Search icon loaded from alternative path: " + altFile.getAbsolutePath());
                        break;
                    }
                }
            }
            
            // If still not found, create a simple search icon
            if (searchIconImage == null) {
                System.out.println("Creating fallback search icon");
                searchIconImage = createFallbackSearchIcon();
            }
        } catch (IOException e) {
            System.out.println("Error loading search icon: " + e.getMessage());
            searchIconImage = createFallbackSearchIcon();
        }
    }
    
    /**
     * Creates a simple search icon as fallback
     */
    private BufferedImage createFallbackSearchIcon() {
        // Create a simple search icon - a magnifying glass
        BufferedImage icon = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a circle for the magnifying glass
        g.setColor(new Color(0x47, 0x55, 0x69));
        g.setStroke(new BasicStroke(2));
        g.drawOval(4, 4, 11, 11);
        
        // Draw the handle
        g.drawLine(14, 14, 19, 19);
        
        g.dispose();
        return icon;
    }
    
    /**
     * Load the clear icon for search fields
     */
    private void loadClearIcon() {
        try {
            // Try to load the clear icon
            File clearFile = new File("resources/images/Candidate Search/clear-icon.png");
            if (clearFile.exists()) {
                clearIconImage = ImageIO.read(clearFile);
            } else {
                // Try alternative location
                clearFile = new File("resources/images/clear-icon.png");
                if (clearFile.exists()) {
                    clearIconImage = ImageIO.read(clearFile);
                } else {
                    clearIconImage = createFallbackClearIcon();
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading clear icon: " + e.getMessage());
            clearIconImage = createFallbackClearIcon();
        }
    }
    
    /**
     * Creates a simple clear icon as fallback
     */
    private BufferedImage createFallbackClearIcon() {
        // Create a simple "X" icon for clearing
        BufferedImage icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw an "X"
        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(2));
        g.drawLine(4, 4, 12, 12);
        g.drawLine(12, 4, 4, 12);
        
        g.dispose();
        return icon;
    }
    
    /**
     * Get the name entered in the left search field
     */
    public String getLeftCandidateName() {
        if (leftSearchField != null) {
            String text = leftSearchField.getText();
            if (text.equals("Search for first candidate...")) {
                return "";
            }
            return text;
        }
        return "";
    }
    
    /**
     * Get the name entered in the right search field
     */
    public String getRightCandidateName() {
        if (rightSearchField != null) {
            String text = rightSearchField.getText();
            if (text.equals("Search for second candidate...")) {
                return "";
            }
            return text;
        }
        return "";
    }
    
    /**
     * Set the candidates to compare
     * @param candidate1 First candidate name
     * @param candidate2 Second candidate name
     */
    public void setCandidates(String candidate1, String candidate2) {
        if (leftSearchField != null && candidate1 != null && !candidate1.isEmpty()) {
            leftSearchField.setText(candidate1);
            leftSearchField.setForeground(Color.BLACK);
        }
        
        if (rightSearchField != null && candidate2 != null && !candidate2.isEmpty()) {
            rightSearchField.setText(candidate2);
            rightSearchField.setForeground(Color.BLACK);
        }
    }
    
    /**
     * Reposition the internal components within this panel
     * @param leftX X position of left search box
     * @param rightX X position of right search box
     * @param buttonX X position of compare button
     * @param y Y position for all components (search boxes and button)
     * @param labelY Y position for labels
     */
    public void repositionComponents(int leftX, int rightX, int buttonX, int y, int labelY) {
        // Update bounds for search panels
        leftSearchPanel.setBounds(leftX, y, 400, 45);
        rightSearchPanel.setBounds(rightX, y, 400, 45);
        compareButton.setBounds(buttonX, y, 120, 45);
        
        // Find and update label positions
        for (Component comp : getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                // Determine which label it is based on position
                if (label.getX() < 200) {
                    // Left label
                    label.setBounds(leftX, labelY, 400, 20);
                } else {
                    // Right label
                    label.setBounds(rightX, labelY, 400, 20);
                }
            }
        }
        
        // Force refresh
        revalidate();
        repaint();
    }
} 