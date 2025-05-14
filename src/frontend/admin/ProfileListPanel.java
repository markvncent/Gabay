package frontend.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.event.*;
import java.util.function.Consumer;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.RepaintManager;

/**
 * A panel that displays a list of candidate profiles.
 * This component is designed to be embedded in the CandidateDirectoryPanel.
 */
public class ProfileListPanel extends JPanel {
    // Colors
    private final Color bgColor = new Color(0xF8, 0xFA, 0xFC); // #F8FAFC very light blue-gray for profile list
    private final Color borderColor = new Color(0xE2, 0xE8, 0xF0); // #E2E8F0 light gray for profile list border
    private final Color labelColor = new Color(0x64, 0x74, 0x8B); // #64748B slate gray for label
    private final Color placeholderColor = new Color(0x94, 0xA3, 0xB8); // #94A3B8 - lighter text color
    private final Color selectedCardColor = new Color(0xE1, 0xEA, 0xFF); // Light blue for selected card
    private final Color selectedCardBorderColor = new Color(0x2B, 0x37, 0x80); // Dark blue for selected card border
    
    // Corner radius for rounded corners
    private final int CORNER_RADIUS = 8;
    private final int CARD_CORNER_RADIUS = 6;
    
    // Fonts
    private Font interSemiBold;
    private Font interRegular;
    private Font interMedium;
    private Font interBold;
    
    // Content
    private String placeholderText = "No candidates found. Add candidates or modify search.";
    private boolean hasData = false;
    
    // UI Components
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private List<JPanel> candidateCards = new ArrayList<>();
    
    // Selection tracking
    private int selectedCardIndex = -1;
    
    // Profile count listener
    private List<Consumer<Integer>> profileCountListeners = new ArrayList<>();
    
    /**
     * Creates a new ProfileListPanel
     */
    public ProfileListPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        
        // Load fonts
        loadFonts();
        
        // Create content panel
        createContentPanel();
        
