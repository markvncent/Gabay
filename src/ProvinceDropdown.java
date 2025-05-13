import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
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
 * A modular province selector dropdown component based on FilterDropdown.
 * This class encapsulates the province selector rectangle and its dropdown functionality.
 */
public class ProvinceDropdown {
    // UI Components
    private JPanel provinceRectangle;
    private JPanel dropdownPanel;
    private JScrollPane scrollPane;
    private JPanel containerPanel;
    private JPanel scrollContainerPanel; // Added to store the scroll container separately
    
    // List to track all subsection panels
    private List<JPanel> subsectionPanels = new ArrayList<>();
    
    // Hover tracking
    private JPanel hoveredPanel = null;
    private boolean isMouseInDropdown = false;
    private MouseMotionListener dropdownMouseMotionListener;
    private MouseListener dropdownMouseListener;
    
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
    private final Color searchBorderColor = new Color(0xCB, 0xD5, 0xE1); // #CBD5E1 - matching search box
    private final int FILTER_CORNER_RADIUS = 10; // Increased from 5 to 10
    private final int DROPDOWN_CORNER_RADIUS = 10; // Increased from 5 to 10
    
    // Selection callback
    private Consumer<String> onSelectionChanged;
    
    // Fonts
    private Font interMedium;
    private Font interRegular;
    
    // Currently selected province option
    private String selectedProvince = null;
    private int selectedIndex = -1;
    
    // Max height for dropdown (to prevent it from going off-screen)
    private final int MAX_DROPDOWN_HEIGHT = 350;
    
    // Global mouse listener for clicks outside the dropdown
    private AWTEventListener globalMouseListener;
    
    /**
     * Creates a new province dropdown component
     * 
     * @param containerPanel The panel that will contain this component
     * @param interMedium The Inter Medium font
     * @param interRegular The Inter Regular font
     * @param onSelectionChanged Callback when selection changes
     */
    public ProvinceDropdown(JPanel containerPanel, Font interMedium, Font interRegular, Consumer<String> onSelectionChanged) {
        this.containerPanel = containerPanel;
        this.interMedium = interMedium;
        this.interRegular = interRegular;
        this.onSelectionChanged = onSelectionChanged;
        
        createProvinceRectangle();
    }
    
