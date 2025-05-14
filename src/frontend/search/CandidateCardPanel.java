package frontend.search;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import backend.model.CandidateDataLoader;

/**
 * A scrollable panel that displays candidate cards in a grid layout.
 * This component handles the layout, spacing, and scrolling of multiple CandidateCard instances.
 */
public class CandidateCardPanel extends JPanel {
    // UI Components
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    
    // Card layout properties
    private int cardsPerRow = 4;
    private int cardWidth = 275;
    private int cardHeight = 94;
    private int horizontalGap = 15;
    private int verticalGap = 15;
    private int topMargin = 0;
    
    // List to store all displayed cards
    private List<CandidateCard> candidateCards = new ArrayList<>();
    
    // Fonts
    private Font interRegular;
    private Font interSemiBold;
    private Font interMedium;
    
    // View profile callback
    private Consumer<String> onViewProfile;
    
    /**
     * Create a scrollable panel that displays candidate cards
     * 
     * @param interRegular Regular font
     * @param interSemiBold SemiBold font
     * @param interMedium Medium font
     * @param onViewProfile Callback when a card is clicked
     */
    public CandidateCardPanel(Font interRegular, Font interSemiBold, Font interMedium, 
                             Consumer<String> onViewProfile) {
        this.interRegular = interRegular;
        this.interSemiBold = interSemiBold;
        this.interMedium = interMedium;
        this.onViewProfile = onViewProfile;
        
        // Initialize UI
        initializeUI();
        
        // Load candidate data
        loadCandidateData();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        // Set up the main panel with BorderLayout
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE); // White background
        