        // Note: We no longer automatically load candidate data
        // This will be handled by CandidateDirectoryPanel
    }
    
    /**
     * Add a listener to be notified when the profile count changes
     * @param listener The listener to add
     */
    public void addProfileCountListener(Consumer<Integer> listener) {
        profileCountListeners.add(listener);
        // Notify the listener immediately with the current count
        listener.accept(candidateCards.size());
    }
    
    /**
     * Remove a profile count listener
     * @param listener The listener to remove
     */
    public void removeProfileCountListener(Consumer<Integer> listener) {
        profileCountListeners.remove(listener);
    }
    
    /**
     * Notify all listeners of the current profile count
     */
    private void notifyProfileCountListeners() {
        int count = candidateCards.size();
        for (Consumer<Integer> listener : profileCountListeners) {
            listener.accept(count);
        }
    }
    
    /**
     * Create the content panel with scroll pane
     */
    private void createContentPanel() {
        // Create a panel for the content with vertical BoxLayout
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create scroll pane with custom appearance
        scrollPane = new JScrollPane(contentPanel) {
            @Override
            protected void paintComponent(Graphics g) {
                // Use double buffering to eliminate artifacts
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ensure the background is painted to eliminate ghosts
                g2d.setColor(bgColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Improve scrolling performance and eliminate artifacts
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Faster scrolling
        scrollPane.setWheelScrollingEnabled(true);
        
        // Use BLIT_SCROLL_MODE for best rendering during scrolling
        scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
        
        // Add a paint manager to handle asynchronous repaints with correct visualization
        RepaintManager.currentManager(scrollPane).setDoubleBufferingEnabled(true);
        
        // Customize scroll bar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(0xCB, 0xD5, 0xE1); // Light gray thumb
                this.trackColor = new Color(0xF1, 0xF5, 0xF9); // Even lighter track
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
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                    return;
                }
                
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                   RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Use rounded rectangle for thumb with smoother corners
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, 
                                thumbBounds.width, thumbBounds.height, 8, 8);
                g2.dispose();
            }
        });
        
        // Add adjustment listener to handle clean repaints during scrolling
        verticalScrollBar.addAdjustmentListener(e -> {
            if (e.getValueIsAdjusting()) {
                // Force a clean repaint when scrolling
                contentPanel.invalidate();
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Load required fonts
     */
    private void loadFonts() {
        try {
            // Load Inter fonts
            File interSemiBoldFile = new File("lib/fonts/Inter_18pt-SemiBold.ttf");
            File interRegularFile = new File("lib/fonts/Inter_18pt-Regular.ttf");
            File interMediumFile = new File("lib/fonts/Inter_18pt-Medium.ttf");
            File interBoldFile = new File("lib/fonts/Inter_18pt-Bold.ttf");
            
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
            
            if (interMediumFile.exists()) {
                interMedium = Font.createFont(Font.TRUETYPE_FONT, interMediumFile);
                ge.registerFont(interMedium);
            } else {
                interMedium = new Font("Sans-Serif", Font.PLAIN, 12);
            }
            
            if (interBoldFile.exists()) {
                interBold = Font.createFont(Font.TRUETYPE_FONT, interBoldFile);
                ge.registerFont(interBold);
            } else {
                interBold = new Font("Sans-Serif", Font.BOLD, 12);
            }
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
            interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
            interMedium = new Font("Sans-Serif", Font.PLAIN, 12);
            interBold = new Font("Sans-Serif", Font.BOLD, 12);
        }
    }
    
    /**
     * Load candidate data from the candidates.txt file
     */
    public void loadCandidateData() {
        // Clear existing candidates
        clearCandidates();
        
        try {
            // Path to candidates data file
            String filePath = "resources/data/candidates.txt";
            
            System.out.println("Loading candidates from file: " + filePath);
            
            File candidatesFile = new File(filePath);
            if (!candidatesFile.exists()) {
                System.err.println("Candidate data file not found: " + filePath);
                // We no longer automatically load sample data if file is not found
                return;
            }
            
            // Use a similar approach to CandidateDataLoader
            try (BufferedReader reader = new BufferedReader(new FileReader(candidatesFile))) {
                String line;
                String name = null;
                String position = null;
                String party = null;
                String imagePath = null;
                int candidateCount = 0;
                
                while ((line = reader.readLine()) != null) {
                    // Skip comments and empty lines
                    if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                        continue;
                    }
                    
                    // If we encounter a new "Name:" entry and already have candidate data,
                    // add the previous candidate to the list before starting the new one
                    if (line.startsWith("Name:") && name != null) {
                        // Add the previous candidate
                        addCandidate(name, position, party, imagePath);
                        candidateCount++;
                        
                        // Reset for next candidate
                        name = null;
                        position = null;
                        party = null;
                        imagePath = null;
                    }
                    
                    // Skip social stance lines - they don't represent new candidates
                    if (line.startsWith("Social Stance:")) {
                        continue;
                    }
                    
                    // Parse each line based on prefix
                    if (line.startsWith("Name:")) {
                        name = line.substring("Name:".length()).trim();
                    } else if (line.startsWith("Position:")) {
                        position = line.substring("Position:".length()).trim();
                    } else if (line.startsWith("Positions:")) {
                        position = line.substring("Positions:".length()).trim();
                    } else if (line.startsWith("Running Position:")) {
                        position = line.substring("Running Position:".length()).trim();
                    } else if (line.startsWith("Party Affiliation:")) {
                        party = line.substring("Party Affiliation:".length()).trim();
                    } else if (line.startsWith("Image:")) {
                        imagePath = line.substring("Image:".length()).trim();
                    }
                }
                
                // Add the last candidate if there's data
                if (name != null) {
                    addCandidate(name, position, party, imagePath);
                    candidateCount++;
                }
                
                System.out.println("Found " + candidateCount + " candidates in the file");
                System.out.println("Loaded " + candidateCards.size() + " candidate cards");
            }
            
            // Mark that we have data if we loaded any candidates
            hasData = candidateCards.size() > 0;
        } catch (IOException e) {
            System.err.println("Error loading candidate data: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Notify listeners of the updated count
        notifyProfileCountListeners();
        
        // Update the UI
        refreshUI();
    }
    
    /**
     * Load sample candidate data (used as fallback)
     */
    public void loadSampleData() {
        // Clear existing candidates first
        clearCandidates();
        
        // Sample data - this would be replaced by actual data loading
        addCandidate("John Doe", "Presidential Candidate", "Independent Party", null);
        addCandidate("Jane Smith", "Vice Presidential Candidate", "Progressive Party", null);
        addCandidate("Robert Johnson", "Senatorial Candidate", "Democratic Party", null);
        
        // Mark that we have data
        hasData = candidateCards.size() > 0;
        
        // Notify listeners of the updated count
        notifyProfileCountListeners();
        
        // Update the UI
        refreshUI();
    }
    
    /**
     * Add a candidate to the list
     * @param name The candidate's name
     * @param position The candidate's position
     * @param party The candidate's party
     * @param imagePath Path to the candidate's image
     */
    public void addCandidate(String name, String position, String party, String imagePath) {
        addCandidate(name, position, party, imagePath, null);
    }
    
    /**
     * Add a candidate to the list with a click handler
     * @param name The candidate's name
     * @param position The candidate's position
     * @param party The candidate's party
     * @param imagePath Path to the candidate's image
     * @param clickListener Optional click listener
     */
    public void addCandidate(String name, String position, String party, String imagePath, ActionListener clickListener) {
        // Get the current index for this card
        final int cardIndex = candidateCards.size();
        
        // Card panel with rounded corners
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Use high-quality rendering with double buffering
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // First clear the background to avoid artifacts
                g2d.clearRect(0, 0, getWidth(), getHeight());
                
                // Determine if this card is selected
                boolean isSelected = (cardIndex == selectedCardIndex);
                
                // Fill background with appropriate color based on selection state
                if (isSelected) {
                    g2d.setColor(selectedCardColor);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                
                // Draw rounded rectangle for the card
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CARD_CORNER_RADIUS, CARD_CORNER_RADIUS);
                
                // Draw border with appropriate color
                if (isSelected) {
                    g2d.setColor(selectedCardBorderColor);
                    g2d.setStroke(new BasicStroke(2f));
                } else {
                    g2d.setColor(new Color(0xE2, 0xE8, 0xF0));
                    g2d.setStroke(new BasicStroke(1f));
                }
                
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, CARD_CORNER_RADIUS, CARD_CORNER_RADIUS);
                
                g2d.dispose();
                
                // Let the layout manager handle the components
                super.paintComponent(g);
            }
            
            // Override to ensure consistent rendering
            @Override
            public void repaint() {
                super.repaint();
                // If any avatar panel exists, also repaint it
                for (Component c : getComponents()) {
                    if (c instanceof JPanel) {
                        c.repaint();
                    }
                }
            }
        };
        
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setOpaque(false); // Important for the rounded corners to be visible
        cardPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        
        // Create card content
        JPanel contentPanel = new JPanel(new BorderLayout(15, 0));
        contentPanel.setOpaque(false);
        
        // Left side: Avatar circle
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Clear the area first to avoid ghost artifacts
                g.clearRect(0, 0, getWidth(), getHeight());
                
                // Use high-quality rendering
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Draw light gray background circle
                g2d.setColor(new Color(0xF1, 0xF5, 0xF9));
                g2d.fillOval(0, 0, getWidth(), getHeight());
                
                // Draw initials
                if (name != null && !name.isEmpty()) {
                    g2d.setColor(new Color(0x64, 0x74, 0x8B));
                    String initials = getInitials(name);
                    g2d.setFont(interSemiBold != null ? 
                              interSemiBold.deriveFont(14f) : 
                              new Font("Sans-Serif", Font.BOLD, 14));
                    
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(initials);
                    int textHeight = fm.getHeight();
                    int x = (getWidth() - textWidth) / 2;
                    int y = ((getHeight() - textHeight) / 2) + fm.getAscent();
                    
                    g2d.drawString(initials, x, y);
                }
            }
            
            private String getInitials(String name) {
                StringBuilder initials = new StringBuilder();
                String[] parts = name.split("\\s+");
                
                if (parts.length > 0 && !parts[0].isEmpty()) {
                    initials.append(parts[0].charAt(0));
                }
                
                if (parts.length > 1 && !parts[parts.length - 1].isEmpty()) {
                    initials.append(parts[parts.length - 1].charAt(0));
                }
                
                return initials.toString().toUpperCase();
            }
        };
        
        avatarPanel.setPreferredSize(new Dimension(50, 50));
        avatarPanel.setMaximumSize(new Dimension(50, 50));
        avatarPanel.setMinimumSize(new Dimension(50, 50));
        avatarPanel.setOpaque(false);
        
        // Right side: Text info
        JPanel textInfoPanel = new JPanel();
        textInfoPanel.setLayout(new BoxLayout(textInfoPanel, BoxLayout.Y_AXIS));
        textInfoPanel.setOpaque(false);
        
        // Name label
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(interSemiBold != null ? 
                         interSemiBold.deriveFont(14f) : 
                         new Font("Sans-Serif", Font.BOLD, 14));
        nameLabel.setForeground(new Color(0x0F, 0x17, 0x2A));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Position label
        JLabel positionLabel = new JLabel(position);
        positionLabel.setFont(interRegular != null ? 
                            interRegular.deriveFont(12f) : 
                            new Font("Sans-Serif", Font.PLAIN, 12));
        positionLabel.setForeground(labelColor);
        positionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Party label
        JLabel partyLabel = new JLabel(party);
        partyLabel.setFont(interRegular != null ? 
                          interRegular.deriveFont(11f) : 
                          new Font("Sans-Serif", Font.PLAIN, 11));
        partyLabel.setForeground(new Color(0x94, 0xA3, 0xB8));
        partyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add components to the text panel
        textInfoPanel.add(nameLabel);
        textInfoPanel.add(Box.createVerticalStrut(3));
        textInfoPanel.add(positionLabel);
        textInfoPanel.add(Box.createVerticalStrut(3));
        textInfoPanel.add(partyLabel);
        
        // Add to content panel
        contentPanel.add(avatarPanel, BorderLayout.WEST);
        contentPanel.add(textInfoPanel, BorderLayout.CENTER);
        
        // Add to card panel
        cardPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Set maximum and preferred size
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        cardPanel.setPreferredSize(new Dimension(getWidth() - 20, 80));
        
        // Add hover effect and click behavior
        cardPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (cardIndex != selectedCardIndex) {
                    cardPanel.setBackground(new Color(0xF8, 0xFA, 0xFC));
                }
                cardPanel.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (cardIndex != selectedCardIndex) {
                    cardPanel.setBackground(Color.WHITE);
                }
                cardPanel.repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Update selection state
                setSelectedCardIndex(cardIndex);
                
                // Notify the click listener if provided
                if (clickListener != null) {
                    clickListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "cardClicked"));
                }
            }
        });
        
        // Add to the profile panel
        this.contentPanel.add(cardPanel);
        this.contentPanel.add(Box.createVerticalStrut(8)); // Add spacing between cards
        
        // Add to our candidate cards list
        candidateCards.add(cardPanel);
        
        // Update hasData flag
        hasData = true;
        
        // Notify listeners of updated count
        notifyProfileCountListeners();
        
        // Update UI
        refreshUI();
    }
    
    /**
     * Sets the selected card index and update the UI
     * @param index The index of the selected card
     */
    public void setSelectedCardIndex(int index) {
        // Only update if the selection has changed
        if (selectedCardIndex != index) {
            selectedCardIndex = index;
            
            // Repaint all cards to show the new selection state
            for (JPanel card : candidateCards) {
                card.repaint();
            }
            
            // Ensure complete repaint without artifacts
            RepaintManager.currentManager(this).markCompletelyDirty(this);
            repaint();
        }
    }
    
    /**
     * Get the currently selected card index
     * @return The selected card index, or -1 if no card is selected
     */
    public int getSelectedCardIndex() {
        return selectedCardIndex;
    }
    
    /**
     * Clear all candidates from the list
     */
    public void clearCandidates() {
        contentPanel.removeAll();
        candidateCards.clear();
        
        // Reset selected card index
        selectedCardIndex = -1;
        
        // Reset placeholder
        hasData = false;
        
        // Refresh UI
        refreshUI();
        
        // Notify listeners of the updated count
        notifyProfileCountListeners();
    }
    
    /**
     * Set the placeholder text to display when no profiles are available
     * @param text The placeholder text
     */
    public void setPlaceholderText(String text) {
        this.placeholderText = text;
        repaint();
    }
    
    /**
     * Update the UI based on whether we have data
     */
    private void refreshUI() {
        // Ensure clean rendering by marking the component as completely dirty
        RepaintManager.currentManager(this).markCompletelyDirty(this);
        revalidate();
        repaint();
        
        // Also mark the scroll pane and content panel for complete repainting
        if (scrollPane != null) {
            RepaintManager.currentManager(scrollPane).markCompletelyDirty(scrollPane);
            scrollPane.revalidate();
            scrollPane.repaint();
        }
        
        if (contentPanel != null) {
            RepaintManager.currentManager(contentPanel).markCompletelyDirty(contentPanel);
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Use Graphics2D for better rendering
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw profile list background
        g2d.setColor(bgColor);
        g2d.fillRoundRect(0, 0, width, height, CORNER_RADIUS, CORNER_RADIUS);
        
        // Draw profile list border
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawRoundRect(0, 0, width, height, CORNER_RADIUS, CORNER_RADIUS);
        
        // Draw "profileList" label
        g2d.setColor(labelColor);
        if (interSemiBold != null) {
            g2d.setFont(interSemiBold.deriveFont(14f));
        } else {
            g2d.setFont(new Font("Sans-Serif", Font.BOLD, 14));
        }
        g2d.drawString("profileList", 15, 25);
        
        // Draw placeholder text if there's no data
        if (!hasData) {
            if (interRegular != null) {
                g2d.setFont(interRegular.deriveFont(12f));
            } else {
                g2d.setFont(new Font("Sans-Serif", Font.PLAIN, 12));
            }
            g2d.setColor(placeholderColor);
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(placeholderText);
            int textX = (width - textWidth) / 2; // Center text horizontally
            int textY = height / 2; // Center vertically
            g2d.drawString(placeholderText, textX, textY);
        }
        
        g2d.dispose();
    }
} 