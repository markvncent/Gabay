import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.function.Consumer;

/**
 * A modular filter dropdown component that can be reused across the application.
 * This class encapsulates the filter rectangle and its dropdown functionality.
 */
public class FilterDropdown {
    // UI Components
    private JPanel filterRectangle;
    private JPanel dropdownPanel;
    private JPanel containerPanel;
    
    // State variables
    private boolean isDropdownVisible = false;
    private int currentDropdownHeight = 0;
    private int dropdownTargetHeight = 208;
    private final int ANIMATION_DURATION = 60; // Reduced for even faster animation
    private final int ANIMATION_FRAMES = 8; // Reduced for smoother and faster animation
    private Timer dropdownAnimationTimer;
    
    // Styling properties
    private final Color primaryBlue = new Color(0x2B, 0x37, 0x80); // #2B3780
    private final Color hoverBlue = new Color(0x22, 0x2C, 0x66); // Darker blue for hover
    private final int FILTER_CORNER_RADIUS = 10; // Increased from 5 to 10
    private final int DROPDOWN_CORNER_RADIUS = 10; // Increased from 5 to 10
    
    // Selection callback
    private Consumer<String> onSelectionChanged;
    
    // Fonts
    private Font interMedium;
    private Font interRegular;
    
    // Currently selected filter option
    private String selectedFilter = null;
    private int selectedIndex = -1;
    
    /**
     * Creates a new filter dropdown component
     * 
     * @param containerPanel The panel that will contain this component
     * @param interMedium The Inter Medium font
     * @param interRegular The Inter Regular font
     * @param onSelectionChanged Callback when selection changes
     */
    public FilterDropdown(JPanel containerPanel, Font interMedium, Font interRegular, Consumer<String> onSelectionChanged) {
        this.containerPanel = containerPanel;
        this.interMedium = interMedium;
        this.interRegular = interRegular;
        this.onSelectionChanged = onSelectionChanged;
        
        createFilterRectangle();
    }
    