        // Create content panel with null layout for absolute positioning
        contentPanel = new JPanel(null) {
            @Override
            public Dimension getPreferredSize() {
                // Calculate preferred size based on number of cards
                if (candidateCards.isEmpty()) {
                    return new Dimension(800, 200); // Provide a minimum size even when empty
                }
                
                int rows = (int) Math.ceil((double) candidateCards.size() / cardsPerRow);
                int width = cardsPerRow * cardWidth + (cardsPerRow - 1) * horizontalGap;
                int height = rows * cardHeight + (rows - 1) * verticalGap + topMargin;
                
                // Add a bottom margin
                height += 20;
                
                return new Dimension(width, height);
            }
            
            @Override
            public void paint(Graphics g) {
                // Create a buffer for double-buffering to prevent artifacts
                Image offscreen = createImage(getWidth(), getHeight());
                if (offscreen != null) {
                    Graphics offscreenG = offscreen.getGraphics();
                    // Enable anti-aliasing for smoother rendering
                    if (offscreenG instanceof Graphics2D) {
                        Graphics2D g2d = (Graphics2D) offscreenG;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                           RenderingHints.VALUE_ANTIALIAS_ON);
                    }
                    // Paint to the offscreen buffer
                    super.paint(offscreenG);
                    // Draw the buffer to the screen
                    g.drawImage(offscreen, 0, 0, this);
                    offscreenG.dispose();
                } else {
                    // Fallback if offscreen buffer couldn't be created
                    super.paint(g);
                }
            }
        };
        contentPanel.setOpaque(true);
        contentPanel.setBackground(Color.WHITE); // White background
        
        // Create scroll pane with better visibility settings
        scrollPane = new JScrollPane(contentPanel) {
            @Override
            public void paint(Graphics g) {
                // Force full repaint of the viewport during scrolling
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paint(g);
            }
        };
        
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        scrollPane.setBackground(Color.WHITE); // White background
        scrollPane.getViewport().setBackground(Color.WHITE); // White background
        
        // Enable double buffering on the viewport to reduce flickering
        scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
        
        // Ensure horizontal scrollbar is never shown
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Make sure we show vertical scrollbar when needed
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Custom scroll bar for a modern look
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
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
        
        // Add listener to clear hover states when scrolling starts
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (e.getValueIsAdjusting()) {
                // Clear hover states on all cards when scrolling
                for (CandidateCard card : candidateCards) {
                    card.setHovering(false);
                }
                // Force repaint to ensure clean rendering
                contentPanel.repaint();
            }
        });
        
        // Add scroll pane to main panel
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Load candidate data and create cards
     */
    private void loadCandidateData() {
        // Clear existing cards
        candidateCards.clear();
        contentPanel.removeAll();
        
        // Load candidate data from the data loader
        List<CandidateDataLoader.Candidate> candidates = CandidateDataLoader.loadCandidates();
        
        // If no candidates were loaded, use placeholder data
        if (candidates.isEmpty()) {
            System.out.println("Warning: No candidates loaded from file. Using placeholder candidates.");
            // Placeholder data
            Object[][] placeholderData = {
                {"Leni Robredo", "Presidential Candidate", "Liberal Party", "resources/images/candidates/leni_robredo.jpg"},
                {"Bongbong Marcos", "Presidential Candidate", "Partido Federal ng Pilipinas", "resources/images/candidates/bongbong_marcos.jpg"},
                {"Sara Duterte", "Vice Presidential Candidate", "Lakas–CMD", "resources/images/candidates/sara_duterte.jpg"},
                {"Manny Pacquiao", "Presidential Candidate", "PROMDI", "resources/images/candidates/manny_pacquiao.jpg"}
            };
            
            // Create cards from placeholder data
            for (int i = 0; i < placeholderData.length; i++) {
                createCard(
                    (String) placeholderData[i][0],
                    (String) placeholderData[i][1],
                    (String) placeholderData[i][2],
                    (String) placeholderData[i][3],
                    i
                );
            }
        } else {
            // Create cards from loaded candidate data
            for (int i = 0; i < candidates.size(); i++) {
                CandidateDataLoader.Candidate candidate = candidates.get(i);
                createCard(
                    candidate.getName(),
                    candidate.getPosition(),
                    candidate.getParty(),
                    candidate.getImagePath(),
                    i
                );
            }
        }
        
        // Update layout
        updateLayout();
        
        // Repaint the panel
        revalidate();
        repaint();
    }
    
    /**
     * Create a candidate card and add it to the panel
     */
    private void createCard(String name, String position, String party, String imagePath, int index) {
        // Create card
        CandidateCard card = new CandidateCard(
            name,
            position,
            party,
            imagePath,
            interRegular,
            interSemiBold,
            interMedium,
            onViewProfile
        );
        
        // Add our custom hover listener to clear all other hover states
        card.addCustomHoverListener(isHovering -> {
            if (isHovering) {
                // When hovering over one card, ensure other cards are not in hover state
                for (CandidateCard otherCard : candidateCards) {
                    if (otherCard != card) {
                        otherCard.setHovering(false);
                    }
                }
            }
            // Ensure proper rendering after hover state changes
            contentPanel.repaint();
        });
        
        // Add card to list and panel
        candidateCards.add(card);
        contentPanel.add(card);
    }
    
    /**
     * Update the layout of all cards
     */
    private void updateLayout() {
        // Calculate the available width
        int availableWidth = getWidth();
        
        // Adjust cardsPerRow based on available width if needed
        if (availableWidth > 0) {
            int possibleCardsPerRow = Math.max(1, availableWidth / (cardWidth + horizontalGap));
            cardsPerRow = Math.min(possibleCardsPerRow, 4); // Limit to 4 cards per row maximum
        }
        
        // Calculate total row width for centering
        int totalRowWidth = cardsPerRow * cardWidth + (cardsPerRow - 1) * horizontalGap;
        int startX = 0;
        
        // Center cards horizontally if we have space
        if (availableWidth > totalRowWidth) {
            startX = (availableWidth - totalRowWidth) / 2;
        }
        
        // Position all cards in the grid
        for (int i = 0; i < candidateCards.size(); i++) {
            CandidateCard card = candidateCards.get(i);
            
            // Calculate row and column
            int row = i / cardsPerRow;
            int col = i % cardsPerRow;
            
            // Calculate position with centering offset
            int x = startX + col * (cardWidth + horizontalGap);
            int y = topMargin + row * (cardHeight + verticalGap);
            
            // Set card bounds
            card.setBounds(x, y, cardWidth, cardHeight);
        }
        
        // Update content panel size
        contentPanel.revalidate();
        
        // Debug visibility - print out current state
        debugLayout();
    }
    
    /**
     * Debug the layout of the card panel
     */
    private void debugLayout() {
        System.out.println("CandidateCardPanel Debug Info:");
        System.out.println("  - Panel bounds: " + getBounds());
        System.out.println("  - Card count: " + candidateCards.size());
        System.out.println("  - Cards per row: " + cardsPerRow);
        System.out.println("  - Content panel size: " + contentPanel.getPreferredSize());
        System.out.println("  - Scroll pane viewport: " + scrollPane.getViewport().getViewSize());
        
        // Check if all cards are visible
        if (!candidateCards.isEmpty()) {
            CandidateCard firstCard = candidateCards.get(0);
            System.out.println("  - First card bounds: " + firstCard.getBounds());
            
            if (candidateCards.size() > 1) {
                CandidateCard lastCard = candidateCards.get(candidateCards.size() - 1);
                System.out.println("  - Last card bounds: " + lastCard.getBounds());
            }
        }
    }
    
    /**
     * Override paintComponent to help with visibility debugging
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw a subtle background to help with visibility debugging
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fill with very light background - now using 15% opacity instead of 30%
        g2d.setColor(new Color(255, 255, 255, 15));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw subtle border - reduced opacity from 50% to 20%
        g2d.setColor(new Color(200, 210, 230, 20)); // Light blue-gray with reduced opacity
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(0, 0, getWidth()-1, getHeight()-1);
        
        g2d.dispose();
    }
    
    /**
     * Set the cards per row
     */
    public void setCardsPerRow(int cardsPerRow) {
        this.cardsPerRow = Math.max(1, cardsPerRow);
        updateLayout();
    }
    
    /**
     * Set the top margin
     */
    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
        updateLayout();
    }
    
    /**
     * Filter the displayed cards based on a search query
     */
    public void filterCards(String query) {
        // This would implement search/filter functionality
        // For now, just reset the view
        loadCandidateData();
    }
    
    /**
     * Filter the displayed cards based on a province
     */
    public void filterByProvince(String province) {
        // This would filter cards by province
        // For now, just reset the view
        loadCandidateData();
    }
    
    /**
     * Handle component resize events
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        
        // Ensure the scroll pane fills our space
        if (scrollPane != null) {
            scrollPane.setBounds(0, 0, width, height);
        }
        
        // Update card layout based on new width
        updateLayout();
        
        // Force revalidation to ensure content is displayed
        revalidate();
        repaint();
    }

    /**
     * Make the panel visible in the parent container
     */
    @Override
    public void addNotify() {
        super.addNotify();
        
        // When added to a parent container, ensure visibility
        SwingUtilities.invokeLater(() -> {
            // Force layout update
            updateLayout();
            revalidate();
            repaint();
        });
    }
    
    /**
     * Set the background opacity level
     * @param opacity Value between 0.0 (fully transparent) and 1.0 (fully opaque)
     */
    public void setBackgroundOpacity(float opacity) {
        // Ensure opacity is between 0 and 1
        opacity = Math.max(0.0f, Math.min(1.0f, opacity));
        
        // Create a background color with the desired opacity
        int alpha = (int)(opacity * 255);
        setBackground(new Color(240, 245, 250, alpha));
        
        // Also update content panel background with the same opacity
        if (contentPanel != null) {
            contentPanel.setBackground(new Color(240, 245, 250, alpha));
        }
        
        // Force repaint to show changes
        repaint();
    }
} 