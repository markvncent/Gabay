package frontend.search;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

/**
 * A modular component to display candidate information in a card format.
 * This can be reused and customized throughout the application.
 */
public class CandidateCard extends JPanel {
    // UI Components
    private JPanel mainCardPanel;
    private JPanel imagePanel;
    private JLabel nameLabel;
    private JLabel positionLabel;
    private JLabel partyLabel;
    
    // Candidate data
    private String candidateName;
    private String candidatePosition;
    private String candidateParty;
    private BufferedImage candidateImage = null;
    
    // Styling properties
    private final Color cardBackground = new Color(255, 255, 255);
    private final Color cardBorder = new Color(0xE2, 0xE8, 0xF0); // #E2E8F0
    private final Color textPrimary = new Color(0x0F, 0x17, 0x2A); // #0F172A as requested
    private final Color textSecondary = new Color(0x64, 0x74, 0x8B); // #64748B
    
    // Consistent radius for all rounded components
    private final int CORNER_RADIUS = 8;
    
    // Uniform padding
    private final int CARD_PADDING = 12;
    
    // Animation properties
    private float hoverState = 0.0f; // 0.0 = not hovering, 1.0 = fully hovering
    private boolean hovering = false; // Track if mouse is currently over the card
    private boolean selected = false;
    private Timer animationTimer;
    private final int ANIMATION_DURATION = 150; // milliseconds
    private final int ANIMATION_STEPS = 10;
    
    // Card sizes - just use one fixed size
    private final Dimension CARD_SIZE = new Dimension(275, 94);
    
    // Highlight colors
    private final Color NORMAL_BACKGROUND = new Color(255, 255, 255);
    private final Color HOVER_BACKGROUND = new Color(0xF8, 0xFA, 0xFC); // Light blue-gray highlight
    
    // Fonts
    private Font interRegular;
    private Font interSemiBold;
    private Font interMedium;
    
    // Callback
    private Consumer<String> onViewProfile;
    
    // Hover state listener for external control
    private List<Consumer<Boolean>> hoverListeners = new ArrayList<>();
    
    // Variable to track scrolling state
    private static boolean isScrolling = false;
    
