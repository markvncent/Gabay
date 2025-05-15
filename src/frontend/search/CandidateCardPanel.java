package frontend.search;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import backend.model.CandidateDataLoader;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

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
    
    // Track if any card is currently being hovered
    private boolean isAnyCardHovered = false;
    
    // Track if the scrollbar is being interacted with
    private boolean isScrollbarBeingUsed = false;
    
    // Store the original unfiltered candidates
    private List<CandidateDataLoader.Candidate> allCandidates = new ArrayList<>();
    // Store the currently filtered candidates
    private List<CandidateDataLoader.Candidate> filteredCandidates = new ArrayList<>();
    // Current filter values
    private String currentSearchQuery = "";
    private String currentProvince = "";
    
    // Class level variable for tracking scrollbar adjustment state
    private boolean isAdjusting = false;
    
    // Add a field for the selected filter type
    private String currentFilterType = "Name"; // Default to "Name" filter
    
    // Add a cache for issue filtering results
    private Map<String, Set<CandidateDataLoader.Candidate>> issueFilterCache = new HashMap<>();
    
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
        setOpaque(false); // Make panel completely transparent
        setBackground(new Color(0, 0, 0, 0)); // Transparent background
        
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
            protected void paintComponent(Graphics g) {
                // Completely transparent - don't call super.paintComponent
                // This avoids any potential background filling
            }
            
            @Override
            public void paint(Graphics g) {
                // Enable higher quality rendering only when not scrolling to improve performance
                Graphics2D g2d = (Graphics2D)g;
                if (!isAdjusting) {
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                }
                
                // Paint only the children, not the background
                paintChildren(g);
            }
        };
        contentPanel.setOpaque(false); // Make content panel transparent 
        contentPanel.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        
        // Create scroll pane with better visibility settings
        scrollPane = new JScrollPane(contentPanel) {
            @Override
            protected void paintComponent(Graphics g) {
                // Skip the standard scroll pane background painting
                // Only paint children components
                paintChildren(g);
            }
            
            @Override
            public void paint(Graphics g) {
                // Only paint children without any background
                Graphics2D g2d = (Graphics2D) g;
                if (!isAdjusting) {
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                }
                paintChildren(g);
            }
        };
        
        scrollPane.setOpaque(false); // Transparent scrollpane
        scrollPane.getViewport().setOpaque(false); // Transparent viewport
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        scrollPane.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 0)); // Transparent viewport background
        
        // Enable double buffering on the viewport to reduce flickering
        scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
        
        // Ensure horizontal scrollbar is never shown
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Make sure we show vertical scrollbar when needed
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Custom scroll bar for a modern look - completely transparent track
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(0xCB, 0xD5, 0xE1, 100); // Light gray thumb with transparency
                this.trackColor = new Color(0, 0, 0, 0); // Transparent track
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
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // Do not paint the track at all - completely transparent
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                    return;
                }
                
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Use anti-aliasing only when not scrolling
                if (!isAdjusting) {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                }
                
                // Use rounded rectangle for thumb with smoother corners
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, 
                                thumbBounds.width, thumbBounds.height, 8, 8);
                g2.dispose();
            }
        });
        
        // Track scrolling state
        isAdjusting = false;
        
        // Add listener to clear hover states when scrolling starts
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            isAdjusting = e.getValueIsAdjusting();
            
            // Update the global scrolling state in CandidateCard
            CandidateCard.setScrolling(isAdjusting);
            
            if (isAdjusting) {
                // Clear hover states on all cards when scrolling
                for (CandidateCard card : candidateCards) {
                    card.setHovering(false);
                }
            }
            
            // Only repaint when the adjustment is complete
            if (!isAdjusting) {
                contentPanel.repaint();
            }
        });
        
        // Track when scrollbar is being used
        scrollPane.getVerticalScrollBar().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isScrollbarBeingUsed = true;
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isScrollbarBeingUsed = false;
                // Force one repaint after user releases scrollbar
                contentPanel.repaint();
            }
        });
        
        // Optimized wheel listener with throttling
        final long[] lastWheelEvent = new long[1];
        final int THROTTLE_MS = 40; // Throttle wheel events to avoid too frequent updates
        
        scrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // Throttle wheel events to reduce excessive updates
                long now = System.currentTimeMillis();
                if (now - lastWheelEvent[0] < THROTTLE_MS) {
                    return;
                }
                lastWheelEvent[0] = now;
                
                // Allow normal scrolling behavior
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                int direction = e.getWheelRotation();
                int increment = verticalScrollBar.getUnitIncrement() * direction * 3; // Faster scrolling
                
                // Get the current position
                int value = verticalScrollBar.getValue();
                
                // Calculate new position
                int newValue = value + increment;
                
                // Ensure it's within bounds
                newValue = Math.max(0, Math.min(newValue, verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount()));
                
                // Set the new position
                verticalScrollBar.setValue(newValue);
            }
        });
        
        // Track mouse movement over the scrollbar
        scrollPane.getVerticalScrollBar().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                isScrollbarBeingUsed = true;
            }
        });
        
        scrollPane.getVerticalScrollBar().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                isScrollbarBeingUsed = false;
            }
        });
        
        // Add mouse listener to reset hover states when mouse exits the panel
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                // Reset all hover states
                resetAllHoverStates();
            }
        });
        
        // Add the same listener to content panel
        contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                // Check if we really exited (not just entered a child component)
                Point p = e.getPoint();
                if (p.x < 0 || p.y < 0 || p.x >= contentPanel.getWidth() || p.y >= contentPanel.getHeight()) {
                    resetAllHoverStates();
                }
            }
        });
        
        // Add scroll pane to main panel
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Reset all hover states on cards
     */
    private void resetAllHoverStates() {
        isAnyCardHovered = false;
        
        for (CandidateCard card : candidateCards) {
            card.setHovering(false);
        }
        
        // Force repaint to ensure clean rendering
        contentPanel.repaint();
    }
    
    /**
     * Load candidate data and create cards
     */
    private void loadCandidateData() {
        // Clear existing cards
        candidateCards.clear();
        contentPanel.removeAll();
        
        // Load candidate data from the data loader
        allCandidates = CandidateDataLoader.loadCandidates();
        filteredCandidates = new ArrayList<>(allCandidates);
        
        // If no candidates were loaded, use placeholder data
        if (allCandidates.isEmpty()) {
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
            for (int i = 0; i < filteredCandidates.size(); i++) {
                CandidateDataLoader.Candidate candidate = filteredCandidates.get(i);
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
                // Set flag that a card is being hovered
                isAnyCardHovered = true;
            } else {
                // Check if any other card is still being hovered
                boolean anyOtherCardHovered = false;
                for (CandidateCard otherCard : candidateCards) {
                    if (otherCard.isHovering()) {
                        anyOtherCardHovered = true;
                        break;
                    }
                }
                // Update the flag
                isAnyCardHovered = anyOtherCardHovered;
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
        
        // No background or border - completely transparent
        // Leave empty to make panel completely transparent
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
        // Save the current search query
        this.currentSearchQuery = query.toLowerCase().trim();
        
        System.out.println("Filtering by search query: '" + this.currentSearchQuery + "'");
        
        // Apply filters
        applyFilters();
    }
    
    /**
     * Filter the displayed cards based on a province
     */
    public void filterByProvince(String province) {
        // Save the current province filter
        this.currentProvince = province;
        
        System.out.println("Filtering by province/region: '" + this.currentProvince + "'");
        
        // Apply filters
        applyFilters();
    }
    
    /**
     * Safely get a string value that won't be null
     * @param value The string value to check
     * @return The original string or an empty string if null
     */
    private String safeString(String value) {
        return value == null ? "" : value;
    }
    
    /**
     * Apply both search query and province filters
     */
    private void applyFilters() {
        // Clear the filtered list
        filteredCandidates.clear();
        
        // Apply both filters to all candidates
        for (CandidateDataLoader.Candidate candidate : allCandidates) {
            boolean matchesSearch = true;
            boolean matchesProvince = true;
            
            // Apply search filter if we have a query
            if (!currentSearchQuery.isEmpty()) {
                // Process differently based on the filter type
                if ("Issue".equals(currentFilterType)) {
                    // Don't apply empty query filter
                    if (currentSearchQuery.isEmpty()) {
                        matchesSearch = true;
                    } else {
                        // Convert search query to lowercase for case-insensitive comparison
                        String query = currentSearchQuery.toLowerCase();
                        
                        // Debug information
                        System.out.println("Searching for issue: '" + query + "'");
                        
                        // First, specifically check if this is a social stance topic
                        // This gives priority to social stances in the search
                        boolean isMatchingSocialStance = false;
                        
                        // Check if candidate has a stance on this topic
                        isMatchingSocialStance = candidate.hasStanceOnTopic(query);
                        
                        // If no direct match on topic, check if the query is part of any social stance
                        if (!isMatchingSocialStance) {
                            for (String stance : candidate.getSocialStances()) {
                                if (stance.toLowerCase().contains(query)) {
                                    isMatchingSocialStance = true;
                                    break;
                                }
                            }
                        }
                        
                        // Match if there's a matching social stance
                        if (isMatchingSocialStance) {
                            matchesSearch = true;
                        } 
                        // If not found in social stances, use the existing hasStanceOn method
                        else {
                            matchesSearch = candidate.hasStanceOn(query);
                        }
                        
                        // If still no match, check in platforms and other fields
                        if (!matchesSearch) {
                            // Check in platforms (using reflection safely)
                            try {
                                java.lang.reflect.Method getPlatformsMethod = candidate.getClass().getMethod("getPlatforms");
                                Object platformsObj = getPlatformsMethod.invoke(candidate);
                                String platforms = (platformsObj != null) ? platformsObj.toString().toLowerCase() : "";
                                if (!platforms.isEmpty() && containsWordOrPartial(platforms, query)) {
                                    matchesSearch = true;
                                }
                            } catch (NoSuchMethodException e) {
                                // Method doesn't exist, skip this check
                            } catch (Exception e) {
                                // Other exceptions, just log and continue
                                System.out.println("Error checking platforms: " + e.getMessage());
                            }
                            
                            // Check in notable laws if available
                            try {
                                java.lang.reflect.Method getNotableLawsMethod = candidate.getClass().getMethod("getNotableLaws");
                                Object notableLawsObj = getNotableLawsMethod.invoke(candidate);
                                String notableLaws = (notableLawsObj != null) ? notableLawsObj.toString().toLowerCase() : "";
                                if (!notableLaws.isEmpty() && containsWordOrPartial(notableLaws, query)) {
                                    matchesSearch = true;
                                }
                            } catch (NoSuchMethodException e) {
                                // Method doesn't exist, skip this check
                            } catch (Exception e) {
                                // Other exceptions, just log and continue
                                System.out.println("Error checking notable laws: " + e.getMessage());
                            }
                        }
                        
                        // Debug logging
                        if (matchesSearch) {
                            System.out.println("Issue match for candidate: " + candidate.getName());
                            System.out.println("  - Supported Issues: " + safeString(candidate.getSupportedIssues()));
                            System.out.println("  - Opposed Issues: " + safeString(candidate.getOpposedIssues()));
                            
                            // Use the new formatted social stances method
                            System.out.println("  - Social Stances: " + candidate.getFormattedSocialStances());
                            
                            // Print the specific social stance topics
                            System.out.println("  - Social Stance Topics: " + candidate.getSocialStanceTopics());
                            
                            System.out.println("  - Search Query: " + currentSearchQuery);
                            System.out.println("  - Related Issues: " + CandidateDataLoader.getRelatedIssues(query));
                        }
                    }
                } else if ("Name".equals(currentFilterType)) {
                    // Check only in candidate's name
                    String candidateName = safeString(candidate.getName()).toLowerCase();
                    
                    // Check if the query matches the full name or just the first name
                    String[] nameParts = candidateName.split(" ");
                    boolean nameMatches = candidateName.contains(currentSearchQuery.toLowerCase());
                    
                    // Check if query matches first name only
                    if (!nameMatches && nameParts.length > 0) {
                        nameMatches = nameParts[0].contains(currentSearchQuery.toLowerCase());
                    }
                    
                    matchesSearch = nameMatches;
                } else if ("Partylist".equals(currentFilterType)) {
                    // Check only in candidate's party
                    String candidateParty = safeString(candidate.getParty()).toLowerCase();
                    matchesSearch = candidateParty.contains(currentSearchQuery.toLowerCase());
                } else if ("Position".equals(currentFilterType)) {
                    // Check only in candidate's position
                    String candidatePosition = safeString(candidate.getPosition()).toLowerCase();
                    matchesSearch = candidatePosition.contains(currentSearchQuery.toLowerCase());
                } else {
                    // Default case (search across all fields if filter type not recognized)
                    String candidateName = safeString(candidate.getName()).toLowerCase();
                    String candidateParty = safeString(candidate.getParty()).toLowerCase();
                    String candidatePosition = safeString(candidate.getPosition()).toLowerCase();
                    
                    // Check if the query matches the full name or just the first name
                    String[] nameParts = candidateName.split(" ");
                    boolean nameMatches = candidateName.contains(currentSearchQuery.toLowerCase());
                    
                    // Check if query matches first name only
                    if (!nameMatches && nameParts.length > 0) {
                        nameMatches = nameParts[0].contains(currentSearchQuery.toLowerCase());
                    }
                    
                    // Check if the query matches the party or position
                    boolean partyMatches = candidateParty.contains(currentSearchQuery.toLowerCase());
                    boolean positionMatches = candidatePosition.contains(currentSearchQuery.toLowerCase());
                    
                    // Match if any of the fields contain the search query
                    matchesSearch = nameMatches || partyMatches || positionMatches;
                }
            }
            
            // Apply province filter if we have a province (not empty, not "Select Region", not "All")
            if (currentProvince != null && !currentProvince.isEmpty() && 
                !"Select Region".equals(currentProvince) && !"All".equals(currentProvince)) {
                
                String candidateRegion = safeString(candidate.getRegion());
                
                // Skip region check if candidate has no region
                if (!candidateRegion.isEmpty()) {
                    // First try exact match with trimming and case insensitivity
                    String trimmedCandidateRegion = candidateRegion.trim();
                    String trimmedCurrentProvince = currentProvince.trim();
                    
                    if (trimmedCandidateRegion.equalsIgnoreCase(trimmedCurrentProvince)) {
                        matchesProvince = true;
                    } else {
                        // Check for region number match (e.g., "Region IV-A" matches "Region IV-A (CALABARZON)")
                        if (trimmedCurrentProvince.contains(" ")) {
                            String regionNumber = trimmedCurrentProvince.split(" ")[0] + " " + 
                                                  trimmedCurrentProvince.split(" ")[1];
                            if (trimmedCandidateRegion.toLowerCase().contains(regionNumber.toLowerCase())) {
                                matchesProvince = true;
                            } else {
                                // Extract region name from parentheses and check
                                if (trimmedCurrentProvince.contains("(") && trimmedCurrentProvince.contains(")")) {
                                    int startIndex = trimmedCurrentProvince.indexOf("(") + 1;
                                    int endIndex = trimmedCurrentProvince.indexOf(")");
                                    if (startIndex > 0 && endIndex > startIndex) {
                                        String regionName = trimmedCurrentProvince.substring(startIndex, endIndex).trim();
                                        // Check if region name is in candidate region (case insensitive)
                                        matchesProvince = trimmedCandidateRegion.toLowerCase().contains(regionName.toLowerCase()) ||
                                                          regionName.toLowerCase().contains(trimmedCandidateRegion.toLowerCase());
                                    } else {
                                        matchesProvince = false;
                                    }
                                } else {
                                    // Try to match on substrings (e.g., "NCR" matches "National Capital Region")
                                    matchesProvince = trimmedCandidateRegion.toLowerCase().contains(trimmedCurrentProvince.toLowerCase()) ||
                                                      trimmedCurrentProvince.toLowerCase().contains(trimmedCandidateRegion.toLowerCase());
                                }
                            }
                        } else {
                            // For simple region names without spaces, do a case-insensitive contains check
                            matchesProvince = trimmedCandidateRegion.toLowerCase().contains(trimmedCurrentProvince.toLowerCase()) ||
                                              trimmedCurrentProvince.toLowerCase().contains(trimmedCandidateRegion.toLowerCase());
                        }
                    }
                } else {
                    matchesProvince = false;
                }
            }
            
            // Add to filtered list if it matches all active filters
            if (matchesSearch && matchesProvince) {
                filteredCandidates.add(candidate);
            }
        }
        
        // Rebuild the card list based on filtered candidates
        rebuildCards();
        
        // Print debug info about how many candidates matched the filters
        System.out.println("Filter results: " + filteredCandidates.size() + " candidates matched the criteria");
        System.out.println("  - Search query: '" + currentSearchQuery + "'");
        System.out.println("  - Filter type: '" + currentFilterType + "'");
        System.out.println("  - Region filter: '" + currentProvince + "'");
    }
    
    /**
     * Rebuild the UI cards based on the filtered candidate list
     */
    private void rebuildCards() {
        // Clear existing cards from UI
        contentPanel.removeAll();
        candidateCards.clear();
        
        // Create cards from filtered candidate data
        for (int i = 0; i < filteredCandidates.size(); i++) {
            CandidateDataLoader.Candidate candidate = filteredCandidates.get(i);
            createCard(
                candidate.getName(),
                candidate.getPosition(),
                candidate.getParty(),
                candidate.getImagePath(),
                i
            );
        }
        
        // Update layout 
        updateLayout();
        
        // Force revalidation and repaint
        SwingUtilities.invokeLater(() -> {
            contentPanel.revalidate();
            contentPanel.repaint();
            scrollPane.revalidate();
            scrollPane.repaint();
            revalidate();
            repaint();
        });
    }
    
    /**
     * Reset all filters and reload the original data
     */
    public void resetFilters() {
        // Clear all filter values
        currentSearchQuery = "";
        currentProvince = "";
        
        // Clear the issue filter cache
        clearIssueFilterCache();
        
        // Reset filteredCandidates to show all
        filteredCandidates = new ArrayList<>(allCandidates);
        
        // Rebuild the cards
        rebuildCards();
        
        System.out.println("All filters have been reset. Showing " + filteredCandidates.size() + " candidates.");
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
        // Always set to transparent, ignoring the opacity parameter
        setBackground(new Color(0, 0, 0, 0));
        
        // Also update content panel background to transparent
        if (contentPanel != null) {
            contentPanel.setBackground(new Color(0, 0, 0, 0));
        }
        
        // Force repaint to show changes
        repaint();
    }
    
    // Add a setter method for the filter type
    public void setFilterType(String filterType) {
        // Only clear cache if the filter type changes
        if (!this.currentFilterType.equals(filterType)) {
            // Reset the issue filter cache when changing filter types
            clearIssueFilterCache();
            System.out.println("Filter type changed from '" + this.currentFilterType + "' to '" + filterType + "' - cache cleared");
        }
        this.currentFilterType = filterType;
        System.out.println("Filter type set to: " + filterType);
        
        // If we already have a search query, reapply the filter with the new type
        if (!currentSearchQuery.isEmpty() && !currentSearchQuery.equals("Search for candidates or issues...")) {
            // Use SwingUtilities.invokeLater for better UI responsiveness
            SwingUtilities.invokeLater(() -> {
                applyFilters();
            });
        }
    }
    
    // Add method to clear the cache when needed
    public void clearIssueFilterCache() {
        issueFilterCache.clear();
        System.out.println("Issue filter cache cleared");
    }
    
    // Add optimized method to get candidates matching an issue
    private Set<CandidateDataLoader.Candidate> getCandidatesMatchingIssue(String issue) {
        // Check if we have cached results for this issue
        if (issueFilterCache.containsKey(issue)) {
            return issueFilterCache.get(issue);
        }
        
        // If not cached, compute the result
        Set<CandidateDataLoader.Candidate> matchingCandidates = new HashSet<>();
        
        for (CandidateDataLoader.Candidate candidate : allCandidates) {
            if (candidate.hasStanceOn(issue)) {
                matchingCandidates.add(candidate);
            }
        }
        
        // Cache the result for future use
        issueFilterCache.put(issue, matchingCandidates);
        
        return matchingCandidates;
    }
    
    // Add helper method to check for word or partial word matches
    /**
     * Check if a text contains a word or partial word match for query
     * @param text The text to search in
     * @param query The query to look for
     * @return True if the text contains the query as a whole word or partial match
     */
    private boolean containsWordOrPartial(String text, String query) {
        // First try exact match
        if (text.contains(query)) {
            return true;
        }
        
        // For single-word queries, check if any word contains the query
        if (!query.contains(" ")) {
            String[] words = text.split("\\s+");
            for (String word : words) {
                // Remove punctuation
                word = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                if (!word.isEmpty() && (word.contains(query) || query.contains(word))) {
                    return true;
                }
            }
        }
        
        return false;
    }
} 