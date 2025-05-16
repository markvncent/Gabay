package frontend.overview;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.Ellipse2D;

import backend.model.CandidateDataLoader;
import frontend.comparison.MinimalScrollBarUI;
import frontend.comparison.CandidateDataManager;
import frontend.search.ViewCandidate;

/**
 * A panel that displays candidates in a list view, grouped by position and sorted alphabetically
 */
public class CandidateListPanel extends JPanel {
    // UI Components
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JPanel mainContainer;
    
    // Fonts
    private Font interRegular;
    private Font interSemiBold;
    private Font interMedium;
    private Font interBlack;
    
    // Colors
    private Color primaryBlue = new Color(0x2B, 0x37, 0x80); // #2B3780
    private Color textColor = new Color(0x47, 0x55, 0x69); // #475569
    private Color lightGray = new Color(0xF1, 0xF5, 0xF9); // #F1F5F9
    private Color darkGray = new Color(0x64, 0x74, 0x8B); // #64748B
    private Color dividerColor = new Color(0xE2, 0xE8, 0xF0); // #E2E8F0
    private Color panelBackground = new Color(255, 255, 255, 245); // Slightly transparent white
    private Color cardBorder = new Color(0xE2, 0xE8, 0xF0); // #E2E8F0
    
    // View profile callback
    private Consumer<String> onViewProfile;
    
    // Store the candidates
    private List<CandidateDataLoader.Candidate> allCandidates = new ArrayList<>();
    
    // Grid layout settings
    private int columns = 3; // Changed from 4 to 3
    private int horizontalGap = 20; // Increased gap for better spacing with fewer columns
    private int verticalGap = 15;
    
    // Panel dimensions
    private Dimension preferredPanelSize = new Dimension(900, 600);
    private Dimension candidateCardSize = new Dimension(250, 80); // Larger cards for 3-column layout
    
    // Cache for candidate images
    private Map<String, BufferedImage> candidateImageCache = new HashMap<>();
    
    // Store the positions
    private List<String> positions = new ArrayList<>();
    
    // Store position panels for scrolling
    private Map<String, JPanel> positionPanels = new HashMap<>();
    