    /**
     * Create a new candidate card
     * 
     * @param candidateName The candidate's name
     * @param candidatePosition The candidate's position (e.g., "Presidential Candidate")
     * @param candidateParty The candidate's political party
     * @param imagePath Path to the candidate's image
     * @param interRegular Regular font
     * @param interSemiBold SemiBold font
     * @param interMedium Medium font
     * @param onViewProfile Callback when View Profile button is clicked (no longer used)
     */
    public CandidateCard(String candidateName, String candidatePosition, String candidateParty, 
                        String imagePath, Font interRegular, Font interSemiBold, Font interMedium,
                        Consumer<String> onViewProfile) {
        this.candidateName = candidateName;
        this.candidatePosition = candidatePosition;
        this.candidateParty = candidateParty;
        this.interRegular = interRegular;
        this.interSemiBold = interSemiBold;
        this.interMedium = interMedium;
        
        // Debug: Print the candidate information received
        System.out.println("--------------------------------------------------------------");
        System.out.println("Creating CandidateCard with details:");
        System.out.println(" - Name: [" + candidateName + "]");
        System.out.println(" - Position: [" + candidatePosition + "]");
        System.out.println(" - Party: [" + candidateParty + "]");
        System.out.println("--------------------------------------------------------------");
        
        // Load candidate image
        loadCandidateImage(imagePath);
        
        // Initialize UI
        initializeUI();
        
        // Set up animation timer
        setupAnimationTimer();
        
        // Add mouse listeners for hover effect
        addHoverListeners();
        
        // Set layer properties to ensure proper z-ordering
        setComponentZOrder();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        // Set up the main panel
        setLayout(new BorderLayout());
        setOpaque(false);
        
        // Create the main card panel with shadow effect
        mainCardPanel = new JPanel() {
            // Use a buffered image for the background to improve performance
            private BufferedImage cachedBackground = null;
            private boolean needsRefresh = true;
            private int lastWidth = 0;
            private int lastHeight = 0;
            private float lastHoverState = -1;
            
            @Override
            public void setBounds(int x, int y, int width, int height) {
                if (width != lastWidth || height != lastHeight) {
                    needsRefresh = true;
                    lastWidth = width;
                    lastHeight = height;
                }
                super.setBounds(x, y, width, height);
            }
            
            /**
             * Pre-render the card background to a buffer
             */
            private void updateCachedBackground() {
                if (getWidth() <= 0 || getHeight() <= 0) return;
                
                cachedBackground = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = cachedBackground.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow first (subtle shadow effect)
                int shadowSize = 4;
                float shadowOpacity = 0.08f;
                
                // Create shadow gradient
                for (int i = 0; i < shadowSize; i++) {
                    float opacity = shadowOpacity * (1 - (float)i / shadowSize);
                    g2d.setColor(new Color(0, 0, 0, (int)(255 * opacity)));
                    g2d.fill(new RoundRectangle2D.Double(
                        shadowSize - i, 
                        shadowSize - i, 
                        getWidth() - 2 * (shadowSize - i), 
                        getHeight() - 2 * (shadowSize - i),
                        CORNER_RADIUS, CORNER_RADIUS
                    ));
                }
                
                // Interpolate background color based on hover state
                Color bgColor = new Color(
                    (int)(NORMAL_BACKGROUND.getRed() + (HOVER_BACKGROUND.getRed() - NORMAL_BACKGROUND.getRed()) * hoverState),
                    (int)(NORMAL_BACKGROUND.getGreen() + (HOVER_BACKGROUND.getGreen() - NORMAL_BACKGROUND.getGreen()) * hoverState),
                    (int)(NORMAL_BACKGROUND.getBlue() + (HOVER_BACKGROUND.getBlue() - NORMAL_BACKGROUND.getBlue()) * hoverState)
                );
                
                // Draw background with rounded corners
                g2d.setColor(bgColor);
                g2d.fill(new RoundRectangle2D.Float(
                    shadowSize, shadowSize, 
                    getWidth() - 2 * shadowSize, 
                    getHeight() - 2 * shadowSize,
                    CORNER_RADIUS, CORNER_RADIUS
                ));
                
                // Draw border - make border darker when hovering
                Color borderColor = new Color(
                    (int)(cardBorder.getRed() + ((cardBorder.getRed() - 40) - cardBorder.getRed()) * hoverState),
                    (int)(cardBorder.getGreen() + ((cardBorder.getGreen() - 40) - cardBorder.getGreen()) * hoverState),
                    (int)(cardBorder.getBlue() + ((cardBorder.getBlue() - 40) - cardBorder.getBlue()) * hoverState)
                );
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(new RoundRectangle2D.Float(
                    shadowSize, shadowSize, 
                    getWidth() - 2 * shadowSize, 
                    getHeight() - 2 * shadowSize,
                    CORNER_RADIUS, CORNER_RADIUS
                ));
                g2d.dispose();
                
                needsRefresh = false;
                lastHoverState = hoverState;
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Check if we need to update the cached background (size changed or hover state changed)
                if (cachedBackground == null || needsRefresh || Math.abs(lastHoverState - hoverState) > 0.01f) {
                    updateCachedBackground();
                }
                
                // Draw the cached background with fast painting
                if (cachedBackground != null) {
                    g.drawImage(cachedBackground, 0, 0, null);
                }
            }
        };
        
        // Set up the card layout - consistent padding on all sides
        mainCardPanel.setLayout(new BorderLayout(12, 0));
        mainCardPanel.setOpaque(false);
        mainCardPanel.setBorder(BorderFactory.createEmptyBorder(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING));
        
        // Create circular image panel with optimizations
        imagePanel = createImagePanel();
        imagePanel.setPreferredSize(new Dimension(60, 60)); // Square size for circular image
        
        // Create info panel with a left alignment
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        // Create name label with specified styling
        nameLabel = new JLabel(candidateName);
        nameLabel.setForeground(textPrimary); // #0F172A as requested
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (interSemiBold != null) {
            nameLabel.setFont(interSemiBold.deriveFont(14f));
        } else {
            nameLabel.setFont(new Font("Sans-Serif", Font.BOLD, 14));
        }
        
        // Create position label with specified styling
        positionLabel = new JLabel(candidatePosition);
        // Use regular font with smaller size for position
        positionLabel.setForeground(textSecondary);
        positionLabel.setBackground(null);
        positionLabel.setOpaque(false);
        positionLabel.setBorder(null);
        positionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (interRegular != null) {
            positionLabel.setFont(interRegular.deriveFont(12f));
        } else {
            positionLabel.setFont(new Font("Sans-Serif", Font.PLAIN, 12));
        }
        
        // Create party label with specified styling - smaller than position text
        partyLabel = new JLabel(candidateParty);
        partyLabel.setForeground(textSecondary); // #64748B as requested
        partyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (interRegular != null) {
            partyLabel.setFont(interRegular.deriveFont(11f)); // Smaller than position
        } else {
            partyLabel.setFont(new Font("Sans-Serif", Font.PLAIN, 11));
        }
        
        // Add components to info panel with spacing
        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3)); // Equal spacing between name and position
        infoPanel.add(positionLabel);
        infoPanel.add(Box.createVerticalStrut(3)); // Equal spacing between position and party
        infoPanel.add(partyLabel);
        infoPanel.add(Box.createVerticalGlue());
        
        // Center info panel vertically
        JPanel infoWrapper = new JPanel(new BorderLayout());
        infoWrapper.setOpaque(false);
        infoWrapper.add(infoPanel, BorderLayout.CENTER);
        
        // Add components to main card panel
        mainCardPanel.add(imagePanel, BorderLayout.WEST);
        mainCardPanel.add(infoWrapper, BorderLayout.CENTER);
        
        // Add main card panel to this panel
        add(mainCardPanel, BorderLayout.CENTER);
        
        // Set fixed size
        setPreferredSize(CARD_SIZE);
    }
    
    /**
     * Set up animation timer for smooth hover transitions
     */
    private void setupAnimationTimer() {
        // Create timer that fires every 16ms (roughly 60fps)
        animationTimer = new Timer(16, e -> {
            // Update hover state based on isHovering flag
            if (hovering && hoverState < 1.0f) {
                hoverState = Math.min(1.0f, hoverState + 0.15f);
                updateFonts();
                repaint();
            } else if (!hovering && hoverState > 0.0f) {
                hoverState = Math.max(0.0f, hoverState - 0.15f);
                updateFonts();
                repaint();
            }
        });
        animationTimer.setRepeats(true);
        animationTimer.start();
    }
    
    // Animation direction: 1 for hover, -1 for un-hover
    private int animationDirection = 0;
    
    /**
     * Update all components based on current hover state
     */
    private void updateComponentsForHoverState() {
        // Interpolate font sizes
        updateFonts();
        
        // Use fixed card size, no resizing
        setPreferredSize(CARD_SIZE);
        
        // Repaint all components
        revalidate();
        repaint();
    }
    
    /**
     * Update font sizes based on hover state
     */
    private void updateFonts() {
        // Name font: 14px normal to 15px on hover - using SemiBold
        if (interSemiBold != null) {
            float nameSize = 14f + (1f * hoverState);
            nameLabel.setFont(interSemiBold.deriveFont(nameSize));
        } else {
            int nameSize = 14 + Math.round(hoverState);
            nameLabel.setFont(new Font("Sans-Serif", Font.BOLD, nameSize));
        }
        
        // Position font: 12px normal to 13px on hover - using Regular
        if (interRegular != null) {
            float positionSize = 12f + (1f * hoverState);
            positionLabel.setFont(interRegular.deriveFont(positionSize));
        } else {
            int positionSize = 12 + Math.round(hoverState);
            positionLabel.setFont(new Font("Sans-Serif", Font.PLAIN, positionSize));
        }
        
        // Party font: 11px normal to 12px on hover - using Regular
        if (interRegular != null) {
            float partySize = 11f + (1f * hoverState);
            partyLabel.setFont(interRegular.deriveFont(partySize));
        } else {
            int partySize = 11 + Math.round(hoverState);
            partyLabel.setFont(new Font("Sans-Serif", Font.PLAIN, partySize));
        }
    }
    
    /**
     * Add hover listeners to create interactive effect
     */
    private void addHoverListeners() {
        MouseAdapter hoverAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Set hovering state to true
                setHovering(true);
                
                // Add a small cursor pointer effect
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                // Ensure this card is on top of other cards, but still behind dropdowns
                Container parent = getParent();
                if (parent != null) {
                    // Find all other CandidateCards and put them behind this one
                    for (Component comp : parent.getComponents()) {
                        if (comp instanceof CandidateCard && comp != CandidateCard.this) {
                            parent.setComponentZOrder((CandidateCard)comp, parent.getComponentCount() - 1);
                        }
                    }
                    
                    // Move this card to be above other cards, but ensure dropdowns stay on top
                    // The optimal position is to be above other cards but below dropdowns
                    // Find components named dropdown or containing dropdown in their name
                    int dropdownIndex = -1;
                    for (int i = 0; i < parent.getComponentCount(); i++) {
                        Component comp = parent.getComponent(i);
                        String compName = comp.getName();
                        if (compName != null && (compName.contains("dropdown") || 
                                               compName.contains("Dropdown"))) {
                            // Remember the position of the first dropdown
                            if (dropdownIndex == -1 || i < dropdownIndex) {
                                dropdownIndex = i;
                            }
                        }
                    }
                    
                    // If a dropdown was found, position this card right below it
                    // Otherwise position it above other cards
                    if (dropdownIndex != -1) {
                        parent.setComponentZOrder(CandidateCard.this, dropdownIndex + 1);
                    } else {
                        parent.setComponentZOrder(CandidateCard.this, 0);
                    }
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Set hovering state to false
                setHovering(false);
                
                // Return cursor to default
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Trigger view profile action
                if (onViewProfile != null) {
                    onViewProfile.accept(candidateName);
                }
            }
        };
        
        // Add listeners to all components
        addMouseListener(hoverAdapter);
        mainCardPanel.addMouseListener(hoverAdapter);
        imagePanel.addMouseListener(hoverAdapter);
    }
    
    /**
     * Set hovering state and animate transition
     */
    public void setHovering(boolean hovering) {
        if (this.hovering != hovering) {
            this.hovering = hovering;
            
            // Stop any existing animation
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }
            
            // Only start animation if component is visible and not in a scrolling operation
            if (isVisible() && isShowing() && !isScrolling) {
                animationDirection = hovering ? 1 : -1;
                animationTimer.start();
            } else {
                // If scrolling or not visible, skip animation and just set final state
                hoverState = hovering ? 1.0f : 0.0f;
                updateComponentsForHoverState();
                mainCardPanel.repaint();
            }
            
            // Notify hover listeners
            for (Consumer<Boolean> listener : hoverListeners) {
                listener.accept(hovering);
            }
        }
    }
    
    /**
     * Check if the card is currently being hovered
     * @return True if the card is being hovered, false otherwise
     */
    public boolean isHovering() {
        return hovering;
    }
    
    /**
     * Add a listener to be notified when hover state changes
     * @param listener Consumer that accepts a boolean indicating hover state
     */
    public void addCustomHoverListener(Consumer<Boolean> listener) {
        if (!hoverListeners.contains(listener)) {
            hoverListeners.add(listener);
        }
    }
    
    /**
     * Remove a hover listener
     * @param listener The listener to remove
     */
    public void removeCustomHoverListener(Consumer<Boolean> listener) {
        hoverListeners.remove(listener);
    }
    
    /**
     * Create circular image panel with placeholder or actual candidate image
     */
    private JPanel createImagePanel() {
        JPanel panel = new JPanel() {
            // Cache for the circular image
            private BufferedImage cachedImage = null;
            private int lastWidth = 0;
            private int lastHeight = 0;
            
            @Override
            public void setBounds(int x, int y, int width, int height) {
                if (width != lastWidth || height != lastHeight) {
                    cachedImage = null;  // Reset cached image when size changes
                    lastWidth = width;
                    lastHeight = height;
                }
                super.setBounds(x, y, width, height);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getWidth() <= 0 || getHeight() <= 0) return;
                
                // Create cached image if needed
                if (cachedImage == null) {
                    cachedImage = createCircularImage();
                }
                
                // Draw the cached image
                g.drawImage(cachedImage, 0, 0, null);
            }
            
            /**
             * Create a circular image for caching
             */
            private BufferedImage createCircularImage() {
                BufferedImage result = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = result.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background as transparent
                g2d.setComposite(AlphaComposite.Clear);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setComposite(AlphaComposite.SrcOver);
                
                int diameter = Math.min(getWidth(), getHeight());
                
                if (candidateImage != null) {
                    // Create a circular clip
                    Shape clip = new java.awt.geom.Ellipse2D.Double(0, 0, diameter, diameter);
                    g2d.setClip(clip);
                    
                    // Calculate scaled image dimensions
                    double scale = Math.max(
                        (double) diameter / candidateImage.getWidth(),
                        (double) diameter / candidateImage.getHeight()
                    );
                    
                    int scaledWidth = (int) (candidateImage.getWidth() * scale);
                    int scaledHeight = (int) (candidateImage.getHeight() * scale);
                    
                    // Draw image centered in the circle
                    int x = (diameter - scaledWidth) / 2;
                    int y = (diameter - scaledHeight) / 2;
                    
                    g2d.drawImage(candidateImage, x, y, scaledWidth, scaledHeight, null);
                    
                    // Reset clip and draw border
                    g2d.setClip(null);
                    g2d.setColor(cardBorder);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.draw(clip);
                } else {
                    // If no image, draw a colored circle with initials
                    g2d.setColor(getColorFromName(candidateName));
                    g2d.fillOval(0, 0, diameter, diameter);
                    
                    // Draw initials
                    String initials = getInitials(candidateName);
                    g2d.setColor(Color.WHITE);
                    
                    // Use a simple font to avoid font loading overhead
                    Font font = new Font(Font.SANS_SERIF, Font.BOLD, diameter / 3);
                    g2d.setFont(font);
                    
                    // Center the text
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(initials);
                    int textHeight = fm.getHeight();
                    int textX = (diameter - textWidth) / 2;
                    int textY = (diameter - textHeight) / 2 + fm.getAscent();
                    
                    g2d.drawString(initials, textX, textY);
                    
                    // Draw border
                    g2d.setColor(cardBorder);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawOval(0, 0, diameter - 1, diameter - 1);
                }
                
                g2d.dispose();
                return result;
            }
        };
        
        panel.setOpaque(false);
        return panel;
    }
    
    /**
     * Load candidate image from path
     */
    private void loadCandidateImage(String imagePath) {
        try {
            // If path is null or empty, use a placeholder
            if (imagePath == null || imagePath.trim().isEmpty()) {
                createPlaceholderImage();
                return;
            }
            
            // Try to load the image
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                candidateImage = ImageIO.read(imageFile);
                System.out.println("Loaded candidate image from: " + imagePath);
            } else {
                System.out.println("Candidate image not found at: " + imagePath);
                createPlaceholderImage();
            }
        } catch (IOException e) {
            System.out.println("Error loading candidate image: " + e.getMessage());
            createPlaceholderImage();
        }
    }
    
    /**
     * Create a placeholder image with the candidate's initials
     */
    private void createPlaceholderImage() {
        System.out.println("Creating placeholder image for: " + candidateName);
        
        // Create a placeholder image with the candidate's initials
        int size = 100; // Larger than needed for better quality when scaled down
        candidateImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = candidateImage.createGraphics();
        
        // Enable anti-aliasing for smoother text
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Create a colored background
        Color bgColor = getColorFromName(candidateName);
        g2d.setColor(bgColor);
        g2d.fillOval(0, 0, size, size);
        
        // Draw the initials in white
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Sans-Serif", Font.BOLD, size / 3));
        
        String initials = getInitials(candidateName);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(initials);
        int textHeight = fm.getHeight();
        
        // Center the text
        int x = (size - textWidth) / 2;
        int y = (size - textHeight) / 2 + fm.getAscent();
        
        g2d.drawString(initials, x, y);
        g2d.dispose();
    }
    
    /**
     * Get initials from a name (up to 2 characters)
     */
    private String getInitials(String name) {
        if (name == null || name.isEmpty()) {
            return "??";
        }
        
        StringBuilder initials = new StringBuilder();
        String[] parts = name.split("\\s+");
        
        // Get first letter of first name
        if (parts.length > 0 && !parts[0].isEmpty()) {
            initials.append(parts[0].charAt(0));
        }
        
        // Get first letter of last name if available
        if (parts.length > 1 && !parts[parts.length - 1].isEmpty()) {
            initials.append(parts[parts.length - 1].charAt(0));
        } else if (initials.length() < 2 && parts[0].length() > 1) {
            // If only one name and initials is still short, use first two letters
            initials.append(parts[0].charAt(1));
        }
        
        return initials.toString().toUpperCase();
    }
    
    /**
     * Generate a consistent color based on the candidate's name
     */
    private Color getColorFromName(String name) {
        if (name == null || name.isEmpty()) {
            return new Color(0x2F, 0x39, 0x8E); // Default blue
        }
        
        // Calculate a hash code for the name
        int hash = name.hashCode();
        
        // Use different prime numbers for each color component
        int r = Math.abs(hash % 256);
        int g = Math.abs((hash / 256) % 256);
        int b = Math.abs((hash / 65536) % 256);
        
        // Ensure the color isn't too light (for white text contrast)
        r = Math.min(r, 180);
        g = Math.min(g, 180);
        b = Math.min(b, 180);
        
        return new Color(r, g, b);
    }
    
    /**
     * Update the candidate data
     */
    public void updateCandidateData(String name, String position, String party, String imagePath) {
        this.candidateName = name;
        this.candidatePosition = position;
        this.candidateParty = party;
        
        // Update UI components
        nameLabel.setText(name);
        positionLabel.setText(position);
        partyLabel.setText(party);
        
        // Load new image
        loadCandidateImage(imagePath);
        
        // Repaint components
        imagePanel.repaint();
        repaint();
    }
    
    /**
     * Get the height of this card
     */
    public int getHeight() {
        return getPreferredSize().height;
    }
    
    /**
     * Set the preferred width of the card
     */
    public void setCardWidth(int width) {
        setPreferredSize(new Dimension(width, getPreferredSize().height));
        revalidate();
    }
    
    /**
     * Set appropriate component z-order properties to ensure proper layering
     */
    private void setComponentZOrder() {
        // Ensure this component has a low z-order value
        // so it doesn't interfere with dropdowns
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane != null) {
            // Add a hierarchy listener to maintain proper z-order when added to container
            addHierarchyListener(e -> {
                if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0) {
                    Container parent = getParent();
                    if (parent != null) {
                        // Move this component to the back of its container
                        // so dropdowns can appear above it
                        parent.setComponentZOrder(this, parent.getComponentCount() - 1);
                    }
                }
            });
        }
    }
    
    /**
     * Override the paintComponent method to use cached images and optimize rendering
     */
    @Override
    public void paintComponent(Graphics g) {
        // Only paint if visible
        if (!isVisible()) return;
        
        super.paintComponent(g);
    }
    
    /**
     * Pause animations during scrolling
     */
    public static void setScrolling(boolean scrolling) {
        isScrolling = scrolling;
        
        // When scrolling starts, immediately stop all animations to save resources
        if (scrolling) {
            // Animation pause is handled by hover state changes
        }
    }
} 