    /**
     * Creates the province rectangle with white background, border and arrow icons
     */
    private void createProvinceRectangle() {
        // Load the arrow down image
        BufferedImage arrowDownImage = loadArrowImage();
        final BufferedImage finalArrowImage = arrowDownImage;
        
        // Track hover state
        final boolean[] isHovering = new boolean[1];
        
        provinceRectangle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill with white background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), FILTER_CORNER_RADIUS, FILTER_CORNER_RADIUS);
                
                // Draw border with color #CBD5E1, thinner 1px width
                g2d.setColor(searchBorderColor);
                g2d.setStroke(new BasicStroke(1)); // Reduced from 2px to 1px
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, FILTER_CORNER_RADIUS, FILTER_CORNER_RADIUS);
                
                // Draw "Select Region" text in dark gray (not white)
                g2d.setColor(new Color(75, 85, 99)); // Dark gray for text
                
                // Calculate font size based on rectangle height
                float fontSize = Math.min(16f, getHeight() * 0.35f);
                
                // Set font - use interMedium if available (with default letter spacing)
                if (interMedium != null) {
                    g2d.setFont(interMedium.deriveFont(fontSize));
                } else {
                    g2d.setFont(new Font("Sans-Serif", Font.PLAIN, (int)fontSize));
                }
                
                // Position text on far left with padding
                String text = selectedProvince != null ? selectedProvince : "Select Region";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int padding = 10;
                int textX = padding;
                int textY = (getHeight() - textHeight) / 2 + fm.getAscent();
                
                g2d.drawString(text, textX, textY);
                
                // Draw arrow down image on far right - in gray
                if (finalArrowImage != null) {
                    int imageSize = Math.min(getHeight() - 10, 20); // Limit size
                    int imageX = getWidth() - imageSize - padding;
                    int imageY = (getHeight() - imageSize) / 2;
                    
                    // Draw the arrow image directly without changing its color
                    g2d.drawImage(finalArrowImage, imageX, imageY, imageSize, imageSize, null);
                } else {
                    // Fallback: draw a simple arrow if image couldn't be loaded
                    int arrowSize = Math.max(6, Math.min(10, getHeight() / 4));
                    int arrowX = getWidth() - arrowSize - padding;
                    int arrowY = getHeight() / 2;
                    g2d.setColor(new Color(75, 85, 99)); // Dark gray for arrow
                    drawArrowDown(g2d, arrowX, arrowY, arrowSize);
                }
            }
            
            private void drawArrowDown(Graphics2D g2d, int x, int y, int arrowSize) {
                int[] xPoints = {x, x + arrowSize, x + arrowSize/2};
                int[] yPoints = {y - arrowSize/2, y - arrowSize/2, y + arrowSize/2};
                g2d.fillPolygon(xPoints, yPoints, 3);
            }
        };
        
        // Add hover effect to change border color
        provinceRectangle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovering[0] = true;
                provinceRectangle.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovering[0] = false;
                provinceRectangle.repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleDropdown();
            }
        });
        
        // Make the entire panel clickable
        provinceRectangle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        provinceRectangle.setPreferredSize(new Dimension(217, 45));
        provinceRectangle.setOpaque(false);
    }
    
    /**
     * Load the arrow down image from resources
     */
    private BufferedImage loadArrowImage() {
        BufferedImage arrowDownImage = null;
        try {
            // Try to load the grey arrow down image first
            File arrowFile = new File("resources/images/Candidate Search/arrow_down_grey.png");
            if (arrowFile.exists()) {
                arrowDownImage = ImageIO.read(arrowFile);
                System.out.println("Grey arrow down image loaded successfully from: " + arrowFile.getAbsolutePath());
            } else {
                System.out.println("Grey arrow down image not found at: " + arrowFile.getAbsolutePath());
                
                // Try alternative locations for grey arrow
                String[] alternativeGreyPaths = {
                    "resources/images/Candidate Search/arrow_down_grey.png",
                    "resources/images/candidate search/arrow_down_grey.png",
                    "resources/images/arrow_down_grey.png",
                    "arrow_down_grey.png"
                };
                
                for (String path : alternativeGreyPaths) {
                    File altFile = new File(path);
                    if (altFile.exists()) {
                        arrowDownImage = ImageIO.read(altFile);
                        System.out.println("Grey arrow down image loaded from alternative path: " + altFile.getAbsolutePath());
                        break;
                    }
                }
                
                // If still not found, fall back to regular arrow image
                if (arrowDownImage == null) {
                    // Fall back to original arrow image
                    File regularArrowFile = new File("resources/images/Candidate Search/arrow_down.png");
                    if (regularArrowFile.exists()) {
                        arrowDownImage = ImageIO.read(regularArrowFile);
                        System.out.println("Regular arrow down image loaded as fallback.");
                    } else {
                        // Try alternative locations for regular arrow as final fallback
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
                                System.out.println("Regular arrow down image loaded from alternative path as fallback.");
                                break;
                            }
                        }
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
        
        // Remove any existing dropdown and scrollpane
        if (scrollContainerPanel != null) {
            containerPanel.remove(scrollContainerPanel);
            scrollContainerPanel = null;
        }
        if (scrollPane != null) {
            containerPanel.remove(scrollPane);
            scrollPane = null;
        }
        if (dropdownPanel != null) {
            containerPanel.remove(dropdownPanel);
            dropdownPanel = null;
        }
        
        // Reset variables
        subsectionPanels.clear();
        isMouseInDropdown = false;
        hoveredPanel = null;
        
        if (isDropdownVisible) {
            // Add a global mouse listener to detect clicks outside the dropdown
            addGlobalMouseListener();
            
            // Shadow size for the container only (not the inner content)
            final int containerShadowSize = 4;
            
            // Position it under the province rectangle
            final Rectangle bounds = provinceRectangle.getBounds();
            
            // Calculate dynamic height based on subsections
            int numSubsections = 17; // Number of subsections - updated for all regions
            int subsectionHeight = 32; // Height of each subsection
            int subsectionSpacing = 2; // Spacing between subsections
            int topPadding = 16; // Top padding
            int bottomPadding = 16; // Bottom padding
            
            // Calculate total content height
            int contentHeight = topPadding + (numSubsections * subsectionHeight) + 
                              ((numSubsections - 1) * subsectionSpacing) + bottomPadding;
            
            // Determine if we need scrolling - if content is taller than MAX_DROPDOWN_HEIGHT
            boolean needsScrolling = contentHeight > MAX_DROPDOWN_HEIGHT;
            
            // Final dropdown height (capped at MAX_DROPDOWN_HEIGHT)
            int dynamicHeight = Math.min(contentHeight, MAX_DROPDOWN_HEIGHT);
            
            // Use the exact width of the button for the dropdown
            int dropdownWidth = bounds.width;
            
            // Create new dropdown panel with the content
            dropdownPanel = createDropdownPanel(dropdownWidth, contentHeight);
            dropdownPanel.setName("province-dropdown-content");
            
            // Position dropdown under the province rectangle
            int dropdownX = bounds.x;
            int dropdownY = bounds.y + bounds.height;
            
            // Create mouse motion and click listeners
            dropdownMouseMotionListener = createMouseMotionListener();
            dropdownMouseListener = createMouseListener();
            
            if (needsScrolling) {
                // Create a container panel to hold the scroll pane and handle shadows
                scrollContainerPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Draw shadow with reduced parameters
                        int shadowSize = 4;
                        int shadowOffset = 3;
                        float shadowOpacity = 0.15f;
                        
                        // Create shadow gradient with smaller blur
                        for (int i = 0; i < shadowSize; i++) {
                            float opacity = shadowOpacity * (1 - (float)i / shadowSize);
                            g2d.setColor(new Color(0, 0, 0, (int)(255 * opacity)));
                            
                            // Draw shadow with downward offset
                            g2d.fill(new RoundRectangle2D.Double(
                                shadowSize - i, 
                                shadowSize - i + shadowOffset, 
                                getWidth() - 2 * (shadowSize - i), 
                                getHeight() - 2 * (shadowSize - i),
                                DROPDOWN_CORNER_RADIUS, DROPDOWN_CORNER_RADIUS
                            ));
                        }
                        
                        // White background with slight opacity
                        g2d.setColor(new Color(255, 255, 255));
                        
                        // Draw rounded rectangle with a margin that accounts for the shadow
                        g2d.fill(new RoundRectangle2D.Float(
                            shadowSize, shadowSize, 
                            getWidth() - 2 * shadowSize, 
                            getHeight() - 2 * shadowSize,
                            DROPDOWN_CORNER_RADIUS, DROPDOWN_CORNER_RADIUS
                        ));
                        
                        // Draw slight border
                        g2d.setColor(new Color(0, 0, 0, 20)); // Very light gray with transparency
                        g2d.setStroke(new BasicStroke(1));
                        g2d.draw(new RoundRectangle2D.Float(
                            shadowSize, shadowSize, 
                            getWidth() - 2 * shadowSize, 
                            getHeight() - 2 * shadowSize,
                            DROPDOWN_CORNER_RADIUS, DROPDOWN_CORNER_RADIUS
                        ));
                    }
                };
                scrollContainerPanel.setLayout(null);
                scrollContainerPanel.setOpaque(false);
                scrollContainerPanel.setName("province-dropdown-container");
                
                // Create a scroll pane for the dropdown if needed
                scrollPane = new JScrollPane(dropdownPanel);
                scrollPane.setName("province-dropdown-scrollpane");
                scrollPane.setBorder(BorderFactory.createEmptyBorder());
                scrollPane.setOpaque(false);
                scrollPane.getViewport().setOpaque(false);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                
                // Customize scrollbar appearance
                scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                    @Override
                    protected JButton createDecreaseButton(int orientation) {
                        return createEmptyButton();
                    }
                    
                    @Override
                    protected JButton createIncreaseButton(int orientation) {
                        return createEmptyButton();
                    }
                    
                    @Override
                    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                        // Paint nothing for track background
                    }
                    
                    @Override
                    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                            return;
                        }
                        
                        // Paint custom thumb - 4px wide, light gray, fully rounded corners
                        int w = 4;
                        int x = thumbBounds.x + (thumbBounds.width - w) / 2;
                        
                        g2.setColor(new Color(203, 213, 225)); // #CBD5E1 - matching search box border
                        g2.fillRoundRect(x, thumbBounds.y, w, thumbBounds.height, w, w);
                        
                        g2.dispose();
                    }
                    
                    private JButton createEmptyButton() {
                        JButton button = new JButton();
                        button.setPreferredSize(new Dimension(0, 0));
                        button.setMinimumSize(new Dimension(0, 0));
                        button.setMaximumSize(new Dimension(0, 0));
                        return button;
                    }
                });
                
                // Position the scroll pane inside the container with padding for shadows
                // Use the exact content width without additional padding since there's no shadow in the inner content
                scrollPane.setBounds(
                    containerShadowSize, 
                    containerShadowSize, 
                    dropdownWidth, 
                    dynamicHeight - containerShadowSize * 2
                );
                
                // Add scroll pane to the container
                scrollContainerPanel.add(scrollPane);
                
                // Set the size of the container to include shadow space
                scrollContainerPanel.setSize(dropdownWidth + containerShadowSize * 2, dynamicHeight + containerShadowSize * 2);
                scrollContainerPanel.setPreferredSize(new Dimension(dropdownWidth + containerShadowSize * 2, dynamicHeight + containerShadowSize * 2));
                
                // Set initial position with 0 height for animation
                scrollContainerPanel.setBounds(
                    dropdownX - containerShadowSize, 
                    dropdownY, 
                    dropdownWidth + containerShadowSize * 2, 
                    0
                );
                
                // Add container to parent panel
                containerPanel.add(scrollContainerPanel);
                
                // Ensure it's at the top of the component stack
                containerPanel.setComponentZOrder(scrollContainerPanel, 0);
                
                // Add listeners
                scrollPane.addMouseMotionListener(dropdownMouseMotionListener);
                scrollPane.addMouseListener(dropdownMouseListener);
                
                currentDropdownHeight = 0;
                dropdownTargetHeight = dynamicHeight + containerShadowSize * 2;
                
                // Start animation
                dropdownAnimationTimer = new Timer(ANIMATION_DURATION / ANIMATION_FRAMES, e -> {
                    // Calculate frame increment
                    int stepHeight = dropdownTargetHeight / ANIMATION_FRAMES;
                    currentDropdownHeight += stepHeight;
                    
                    if (currentDropdownHeight >= dropdownTargetHeight) {
                        currentDropdownHeight = dropdownTargetHeight;
                        dropdownAnimationTimer.stop();
                        
                        // Ensure it stays at the top of the z-order
                        containerPanel.setComponentZOrder(scrollContainerPanel, 0);
                    }
                    
                    // Get current bounds of the button
                    Rectangle currentBounds = provinceRectangle.getBounds();
                    
                    // Update scroll container position during animation
                    scrollContainerPanel.setBounds(
                        currentBounds.x - containerShadowSize, 
                        currentBounds.y + currentBounds.height, 
                        dropdownWidth + containerShadowSize * 2, 
                        currentDropdownHeight
                    );
                    containerPanel.repaint();
                });
                dropdownAnimationTimer.start();
            } else {
                // For non-scrolling dropdowns, add directly to container
                // Set initial position with 0 height for animation - account for shadow size
                dropdownPanel.setBounds(dropdownX - containerShadowSize, dropdownY, dropdownWidth + containerShadowSize * 2, 0);
                containerPanel.add(dropdownPanel);
                
                // Ensure the dropdown is at the top of the component stack
                containerPanel.setComponentZOrder(dropdownPanel, 0);
                
                // Add mouse listeners to dropdown
                dropdownPanel.addMouseMotionListener(dropdownMouseMotionListener);
                dropdownPanel.addMouseListener(dropdownMouseListener);
                
                currentDropdownHeight = 0;
                dropdownTargetHeight = dynamicHeight + containerShadowSize * 2;
                
                // Start animation
                dropdownAnimationTimer = new Timer(ANIMATION_DURATION / ANIMATION_FRAMES, e -> {
                    // Calculate frame increment
                    int stepHeight = dropdownTargetHeight / ANIMATION_FRAMES;
                    currentDropdownHeight += stepHeight;
                    
                    if (currentDropdownHeight >= dropdownTargetHeight) {
                        currentDropdownHeight = dropdownTargetHeight;
                        dropdownAnimationTimer.stop();
                        
                        // Ensure it stays at the top of the z-order
                        containerPanel.setComponentZOrder(dropdownPanel, 0);
                    }
                    
                    // Get current bounds of the button
                    Rectangle currentBounds = provinceRectangle.getBounds();
                    
                    // Update dropdown position during animation - account for shadows
                    dropdownPanel.setBounds(currentBounds.x - containerShadowSize, currentBounds.y + currentBounds.height,
                                         dropdownWidth + containerShadowSize * 2, currentDropdownHeight);
                    containerPanel.repaint();
                });
                dropdownAnimationTimer.start();
            }
        } else {
            // Remove the global mouse listener
            removeGlobalMouseListener();
        }
        
        // Ensure the dropdown component has the highest z-order to always be on top
        if (scrollContainerPanel != null) {
            containerPanel.setComponentZOrder(scrollContainerPanel, 0);
        } else if (scrollPane != null) {
            containerPanel.setComponentZOrder(scrollPane, 0);
        } else if (dropdownPanel != null) {
            containerPanel.setComponentZOrder(dropdownPanel, 0);
        }
    }
    
    /**
     * Creates the dropdown panel with styled options
     */
    private JPanel createDropdownPanel(int width, int height) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Use clean white background with no shadow
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        panel.setLayout(null); // Use absolute positioning for dropdown items
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBorder(null);
        panel.setOpaque(false);
        
        // Add the province options
        addDropdownSubsections(panel);
        
        return panel;
    }
    
    /**
     * Add the province options to the dropdown panel
     */
    private void addDropdownSubsections(JPanel dropdownPanel) {
        // Clear the existing list of subsection panels
        subsectionPanels.clear();
        
        // Reset hovered panel
        hoveredPanel = null;
        
        // Define the list of regions as requested by the user
        String[] regions = {
            "Region I",
            "Region II",
            "Region III",
            "Region IV-A",
            "Region IV-B",
            "Region V",
            "Region VI",
            "Region VII",
            "Region VIII",
            "Region IX",
            "Region X",
            "Region XI",
            "Region XII",
            "Region XIII",
            "NCR",
            "CAR",
            "BARMM"
        };
        
        // Load checkmark image
        BufferedImage checkmarkImage = loadCheckmarkImage();
        
        // Shadow parameters - reduced to match the other methods
        int shadowSize = 4;
        int shadowOffset = 3;
        
        // Add each subsection
        int subsectionHeight = 32;
        int subsectionSpacing = 2;
        // Calculate width accounting for shadows and side padding
        int totalPanelWidth = dropdownPanel.getWidth() - (shadowSize * 2) - 16; // Account for shadow and padding
        int startY = 16 + shadowSize; // Top padding + shadow size
        int startX = 8 + shadowSize; // Left padding + shadow size
        
        // Ensure the panel is wide enough
        if (totalPanelWidth <= 0) totalPanelWidth = 180; // Minimum width fallback
        
        for (int i = 0; i < regions.length; i++) {
            final int index = i;
            final String region = regions[i];
            final boolean isSelected = (selectedIndex == index);
            
            JPanel subsectionPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Fill background based on hover state (background color is set in mouse listeners)
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    
                    // Set font - this time using Inter Regular instead of Medium
                    Font font = interRegular;
                    if (font == null) {
                        font = new Font("Sans-Serif", Font.PLAIN, 14);
                    } else {
                        font = font.deriveFont(14f);
                    }
                    g2d.setFont(font);
                    
                    // Draw region name
                    g2d.setColor(new Color(0, 0, 0, 220)); // Very dark gray, not pure black
                    FontMetrics fm = g2d.getFontMetrics();
                    int textHeight = fm.getHeight();
                    int textX = 8; // Left padding
                    int textY = (getHeight() - textHeight) / 2 + fm.getAscent();
                    
                    g2d.drawString(region, textX, textY);
                    
                    // If this item is selected, draw the checkmark
                    if (isSelected && checkmarkImage != null) {
                        // Calculate checkmark dimensions
                        int[] dimensions = calculateCheckmarkDimensions(checkmarkImage, 16);
                        int checkmarkWidth = dimensions[0];
                        int checkmarkHeight = dimensions[1];
                        
                        // Position on far right
                        int checkmarkX = getWidth() - checkmarkWidth - 8;
                        int checkmarkY = (getHeight() - checkmarkHeight) / 2;
                        
                        g2d.drawImage(checkmarkImage, checkmarkX, checkmarkY, checkmarkWidth, checkmarkHeight, null);
                    }
                }
            };
            
            // Set position and dimensions - adjusted for shadow
            int y = startY + (i * (subsectionHeight + subsectionSpacing));
            subsectionPanel.setBounds(startX, y, totalPanelWidth, subsectionHeight);
            
            // Add hover styling with mouse listeners - only for click, not hover
            subsectionPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Update selection
                    selectedIndex = index;
                    selectedProvince = region;
                    
                    // Close dropdown
                    closeDropdown();
                    
                    // Notify listener if provided
                    if (onSelectionChanged != null) {
                        onSelectionChanged.accept(selectedProvince);
                    }
                    
                    // Repaint the main button
                    provinceRectangle.repaint();
                }
            });
            
            // Use transparent background
            subsectionPanel.setBackground(new Color(255, 255, 255, 0));
            subsectionPanel.setOpaque(true);
            subsectionPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Add to dropdown
            dropdownPanel.add(subsectionPanel);
            
            // Add to subsection panels list
            subsectionPanels.add(subsectionPanel);
        }
    }
    
    /**
     * Calculate proportional dimensions for the checkmark image
     */
    private int[] calculateCheckmarkDimensions(BufferedImage image, int targetHeight) {
        if (image == null) {
            // Return default dimensions if image is null
            return new int[] {targetHeight, targetHeight};
        }
        
        double aspectRatio = (double)image.getWidth() / image.getHeight();
        int width = (int)(targetHeight * aspectRatio);
        int height = targetHeight;
        return new int[]{width, height};
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
        if (isDropdownVisible) {
            Rectangle bounds = provinceRectangle.getBounds();
            
            if (scrollContainerPanel != null) {
                // Shadow size for proper positioning
                int shadowSize = 4;
                
                // Update scroll container panel position - maintain its height
                scrollContainerPanel.setBounds(
                    bounds.x - shadowSize, 
                    bounds.y + bounds.height, 
                    bounds.width + (shadowSize * 2), 
                    scrollContainerPanel.getHeight()
                );
            } else if (scrollPane != null) {
                // Use the exact width of the button for the dropdown
                scrollPane.setBounds(bounds.x, bounds.y + bounds.height, 
                                  bounds.width, scrollPane.getHeight());
            } else if (dropdownPanel != null) {
                // Use the exact width of the button for the dropdown
                dropdownPanel.setBounds(bounds.x, bounds.y + bounds.height, 
                                      bounds.width, dropdownPanel.getHeight());
            }
        }
    }
    
    /**
     * Get the province rectangle panel
     */
    public JPanel getProvinceRectangle() {
        return provinceRectangle;
    }
    
    /**
     * Get the currently selected province
     */
    public String getSelectedProvince() {
        return selectedProvince;
    }
    
    /**
     * Close the dropdown immediately without animation
     */
    public void closeDropdown() {
        if (isDropdownVisible) {
            isDropdownVisible = false;
            
            // Remove global mouse listener
            removeGlobalMouseListener();
            
            // Reset hover state
            hoveredPanel = null;
            isMouseInDropdown = false;
            
            // Remove mouse motion listeners
            if (dropdownMouseMotionListener != null) {
                if (scrollPane != null) {
                    scrollPane.removeMouseMotionListener(dropdownMouseMotionListener);
                }
                if (dropdownPanel != null) {
                    dropdownPanel.removeMouseMotionListener(dropdownMouseMotionListener);
                }
                dropdownMouseMotionListener = null;
            }
            
            // Remove mouse listeners
            if (dropdownMouseListener != null) {
                if (scrollPane != null) {
                    scrollPane.removeMouseListener(dropdownMouseListener);
                }
                if (dropdownPanel != null) {
                    dropdownPanel.removeMouseListener(dropdownMouseListener);
                }
                dropdownMouseListener = null;
            }
            
            if (dropdownAnimationTimer != null) {
                dropdownAnimationTimer.stop();
            }
            
            if (scrollContainerPanel != null && scrollContainerPanel.getParent() == containerPanel) {
                containerPanel.remove(scrollContainerPanel);
                containerPanel.repaint();
                scrollContainerPanel = null;
            }
            
            if (scrollPane != null && scrollPane.getParent() == containerPanel) {
                containerPanel.remove(scrollPane);
                containerPanel.repaint();
                scrollPane = null;
            }
            
            if (dropdownPanel != null && dropdownPanel.getParent() == containerPanel) {
                containerPanel.remove(dropdownPanel);
                containerPanel.repaint();
                dropdownPanel = null;
            }
            
            // Clear the subsection panels list
            subsectionPanels.clear();
        }
    }
    
    /**
     * Add a global mouse listener to detect clicks outside the dropdown
     */
    private void addGlobalMouseListener() {
        // Remove any existing listener first
        removeGlobalMouseListener();
        
        // Create a new listener
        globalMouseListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                // Only listen for mouse press events
                if (event instanceof MouseEvent && event.getID() == MouseEvent.MOUSE_PRESSED) {
                    MouseEvent mouseEvent = (MouseEvent) event;
                    
                    // Check if click is outside dropdown and button
                    if (dropdownPanel != null || scrollPane != null) {
                        Component clickedComponent = mouseEvent.getComponent();
                        boolean clickedOnButton = isChildOf(clickedComponent, provinceRectangle);
                        boolean clickedOnDropdown = (dropdownPanel != null && isChildOf(clickedComponent, dropdownPanel))
                                               || (scrollPane != null && isChildOf(clickedComponent, scrollPane));
                        
                        // If clicked outside both, close the dropdown
                        if (!clickedOnButton && !clickedOnDropdown) {
                            closeDropdown();
                        }
                    }
                }
            }
        };
        
        // Add the listener to the toolkit
        Toolkit.getDefaultToolkit().addAWTEventListener(
            globalMouseListener, AWTEvent.MOUSE_EVENT_MASK);
    }
    
    /**
     * Remove the global mouse listener
     */
    private void removeGlobalMouseListener() {
        if (globalMouseListener != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(globalMouseListener);
            globalMouseListener = null;
        }
    }
    
    /**
     * Check if a component is a child of another component
     */
    private boolean isChildOf(Component child, Component parent) {
        if (child == null || parent == null) {
            return false;
        }
        
        if (child == parent) {
            return true;
        }
        
        Component current = child;
        while (current != null) {
            if (current == parent) {
                return true;
            }
            current = current.getParent();
        }
        
        return false;
    }
    
    /**
     * Create a mouse motion listener to track mouse movements over the dropdown
     */
    private MouseMotionListener createMouseMotionListener() {
        dropdownMouseMotionListener = new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Get the mouse position in the dropdown panel's coordinate system
                Point mousePoint = SwingUtilities.convertPoint(
                    (Component) e.getSource(), 
                    e.getPoint(), 
                    dropdownPanel
                );
                
                // Track that mouse is inside the dropdown
                isMouseInDropdown = true;
                
                // Check if mouse is over any of the subsection panels
                boolean overAnyPanel = false;
                JPanel panelToHighlight = null;
                
                for (JPanel panel : subsectionPanels) {
                    if (panel.getBounds().contains(mousePoint)) {
                        // Found the panel under the mouse
                        overAnyPanel = true;
                        panelToHighlight = panel;
                        break;
                    }
                }
                
                // Update hover states
                if (overAnyPanel) {
                    // If hovering over a different panel than before
                    if (hoveredPanel != panelToHighlight) {
                        // Clear previous hover
                        if (hoveredPanel != null) {
                            hoveredPanel.setBackground(new Color(255, 255, 255, 0));
                            hoveredPanel.repaint();
                        }
                        
                        // Set new hover
                        hoveredPanel = panelToHighlight;
                        hoveredPanel.setBackground(new Color(240, 240, 240));
                        hoveredPanel.repaint();
                    }
                } else {
                    // Not over any panel, clear hover if needed
                    if (hoveredPanel != null) {
                        hoveredPanel.setBackground(new Color(255, 255, 255, 0));
                        hoveredPanel.repaint();
                        hoveredPanel = null;
                    }
                }
            }
        };
        
        return dropdownMouseMotionListener;
    }
    
    /**
     * Create a mouse listener to track mouse enter/exit events
     */
    private MouseListener createMouseListener() {
        dropdownMouseListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isMouseInDropdown = true;
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Check if the mouse truly exited the entire dropdown
                Point mousePoint = e.getPoint();
                SwingUtilities.convertPointToScreen(mousePoint, (Component) e.getSource());
                
                Rectangle bounds;
                if (scrollPane != null) {
                    bounds = new Rectangle(scrollPane.getLocationOnScreen(), scrollPane.getSize());
                } else {
                    bounds = new Rectangle(dropdownPanel.getLocationOnScreen(), dropdownPanel.getSize());
                }
                
                if (!bounds.contains(mousePoint)) {
                    isMouseInDropdown = false;
                    
                    // Clear any hover state
                    if (hoveredPanel != null) {
                        hoveredPanel.setBackground(new Color(255, 255, 255, 0));
                        hoveredPanel.repaint();
                        hoveredPanel = null;
                    }
                }
            }
        };
        
        return dropdownMouseListener;
    }
} 