    /**
     * Create a panel that displays candidates in a list view
     * 
     * @param interRegular Regular font
     * @param interSemiBold SemiBold font
     * @param interMedium Medium font
     * @param interBlack Black font
     * @param onViewProfile Callback when a candidate is clicked
     */
    public CandidateListPanel(Font interRegular, Font interSemiBold, Font interMedium, Font interBlack,
                             Consumer<String> onViewProfile) {
        this.interRegular = interRegular;
        this.interSemiBold = interSemiBold;
        this.interMedium = interMedium;
        this.interBlack = interBlack;
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
        
        // Create main container panel with rounded corners and background
        mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background
                g2d.setColor(panelBackground);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw border
                g2d.setColor(dividerColor);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        mainContainer.setOpaque(false);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Reduced from 15px
        
        // Create content panel with BoxLayout for vertical stacking
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Reduced from 10px
        
        // Create scroll pane with ALWAYS showing scrollbar to ensure it's accessible
        scrollPane = new JScrollPane(contentPanel, 
                                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        
        // Apply custom scrollbar UI
        scrollPane.getVerticalScrollBar().setUI(new MinimalScrollBarUI());
        
        // Add scroll pane to main container
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        
        // Add main container to this panel
        add(mainContainer, BorderLayout.CENTER);
        
        // Add some padding around the panel
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Reduced from 10px
        
        // Set preferred size
        setPreferredSize(preferredPanelSize);
    }
    
    /**
     * Get the list of positions
     * @return List of position names
     */
    public List<String> getPositions() {
        return positions;
    }
    
    /**
     * Scroll to a specific position
     * @param position Position name
     */
    public void scrollToPosition(String position) {
        JPanel panel = positionPanels.get(position);
        if (panel != null) {
            // Scroll to the position panel
            Rectangle bounds = panel.getBounds();
            scrollPane.getViewport().setViewPosition(new Point(0, bounds.y));
            
            // Update UI
            revalidate();
            repaint();
        }
    }
    
    /**
     * Load candidate data and create list items
     */
    private void loadCandidateData() {
        // Clear existing content
        contentPanel.removeAll();
        positionPanels.clear();
        
        // Load candidate data
        allCandidates = CandidateDataLoader.loadCandidates();
        
        // Group candidates by position
        Map<String, List<CandidateDataLoader.Candidate>> candidatesByPosition = allCandidates.stream()
            .collect(Collectors.groupingBy(CandidateDataLoader.Candidate::getPosition));
        
        // Define the custom position order
        List<String> orderedPositions = Arrays.asList(
            "President",
            "Vice President",
            "Senator",
            "Governor",
            "Vice Governor",
            "Partylist Representative"
        );
        
        // Create a list of positions that exist in our data
        positions = new ArrayList<>();
        
        // First add positions in the specified order if they exist in our data
        for (String position : orderedPositions) {
            if (candidatesByPosition.containsKey(position)) {
                positions.add(position);
            }
        }
        
        // Then add any remaining positions alphabetically
        List<String> remainingPositions = new ArrayList<>(candidatesByPosition.keySet());
        remainingPositions.removeAll(orderedPositions);
        Collections.sort(remainingPositions);
        positions.addAll(remainingPositions);
        
        // Create section for each position
        for (String position : positions) {
            // Get candidates for this position
            List<CandidateDataLoader.Candidate> candidates = candidatesByPosition.get(position);
            
            // Sort candidates alphabetically by name
            candidates.sort(Comparator.comparing(CandidateDataLoader.Candidate::getName));
            
            // Create position header
            JPanel headerPanel = createPositionHeader(position, candidates.size());
            contentPanel.add(headerPanel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Minimal spacing
            
            // Store the header panel for scrolling
            positionPanels.put(position, headerPanel);
            
            // Create grid panel for candidates
            JPanel gridPanel = createCandidateGrid(candidates);
            contentPanel.add(gridPanel);
            
            // Add minimal spacing between position sections
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // Force the content panel to calculate its proper size for scrolling
        ensureScrollingWorks();
        
        // Update UI
        revalidate();
        repaint();
    }
    
    /**
     * Ensure scrolling works by forcing the content panel to calculate its proper size
     */
    private void ensureScrollingWorks() {
        // Force the content panel to calculate its proper size
        contentPanel.setSize(contentPanel.getPreferredSize());
        
        // Make sure the scrollpane knows the content size
        if (scrollPane != null) {
            scrollPane.validate();
            
            // Force scrollbar to be visible
            JScrollBar vScrollBar = scrollPane.getVerticalScrollBar();
            vScrollBar.setEnabled(true);
            vScrollBar.setVisible(true);
            
            // Debug the sizes
            System.out.println("Content panel size: " + contentPanel.getPreferredSize().height);
            System.out.println("Scroll pane viewport size: " + scrollPane.getViewport().getViewSize().height);
            System.out.println("Scroll pane view size: " + scrollPane.getViewport().getView().getPreferredSize().height);
        }
    }
    
    /**
     * Create a grid panel for candidates
     */
    private JPanel createCandidateGrid(List<CandidateDataLoader.Candidate> candidates) {
        // Calculate rows needed
        int rows = (int) Math.ceil((double) candidates.size() / columns);
        
        // Create panel with grid layout
        JPanel gridPanel = new JPanel(new GridLayout(rows, columns, horizontalGap, verticalGap));
        gridPanel.setOpaque(false);
        
        // Add candidates to grid
        for (CandidateDataLoader.Candidate candidate : candidates) {
            JPanel candidatePanel = createCandidatePanel(candidate);
            gridPanel.add(candidatePanel);
        }
        
        // Fill empty cells with blank panels to maintain grid structure
        int emptyCells = (rows * columns) - candidates.size();
        for (int i = 0; i < emptyCells; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setOpaque(false);
            gridPanel.add(emptyPanel);
        }
        
        return gridPanel;
    }
    
    /**
     * Create a header panel for a position
     */
    private JPanel createPositionHeader(String position, int count) {
        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.setOpaque(false);
        headerContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Reduced from 70
        
        // Header panel for title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Reduced padding
        
        // Position title
        JLabel positionLabel = new JLabel(position + " (" + count + ")");
        positionLabel.setFont(interBlack.deriveFont(18f)); // Reduced from 24f
        positionLabel.setForeground(primaryBlue);
        headerPanel.add(positionLabel, BorderLayout.CENTER);
        
        // Add header to container
        headerContainer.add(headerPanel, BorderLayout.CENTER);
        
        // Add divider line
        JSeparator divider = new JSeparator();
        divider.setForeground(dividerColor);
        divider.setBackground(dividerColor);
        headerContainer.add(divider, BorderLayout.SOUTH);
        
        return headerContainer;
    }
    
    /**
     * Create a panel for a candidate
     */
    private JPanel createCandidatePanel(CandidateDataLoader.Candidate candidate) {
        // Create main panel with hover effect
        JPanel panel = new JPanel() {
            private boolean isHovered = false;
            
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        repaint();
                    }
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Open the ViewCandidate dialog
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(CandidateListPanel.this);
                        new ViewCandidate(parentFrame, candidate.getName());
                        
                        // Also call the callback if provided
                        if (onViewProfile != null) {
                            onViewProfile.accept(candidate.getName());
                        }
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                if (isHovered) {
                    g2d.setColor(lightGray);
                } else {
                    g2d.setColor(new Color(255, 255, 255));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2d.setColor(dividerColor);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        
        panel.setLayout(new BorderLayout(10, 0)); // Horizontal gap between image and text
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        panel.setPreferredSize(candidateCardSize);
        
        // Create left panel for candidate image
        JPanel imagePanel = createCandidateImagePanel(candidate.getName(), candidate.getImagePath());
        imagePanel.setPreferredSize(new Dimension(40, 40)); // Image size
        
        // Create a wrapper panel to center the image vertically but keep it left-aligned
        JPanel imageWrapper = new JPanel(new BorderLayout());
        imageWrapper.setOpaque(false);
        imageWrapper.add(imagePanel, BorderLayout.CENTER);
        
        // Create right panel for candidate info with FlowLayout for left alignment
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        // Candidate name
        JLabel nameLabel = new JLabel(candidate.getName());
        nameLabel.setFont(interSemiBold.deriveFont(14f));
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Candidate party
        JLabel partyLabel = new JLabel(candidate.getParty());
        partyLabel.setFont(interRegular.deriveFont(12f));
        partyLabel.setForeground(darkGray);
        partyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add components to info panel
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(partyLabel);
        
        // Create a wrapper panel with BorderLayout to ensure left alignment
        JPanel infoWrapper = new JPanel(new BorderLayout());
        infoWrapper.setOpaque(false);
        infoWrapper.add(infoPanel, BorderLayout.WEST);
        
        // Add components to main panel
        panel.add(imageWrapper, BorderLayout.WEST);
        panel.add(infoWrapper, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create an image panel for a candidate with profile picture or initials
     */
    private JPanel createCandidateImagePanel(String candidateName, String imagePath) {
        // Create panel for circular image
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
                
                // Get candidate image
                BufferedImage candidateImage = getCandidateImage(candidateName, imagePath);
                
                if (candidateImage != null) {
                    // Create a circular clip
                    Shape clip = new Ellipse2D.Double(0, 0, diameter, diameter);
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
     * Get or load candidate image from path or cache
     */
    private BufferedImage getCandidateImage(String candidateName, String imagePath) {
        // Check if image is already in cache
        if (candidateImageCache.containsKey(candidateName)) {
            return candidateImageCache.get(candidateName);
        }
        
        BufferedImage image = null;
        
        try {
            // If path is null or empty, use a placeholder
            if (imagePath == null || imagePath.trim().isEmpty()) {
                image = createPlaceholderImage(candidateName);
            } else {
                // Try to load the image
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    image = ImageIO.read(imageFile);
                } else {
                    image = createPlaceholderImage(candidateName);
                }
            }
        } catch (IOException e) {
            image = createPlaceholderImage(candidateName);
        }
        
        // Cache the image
        candidateImageCache.put(candidateName, image);
        
        return image;
    }
    
    /**
     * Create a placeholder image with the candidate's initials
     */
    private BufferedImage createPlaceholderImage(String candidateName) {
        // Try to get the default profile image from CandidateDataManager
        try {
            BufferedImage defaultImage = CandidateDataManager.getDefaultProfileImage();
            if (defaultImage != null) {
                return defaultImage;
            }
        } catch (Exception e) {
            // If there's any error, continue to create our own placeholder
        }
        
        // Create a placeholder image with the candidate's initials
        int size = 100; // Larger than needed for better quality when scaled down
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
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
        
        return image;
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
     * Set the panel's preferred size
     * @param width Panel width
     * @param height Panel height
     */
    public void setPanelSize(int width, int height) {
        // Store dimensions
        preferredPanelSize = new Dimension(width, height);
        
        // Set panel size constraints
        setPreferredSize(preferredPanelSize);
        setMinimumSize(preferredPanelSize);
        setMaximumSize(preferredPanelSize);
        
        // Set container size
        if (mainContainer != null) {
            Dimension containerSize = new Dimension(width - 10, height - 10);
            mainContainer.setPreferredSize(containerSize);
        }
        
        // Explicitly set scroll pane size
        if (scrollPane != null) {
            scrollPane.setPreferredSize(new Dimension(width - 20, height - 20));
            
            // Ensure scrollbar is always visible
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            
            // Make sure the scrollbar is visible and enabled
            JScrollBar vScrollBar = scrollPane.getVerticalScrollBar();
            vScrollBar.setEnabled(true);
            vScrollBar.setVisible(true);
        }
        
        // Don't restrict content panel height to allow scrolling
        if (contentPanel != null) {
            contentPanel.setPreferredSize(null); // Let it determine its own preferred size
        }
        
        // Force update
        revalidate();
        repaint();
        
        // Debug
        System.out.println("Panel size set to: " + width + "x" + height);
    }
    
    /**
     * Set the card size
     * @param width Card width
     * @param height Card height
     */
    public void setCardSize(int width, int height) {
        candidateCardSize = new Dimension(width, height);
        loadCandidateData(); // Reload with new card size
    }
    
    /**
     * Set the number of columns in the grid
     * @param columns Number of columns
     */
    public void setColumns(int columns) {
        this.columns = Math.max(1, columns);
        loadCandidateData(); // Reload with new column count
    }
    
    /**
     * Set the spacing between cards
     * @param horizontal Horizontal gap
     * @param vertical Vertical gap
     */
    public void setCardSpacing(int horizontal, int vertical) {
        this.horizontalGap = horizontal;
        this.verticalGap = vertical;
        loadCandidateData(); // Reload with new spacing
    }
} 