    /**
     * Creates the filter rectangle with blue background, text and arrow icons
     */
    private void createFilterRectangle() {
        // Load the arrow down image
        BufferedImage arrowDownImage = loadArrowImage();
        final BufferedImage finalArrowImage = arrowDownImage;
        
        // Track hover state
        final boolean[] isHovering = new boolean[1];
        
        filterRectangle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill with #2B3780 color - darken when hovering
                if (isHovering[0]) {
                    // Darker shade when hovering (80% brightness)
                    g2d.setColor(hoverBlue);
                } else {
                    g2d.setColor(primaryBlue);
                }
                
                // Draw rounded rectangle instead of regular rectangle
                RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), FILTER_CORNER_RADIUS, FILTER_CORNER_RADIUS);
                g2d.fill(roundedRect);
                
                // Draw "Filter Search" text in white on the far left
                g2d.setColor(Color.WHITE);
                
                // Calculate font size based on rectangle height
                float fontSize = Math.min(16f, getHeight() * 0.35f);
                
                // Set font - use interMedium if available (with default letter spacing)
                if (interMedium != null) {
                    g2d.setFont(interMedium.deriveFont(fontSize));
                } else {
                    g2d.setFont(new Font("Sans-Serif", Font.PLAIN, (int)fontSize));
                }
                
                // Position text on far left with padding
                String text = selectedFilter != null ? selectedFilter : "Filter Search";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int padding = 10;
                int textX = padding;
                int textY = (getHeight() - textHeight) / 2 + fm.getAscent();
                
                g2d.drawString(text, textX, textY);
                
                // Draw arrow down image on far right
                if (finalArrowImage != null) {
                    int imageSize = Math.min(getHeight() - 10, 20); // Limit size
                    int imageX = getWidth() - imageSize - padding;
                    int imageY = (getHeight() - imageSize) / 2;
                    g2d.drawImage(finalArrowImage, imageX, imageY, imageSize, imageSize, null);
                } else {
                    // Fallback: draw a simple arrow if image couldn't be loaded
                    int arrowSize = Math.max(6, Math.min(10, getHeight() / 4));
                    int arrowX = getWidth() - arrowSize - padding;
                    int arrowY = getHeight() / 2;
                    drawArrowDown(g2d, arrowX, arrowY, arrowSize);
                }
            }
            
            private void drawArrowDown(Graphics2D g2d, int x, int y, int arrowSize) {
                int[] xPoints = {x, x + arrowSize, x + arrowSize/2};
                int[] yPoints = {y - arrowSize/2, y - arrowSize/2, y + arrowSize/2};
                g2d.fillPolygon(xPoints, yPoints, 3);
            }
        };
        
        // Add hover effect to darken the rectangle
        filterRectangle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovering[0] = true;
                filterRectangle.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovering[0] = false;
                filterRectangle.repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleDropdown();
            }
        });
        
        // Make the entire panel clickable
        filterRectangle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        filterRectangle.setPreferredSize(new Dimension(217, 45));
        filterRectangle.setOpaque(false);
    }
    
    /**
     * Load the arrow down image from resources
     */
    private BufferedImage loadArrowImage() {
        BufferedImage arrowDownImage = null;
        try {
            File arrowFile = new File("resources/images/candidate search/arrow_down.png");
            if (arrowFile.exists()) {
                arrowDownImage = ImageIO.read(arrowFile);
            } else {
                // Try alternative locations
                String[] alternativePaths = {
                    "resources/images/candidate search/arrow_down.png",
                    "resources/images/Candidate Search/arrow_down.png",
                    "resources/images/arrow_down.png",
                    "arrow_down.png"
                };
                
                for (String path : alternativePaths) {
                    File altFile = new File(path);
                    if (altFile.exists()) {
                        arrowDownImage = ImageIO.read(altFile);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading arrow down image: " + e.getMessage());
        }
        return arrowDownImage;
    }
    
    /**
     * Toggle the dropdown visibility with animation
     */
    public void toggleDropdown() {
        if (dropdownAnimationTimer != null && dropdownAnimationTimer.isRunning()) {
            dropdownAnimationTimer.stop();
        }
        
        isDropdownVisible = !isDropdownVisible;
        
        // Remove any existing dropdown
        if (dropdownPanel != null) {
            containerPanel.remove(dropdownPanel);
            dropdownPanel = null;
        }
        
        if (isDropdownVisible) {
            // Position it under the filter rectangle
            final Rectangle bounds = filterRectangle.getBounds();
            
            // Calculate dynamic height based on subsections
            int numSubsections = 4; // Reduced from 6 to 4 (removed Laws/Bill and Slogan)
            int subsectionHeight = 32; // Height of each subsection
            int subsectionSpacing = 2; // Spacing between subsections
            int topPadding = 16; // Top padding
            int bottomPadding = 16; // Bottom padding
            
            // Calculate total dropdown height
            int dynamicHeight = topPadding + (numSubsections * subsectionHeight) + 
                               ((numSubsections - 1) * subsectionSpacing) + bottomPadding;
            
            // Shadow offsets - reduced for smaller shadows
            int shadowSize = 4; // Reduced to match paintComponent
            int shadowOffset = 3; // Reduced to match paintComponent
            
            // Extra width to account for shadows (shadows appear on both sides)
            int extraWidth = shadowSize * 2;
            int extraHeight = shadowSize * 2 + shadowOffset;
            
            // Create new dropdown panel with width and height that includes shadow space
            dropdownPanel = createDropdownPanel(200 + extraWidth, dynamicHeight + extraHeight);
            dropdownPanel.setName("dropdown");
            
            // Center dropdown under the filter rectangle
            int centerX = bounds.x + (bounds.width / 2);
            int dropdownX = centerX - ((200 + extraWidth) / 2);
            int dropdownY = bounds.y + bounds.height;
            
            // Set initial position with 0 height for animation, centered under button
            // Position needs to account for shadow size to avoid visual jumping
            dropdownPanel.setBounds(dropdownX, dropdownY, 200 + extraWidth, 0);
            
            // Add to container and ensure it's at the top of the z-order
            containerPanel.add(dropdownPanel);
            containerPanel.setComponentZOrder(dropdownPanel, 0);
            
            currentDropdownHeight = 0;
            
            // Update target height for animation
            dropdownTargetHeight = dynamicHeight + extraHeight;
            
            // Start animation
            dropdownAnimationTimer = new Timer(ANIMATION_DURATION / ANIMATION_FRAMES, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentDropdownHeight += dropdownTargetHeight / ANIMATION_FRAMES;
                    if (currentDropdownHeight >= dropdownTargetHeight) {
                        currentDropdownHeight = dropdownTargetHeight;
                        dropdownAnimationTimer.stop();
                        
                        // Ensure the dropdown stays at the top of the z-order after animation completes
                        containerPanel.setComponentZOrder(dropdownPanel, 0);
                    }
                    
                    // Get the current position of the filter rectangle in case it moved
                    Rectangle currentBounds = filterRectangle.getBounds();
                    int currentCenterX = currentBounds.x + (currentBounds.width / 2);
                    int currentDropdownX = currentCenterX - ((200 + extraWidth) / 2);
                    
                    // Keep dropdown centered during animation
                    dropdownPanel.setBounds(currentDropdownX, currentBounds.y + currentBounds.height, 
                                         200 + extraWidth, currentDropdownHeight);
                    containerPanel.repaint();
                }
            });
            dropdownAnimationTimer.start();
        }
        
        // Force a new layer for the dropdown to ensure it appears above other components
        if (dropdownPanel != null) {
            // Set the layer property to ensure it's rendered above other components
            containerPanel.setComponentZOrder(dropdownPanel, 0);
            
            // Request focus to ensure it gets keyboard events
            dropdownPanel.requestFocus();
        }
    }
    
    /**
     * Creates the dropdown panel with shadow and subsections
     */
    private JPanel createDropdownPanel(int width, int height) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Reduced shadow parameters (70% less blur)
                int shadowSize = 4; // Reduced from 15 to 4 (about 70% reduction)
                int shadowOffset = 3; // Adjusted to match the reduced blur
                float shadowOpacity = 0.15f; // Keeping the same opacity
                
                // Draw shadow first with smaller blur
                for (int i = 0; i < shadowSize; i++) {
                    float opacity = shadowOpacity * (1 - (float)i / shadowSize);
                    g2d.setColor(new Color(0, 0, 0, (int)(255 * opacity)));
                    
                    // Create smaller shadow with downward offset
                    g2d.fill(new RoundRectangle2D.Double(
                        shadowSize - i, 
                        shadowSize - i + shadowOffset, 
                        getWidth() - 2 * (shadowSize - i), 
                        getHeight() - 2 * (shadowSize - i),
                        DROPDOWN_CORNER_RADIUS, DROPDOWN_CORNER_RADIUS
                    ));
                }
                
                // Draw white background with rounded corners
                g2d.setColor(Color.WHITE);
                g2d.fill(new RoundRectangle2D.Float(
                    shadowSize, shadowSize, 
                    getWidth() - 2 * shadowSize, 
                    getHeight() - 2 * shadowSize,
                    DROPDOWN_CORNER_RADIUS, DROPDOWN_CORNER_RADIUS
                ));
            }
        };
        
        panel.setLayout(null);
        panel.setOpaque(false);
        
        // Add the subsections to the dropdown panel
        addDropdownSubsections(panel);
        
        return panel;
    }
    
    /**
     * Adds the subsections to the dropdown panel
     */
    private void addDropdownSubsections(JPanel dropdownPanel) {
        // Define the subsection properties
        final int SUBSECTION_WIDTH = 168; // Reduced from 193 to match the narrower dropdown width
        final int SUBSECTION_HEIGHT = 32;
        final int CORNER_RADIUS = 10; // Corner radius for rounded rectangles
        final int PADDING_LEFT = 16; // Left padding for text
        final int CHECKMARK_PADDING = 16; // Padding for checkmark from left
        final int TEXT_PADDING = 40; // Padding for text (after checkmark)
        final Color HOVER_COLOR = new Color(0xF1, 0xF5, 0xF9); // #F1F5F9 - now used for hover
        final Color DEFAULT_BG_COLOR = Color.WHITE; // White background by default
        final Color TEXT_COLOR = new Color(0x33, 0x41, 0x55); // #334155
        
        // Shadow parameters - reduced to match createDropdownPanel
        final int SHADOW_SIZE = 4;
        final int SHADOW_OFFSET = 3;
        
        // Define the subsection labels - removed "Slogan" and "Law/Bill"
        String[] labels = {"Name", "Partylist", "Issue", "Position"};
        
        // Load checkmark image
        BufferedImage checkmarkImage = loadCheckmarkImage();
        final BufferedImage finalCheckmarkImage = checkmarkImage;
        
        // Calculate checkmark dimensions while preserving aspect ratio
        final int checkmarkTargetHeight = 14; // Increased from 11 to 14 to better match text size
        final int[] checkmarkDimensions = calculateCheckmarkDimensions(finalCheckmarkImage, checkmarkTargetHeight);
        final int checkmarkWidth = checkmarkDimensions[0];
        final int checkmarkHeight = checkmarkDimensions[1];
        
        // Calculate starting position (centered in dropdown, accounting for shadow)
        int startX = ((dropdownPanel.getWidth() - (SHADOW_SIZE * 2)) - SUBSECTION_WIDTH) / 2 + SHADOW_SIZE;
        if (startX < SHADOW_SIZE) startX = 16 + SHADOW_SIZE; // Fallback with padding if panel width is not yet set
        
        int startY = 16 + SHADOW_SIZE; // Start 16px from the top, plus shadow size
        int spacing = 2; // 2px spacing between subsections
        
        // Create array to hold all subsection panels
        final JPanel[] subsectionPanels = new JPanel[labels.length];
        
        // Create and add each subsection
        for (int i = 0; i < labels.length; i++) {
            final String label = labels[i];
            final int index = i;
            final boolean[] isHovering = {false}; // Track hover state
            
            JPanel subsection = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Fill background with rounded corners
                    if (isHovering[0]) {
                        g2d.setColor(HOVER_COLOR);
                    } else {
                        g2d.setColor(DEFAULT_BG_COLOR);
                    }
                    
                    // Create rounded rectangle for background
                    RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
                        0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                    g2d.fill(roundedRect);
                    
                    // Draw checkmark if this is the selected item
                    if (selectedIndex == index && finalCheckmarkImage != null) {
                        int checkX = CHECKMARK_PADDING;
                        int checkY = (getHeight() - checkmarkHeight) / 2;
                        g2d.drawImage(finalCheckmarkImage, checkX, checkY, checkmarkWidth, checkmarkHeight, null);
                    }
                    
                    // Draw text (moved to the right to make room for checkmark)
                    g2d.setColor(TEXT_COLOR);
                    
                    // Set font to Inter Medium 14pt
                    Font textFont = interMedium;
                    if (textFont != null) {
                        textFont = textFont.deriveFont(14f);
                    } else {
                        textFont = new Font("Sans-Serif", Font.PLAIN, 14);
                    }
                    g2d.setFont(textFont);
                    
                    FontMetrics fm = g2d.getFontMetrics();
                    int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    
                    g2d.drawString(label, TEXT_PADDING, textY);
                }
            };
            
            // Set position and size
            subsection.setBounds(startX, startY + (SUBSECTION_HEIGHT + spacing) * i, SUBSECTION_WIDTH, SUBSECTION_HEIGHT);
            subsection.setOpaque(false);
            
            // Store reference to this panel
            subsectionPanels[i] = subsection;
            
            // Add hover and click listeners
            subsection.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // If this item is already selected, deselect it
                    if (selectedIndex == index) {
                        selectedIndex = -1;
                        selectedFilter = null;
                    } else {
                        // Otherwise select this item
                        selectedIndex = index;
                        selectedFilter = label;
                    }
                    
                    // Notify the callback if provided
                    if (onSelectionChanged != null) {
                        onSelectionChanged.accept(selectedFilter);
                    }
                    
                    // Update the filter rectangle text to show the selection
                    filterRectangle.repaint();
                    
                    // Repaint all subsections to update the checkmarks
                    for (JPanel panel : subsectionPanels) {
                        panel.repaint();
                    }
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovering[0] = true;
                    subsection.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    subsection.repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovering[0] = false;
                    subsection.repaint();
                }
            });
            
            // Add to dropdown panel
            dropdownPanel.add(subsection);
        }
    }
    
    /**
     * Calculate checkmark dimensions while preserving aspect ratio
     * @param image The checkmark image
     * @param targetHeight The target height for the checkmark
     * @return An array containing [width, height]
     */
    private int[] calculateCheckmarkDimensions(BufferedImage image, int targetHeight) {
        if (image == null) {
            // Default dimensions if image is not available
            return new int[] {11, 11};
        }
        
        // Get original dimensions
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        
        // Calculate new width to maintain aspect ratio
        int newWidth = (int)((double)originalWidth / originalHeight * targetHeight);
        
        // Return the new dimensions
        return new int[] {newWidth, targetHeight};
    }
    
    /**
     * Load the checkmark image from resources
     */
    private BufferedImage loadCheckmarkImage() {
        BufferedImage checkmarkImage = null;
        try {
            File checkmarkFile = new File("resources/images/candidate search/checkmark.png");
            if (checkmarkFile.exists()) {
                checkmarkImage = ImageIO.read(checkmarkFile);
            } else {
                // Try alternative paths
                String[] alternativePaths = {
                    "resources/images/candidate search/checkmark.png",
                    "resources/images/Candidate Search/checkmark.png",
                    "resources/images/checkmark.png"
                };
                
                for (String path : alternativePaths) {
                    File altFile = new File(path);
                    if (altFile.exists()) {
                        checkmarkImage = ImageIO.read(altFile);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading checkmark image: " + e.getMessage());
        }
        return checkmarkImage;
    }
    
    /**
     * Update the position of the dropdown when the container is resized
     */
    public void updatePositionOnResize() {
        if (dropdownPanel != null && isDropdownVisible) {
            Rectangle bounds = filterRectangle.getBounds();
            int centerX = bounds.x + (bounds.width / 2);
            int dropdownX = centerX - (200 / 2);
            
            dropdownPanel.setBounds(dropdownX, bounds.y + bounds.height, 
                                  200, dropdownPanel.getHeight());
        }
    }
    
    /**
     * Get the filter rectangle panel
     */
    public JPanel getFilterRectangle() {
        return filterRectangle;
    }
    
    /**
     * Get the currently selected filter
     */
    public String getSelectedFilter() {
        return selectedFilter;
    }
    
    /**
     * Close the dropdown if it's open
     */
    public void closeDropdown() {
        if (isDropdownVisible) {
            toggleDropdown();
        }
    }
} 