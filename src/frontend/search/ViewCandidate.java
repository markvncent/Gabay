package frontend.search;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import backend.model.CandidateDataLoader;
import backend.model.CandidateDataLoader.Candidate;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Popup window to display detailed candidate information when a candidate card is clicked.
 */
public class ViewCandidate extends JDialog {
    // UI Components
    private JPanel contentPanel;
    private JLabel titleLabel;
    private JButton closeButton;
    
    // Styling properties
    private final Color BACKGROUND_COLOR = new Color(0xF7, 0xF9, 0xFC); // Light background color
    private final Color TEXT_COLOR = new Color(0x47, 0x55, 0x69); // Text color
    private final Color BORDER_COLOR = new Color(0xE5, 0xE7, 0xEB); // Border color
    private final Color ACCENT_COLOR = new Color(0x2B, 0x37, 0x80); // Blue accent #2B3780
    private final int CORNER_RADIUS = 10;
    
    // Candidate data
    private String candidateName;
    private CandidateDataLoader.Candidate candidateData;
    private BufferedImage candidateImage;
    
    // Dimensions
    private final int WIDTH = 850;
    private final int HEIGHT = 500;
    
    // Profile image dimensions
    private int profileImageWidth = 250;
    private int profileImageHeight = 150;
    
    // Profile sections to display
    private static final String[] PROFILE_SECTIONS = {
        "Position", "Age", "Region", "Party Affiliation", "Years of Experience", "Campaign Slogan"
    };
    
    // Fonts
    private Font interRegular;
    private Font interMedium;
    private Font interSemiBold;
    
    /**
     * Create a new ViewCandidate popup
     * 
     * @param parent The parent component
     * @param candidateName The name of the candidate to display
     */
    public ViewCandidate(JFrame parent, String candidateName) {
        super(parent, true); // Modal dialog
        this.candidateName = candidateName;
        
        // Load fonts
        loadFonts();
        
        // Initialize UI
        initializeUI();
        
        // Load candidate data
        loadCandidateData();
        
        // Center the popup relative to parent
        if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            setLocationRelativeTo(null);
        }
        
        // Make dialog visible
        setVisible(true);
    }
    
    /**
     * Load fonts used in the UI
     */
    private void loadFonts() {
        try {
            // Load Inter fonts
            File interRegularFile = new File("lib/fonts/Inter_18pt-Regular.ttf");
            File interMediumFile = new File("lib/fonts/Inter_18pt-Medium.ttf");
            File interSemiBoldFile = new File("lib/fonts/Inter_18pt-SemiBold.ttf");
            
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            
            if (interRegularFile.exists()) {
                interRegular = Font.createFont(Font.TRUETYPE_FONT, interRegularFile).deriveFont(14f);
                ge.registerFont(interRegular);
            } else {
                interRegular = new Font("Sans-Serif", Font.PLAIN, 14);
            }
            
            if (interMediumFile.exists()) {
                interMedium = Font.createFont(Font.TRUETYPE_FONT, interMediumFile).deriveFont(14f);
                ge.registerFont(interMedium);
            } else {
                interMedium = new Font("Sans-Serif", Font.PLAIN, 14);
            }
            
            if (interSemiBoldFile.exists()) {
                interSemiBold = Font.createFont(Font.TRUETYPE_FONT, interSemiBoldFile).deriveFont(16f);
                ge.registerFont(interSemiBold);
            } else {
                interSemiBold = new Font("Sans-Serif", Font.BOLD, 16);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to system fonts
            interRegular = new Font("Sans-Serif", Font.PLAIN, 14);
            interMedium = new Font("Sans-Serif", Font.PLAIN, 14);
            interSemiBold = new Font("Sans-Serif", Font.BOLD, 16);
        }
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        // Set up the dialog
        setUndecorated(true); // Remove window decorations
        setSize(WIDTH, HEIGHT);
        
        // Create the main content panel with rounded corners (no shadow)
        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fill(new RoundRectangle2D.Double(
                    0, 0, 
                    getWidth(), 
                    getHeight(),
                    CORNER_RADIUS, CORNER_RADIUS
                ));
                
                // Draw border
                g2d.setColor(BORDER_COLOR);
                g2d.draw(new RoundRectangle2D.Double(
                    0, 0, 
                    getWidth() - 1, 
                    getHeight() - 1,
                    CORNER_RADIUS, CORNER_RADIUS
                ));
            }
        };
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create header with close button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 15, 5));
        
        // Title label
        titleLabel = new JLabel("Candidate Profile");
        titleLabel.setFont(interSemiBold != null ? interSemiBold.deriveFont(22f) : new Font("Inter", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Close button
        closeButton = new JButton("×");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 22));
        closeButton.setForeground(TEXT_COLOR);
        closeButton.setBackground(null);
        closeButton.setBorder(null);
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect to close button
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(ACCENT_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(TEXT_COLOR);
            }
        });
        
        // Add action listener to close button
        closeButton.addActionListener(e -> dispose());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(closeButton, BorderLayout.EAST);
        
        // Create main content area
        JPanel mainContent = new JPanel(new BorderLayout(25, 0));
        mainContent.setOpaque(false);
        
        // Create profile panel (left side)
        JPanel profilePanel = createProfilePanel();
        
        // Create details panel (right side)
        JPanel detailsPanel = createDetailsPanel();
        
        // Add panels to main content
        mainContent.add(profilePanel, BorderLayout.WEST);
        mainContent.add(detailsPanel, BorderLayout.CENTER);
        
        // Add components to the main content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(mainContent, BorderLayout.CENTER);
        
        // Add content panel to dialog
        setContentPane(contentPanel);
        
        // Allow closing when clicking outside the popup
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                dispose();
            }
        });
        
        // Add drag functionality to move the popup
        FrameDragListener frameDragListener = new FrameDragListener(this);
        addMouseListener(frameDragListener);
        addMouseMotionListener(frameDragListener);
    }
    
    /**
     * Create the profile panel (left side)
     */
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                
                // Draw subtle border
                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
            }
        };
        
        panel.setLayout(new BorderLayout(0, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(280, HEIGHT - 60));
        
        // Create image panel (top)
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        
        // Create a panel to contain the image with margins
        JPanel imageBorderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                // Draw border
                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                // Draw image if available
                if (candidateImage != null) {
                    // Create a rounded rectangle clip
                    Shape roundRect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.setClip(roundRect);
                    
                    // Scale image to fill the box while maintaining aspect ratio
                    float imageRatio = (float) candidateImage.getWidth() / candidateImage.getHeight();
                    float boxRatio = (float) getWidth() / getHeight();
                    
                    int targetWidth, targetHeight;
                    
                    if (imageRatio > boxRatio) {
                        // Image is wider than the box (relative to height)
                        targetHeight = getHeight();
                        targetWidth = Math.round(targetHeight * imageRatio);
                    } else {
                        // Image is taller than the box (relative to width)
                        targetWidth = getWidth();
                        targetHeight = Math.round(targetWidth / imageRatio);
                    }
                    
                    // Center the image
                    int x = (getWidth() - targetWidth) / 2;
                    int y = (getHeight() - targetHeight) / 2;
                    
                    // Draw the image
                    g2d.drawImage(candidateImage, x, y, targetWidth, targetHeight, this);
                }
            }
        };
        imageBorderPanel.setOpaque(false);
        imageBorderPanel.setPreferredSize(new Dimension(profileImageWidth, profileImageHeight));
        
        // Add the image border panel to the image panel
        imagePanel.add(imageBorderPanel, BorderLayout.CENTER);
        
        // Create a panel for header with name
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        
        // Add name label (will be updated later)
        JLabel nameLabel = new JLabel("Candidate Profile");
        nameLabel.setFont(interSemiBold != null ? 
                         interSemiBold.deriveFont(18f) : 
                         new Font("Sans-Serif", Font.BOLD, 18));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(nameLabel);
        
        // Create a panel for scrollable profile sections
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        scrollContent.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        // Add placeholders for profile sections
        for (String section : PROFILE_SECTIONS) {
            JPanel sectionPanel = createProfileSection(section, "");
            sectionPanel.setName("section_" + section); // Tag with name for updating later
            scrollContent.add(sectionPanel);
            scrollContent.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // Create scrollpane for content
        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Apply minimal scrollbar UI
        scrollPane.getVerticalScrollBar().setUI(new MinimalScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        
        // Create content panel for header and scrollpane
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Add the header and scrollpane to the content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to panel
        panel.add(imagePanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create a profile section with label and value
     */
    private JPanel createProfileSection(String label, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add label
        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(interMedium != null ? 
                          interMedium.deriveFont(14f) : 
                          new Font("Sans-Serif", Font.BOLD, 14));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        
        // Add spacing
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Add value
        JLabel valueLabel = new JLabel(value.isEmpty() ? "No data available" : value);
        valueLabel.setFont(interRegular != null ? 
                         interRegular.deriveFont(14f) : 
                         new Font("Sans-Serif", Font.PLAIN, 14));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(valueLabel);
        
        return panel;
    }
    
    /**
     * Create the details panel (right side)
     */
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                
                // Draw subtle border
                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
            }
        };
        
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        
        // Create a panel for scrollable content
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        scrollContent.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Create scrollpane for content
        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Apply minimal scrollbar UI
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getVerticalScrollBar().setUI(new MinimalScrollBarUI());
        
        // Add scrollpane to panel
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Details sections will be added after loading candidate data
        
        return panel;
    }
    
    /**
     * Load candidate data from the data source
     */
    private void loadCandidateData() {
        CandidateDataLoader loader = new CandidateDataLoader();
        candidateData = loader.getCandidateByName(candidateName);
        
        if (candidateData == null) {
            System.err.println("No candidate data found for: " + candidateName);
            return;
        }
        
        // Update title with candidate name
        titleLabel.setText(candidateData.getName());
        
        // Load candidate image
        loadCandidateImage();
        
        // Update profile panel with basic info
        updateProfilePanel();
        
        // Update details panel with sections
        updateDetailsPanel();
    }
    
    /**
     * Load the candidate's image
     */
    private void loadCandidateImage() {
        try {
            // Try to load from candidate data
            String imagePath = candidateData.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    candidateImage = ImageIO.read(imageFile);
                    return;
                }
            }
            
            // If no image found, load default
            File defaultImage = new File("resources/images/defaultprofpic.png");
            if (defaultImage.exists()) {
                candidateImage = ImageIO.read(defaultImage);
            } else {
                // Create blank image as fallback
                candidateImage = new BufferedImage(profileImageWidth, profileImageHeight, BufferedImage.TYPE_INT_ARGB);
            }
        } catch (Exception e) {
            System.err.println("Error loading candidate image: " + e.getMessage());
            // Create blank image as fallback
            candidateImage = new BufferedImage(profileImageWidth, profileImageHeight, BufferedImage.TYPE_INT_ARGB);
        }
    }
    
    /**
     * Update the profile panel with basic candidate info
     */
    private void updateProfilePanel() {
        if (candidateData == null) return;
        
        // Get the profile panel (first component in the main content)
        JPanel mainContent = (JPanel) contentPanel.getComponent(1);
        JPanel profilePanel = (JPanel) mainContent.getComponent(0);
        
        // Update the name in the header
        JPanel contentPanel = (JPanel) profilePanel.getComponent(1);
        JPanel headerPanel = (JPanel) contentPanel.getComponent(0);
        if (headerPanel.getComponentCount() > 0 && headerPanel.getComponent(0) instanceof JLabel) {
            JLabel nameLabel = (JLabel) headerPanel.getComponent(0);
            nameLabel.setText(candidateData.getName());
        }
        
        // Update profile sections
        JScrollPane scrollPane = (JScrollPane) contentPanel.getComponent(1);
        JPanel scrollContent = (JPanel) scrollPane.getViewport().getView();
        updateProfileSections(scrollContent);
    }
    
    /**
     * Update profile sections with candidate data
     */
    private void updateProfileSections(JPanel contentPanel) {
        // For each profile section, find its panel and update the value
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel sectionPanel = (JPanel) comp;
                String name = sectionPanel.getName();
                
                if (name != null && name.startsWith("section_")) {
                    String sectionKey = name.substring("section_".length());
                    
                    // Get all components in this section panel
                    Component[] sectionComps = sectionPanel.getComponents();
                    
                    // Find the value label (should be the third component after title and spacing)
                    if (sectionComps.length >= 3 && sectionComps[2] instanceof JLabel) {
                        JLabel valueLabel = (JLabel) sectionComps[2];
                        String value = "";
                        
                        // Get value based on section key
                        switch (sectionKey) {
                            case "Position":
                                value = candidateData.getPosition();
                                break;
                            case "Age":
                                value = candidateData.getAge();
                                break;
                            case "Region":
                                value = candidateData.getRegion();
                                break;
                            case "Party Affiliation":
                                value = candidateData.getParty();
                                break;
                            case "Years of Experience":
                                // This field might not be directly available
                                value = "No data available";
                                break;
                            case "Campaign Slogan":
                                // This field might not be directly available
                                value = "No data available";
                                break;
                            default:
                                value = "No data available";
                        }
                        
                        valueLabel.setText(value != null && !value.isEmpty() ? value : "No data available");
                    }
                }
            }
        }
    }
    
    /**
     * Update the details panel with candidate information sections
     */
    private void updateDetailsPanel() {
        if (candidateData == null) return;
        
        // Get the details panel (second component in the main content)
        JPanel mainContent = (JPanel) contentPanel.getComponent(1);
        JPanel detailsPanel = (JPanel) mainContent.getComponent(1);
        
        // Get the scroll content panel
        JScrollPane scrollPane = (JScrollPane) detailsPanel.getComponent(0);
        JPanel scrollContent = (JPanel) scrollPane.getViewport().getView();
        
        // Section: Platforms
        if (candidateData.getPlatforms() != null && !candidateData.getPlatforms().isEmpty()) {
            List<String> platformsList = stringToList(candidateData.getPlatforms());
            addSection(scrollContent, "Platforms", platformsList);
        }
        
        // Section: Supported Issues
        if (candidateData.getSupportedIssues() != null && !candidateData.getSupportedIssues().isEmpty()) {
            List<String> supportedIssuesList = stringToList(candidateData.getSupportedIssues());
            addSection(scrollContent, "Supported Issues", supportedIssuesList);
        }
        
        // Section: Opposed Issues
        if (candidateData.getOpposedIssues() != null && !candidateData.getOpposedIssues().isEmpty()) {
            List<String> opposedIssuesList = stringToList(candidateData.getOpposedIssues());
            addSection(scrollContent, "Opposed Issues", opposedIssuesList);
        }
        
        // Section: Social Stances
        if (candidateData.getSocialStances() != null && !candidateData.getSocialStances().isEmpty()) {
            addSocialStancesSection(scrollContent);
        }
    }
    
    /**
     * Add a section with a title and list of items
     */
    private void addSection(JPanel parent, String title, List<String> items) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setOpaque(false);
        sectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        // Section title
        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(interSemiBold != null ? interSemiBold.deriveFont(16f) : new Font("Inter", Font.BOLD, 16));
        sectionTitle.setForeground(ACCENT_COLOR);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sectionPanel.add(sectionTitle);
        sectionPanel.add(Box.createVerticalStrut(10));
        
        // Add each item as a bullet point
        for (String item : items) {
            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new BorderLayout(10, 0));
            itemPanel.setOpaque(false);
            itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel bulletLabel = new JLabel("•");
            bulletLabel.setFont(interRegular != null ? interRegular.deriveFont(16f) : new Font("Inter", Font.BOLD, 16));
            bulletLabel.setForeground(ACCENT_COLOR);
            
            // Use HTML for text wrapping
            String wrappedText = "<html><body style='width: 100%'>" + item + "</body></html>";
            JLabel textLabel = new JLabel(wrappedText);
            textLabel.setFont(interRegular != null ? interRegular.deriveFont(14f) : new Font("Inter", Font.PLAIN, 14));
            textLabel.setForeground(TEXT_COLOR);
            
            itemPanel.add(bulletLabel, BorderLayout.WEST);
            itemPanel.add(textLabel, BorderLayout.CENTER);
            
            sectionPanel.add(itemPanel);
            sectionPanel.add(Box.createVerticalStrut(10));
        }
        
        // Add divider
        parent.add(sectionPanel);
        parent.add(createDivider());
    }
    
    /**
     * Add social stances section
     */
    private void addSocialStancesSection(JPanel parent) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setOpaque(false);
        sectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Section title
        JLabel sectionTitle = new JLabel("Social Stances");
        sectionTitle.setFont(interSemiBold != null ? interSemiBold.deriveFont(16f) : new Font("Inter", Font.BOLD, 16));
        sectionTitle.setForeground(ACCENT_COLOR);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sectionPanel.add(sectionTitle);
        sectionPanel.add(Box.createVerticalStrut(10));
        
        // Add each social stance with styled indicators
        for (String stance : candidateData.getSocialStances()) {
            String[] parts = stance.split(" - ");
            if (parts.length != 2) continue;
            
            String issue = parts[0].trim();
            String position = parts[1].trim();
            
            // Create a panel for each stance with position indicator
            JPanel stancePanel = new JPanel(new BorderLayout(10, 0));
            stancePanel.setOpaque(false);
            stancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Use HTML for text wrapping
            String wrappedText = "<html><body style='width: 100%'>" + issue + "</body></html>";
            JLabel issueLabel = new JLabel(wrappedText);
            issueLabel.setFont(interRegular != null ? interRegular.deriveFont(14f) : new Font("Inter", Font.PLAIN, 14));
            issueLabel.setForeground(TEXT_COLOR);
            
            JLabel positionIndicator = new JLabel(position);
            positionIndicator.setFont(interMedium != null ? interMedium.deriveFont(14f) : new Font("Inter", Font.BOLD, 14));
            
            // Set color based on position
            Color positionColor;
            switch (position) {
                case "Agree":
                    positionColor = new Color(0x22, 0xC5, 0x5E); // Green
                    break;
                case "Disagree":
                    positionColor = new Color(0xEF, 0x44, 0x44); // Red
                    break;
                case "Neutral":
                    positionColor = new Color(0xF5, 0x9E, 0x0B); // Orange
                    break;
                default:
                    positionColor = TEXT_COLOR; // Default text color
            }
            positionIndicator.setForeground(positionColor);
            
            stancePanel.add(issueLabel, BorderLayout.WEST);
            stancePanel.add(positionIndicator, BorderLayout.EAST);
            
            sectionPanel.add(stancePanel);
            sectionPanel.add(Box.createVerticalStrut(10));
        }
        
        parent.add(sectionPanel);
    }
    
    /**
     * Create a horizontal divider line
     */
    private JPanel createDivider() {
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(BORDER_COLOR);
                g2d.drawLine(0, 0, getWidth(), 0);
            }
        };
        divider.setOpaque(false);
        divider.setPreferredSize(new Dimension(WIDTH, 1));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        return divider;
    }
    
    /**
     * Helper method to convert a string to a list by splitting on semicolons or commas
     */
    private List<String> stringToList(String str) {
        List<String> result = new ArrayList<>();
        if (str == null || str.isEmpty()) {
            return result;
        }
        
        // Split by semicolons first, if none found, try commas
        String[] items;
        if (str.contains(";")) {
            items = str.split(";");
        } else {
            items = str.split(",");
        }
        
        // Add each non-empty item to the list
        for (String item : items) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        
        return result;
    }
    
    /**
     * Class to enable dragging the dialog
     */
    private static class FrameDragListener extends MouseAdapter {
        private final JDialog frame;
        private Point mouseDownCompCoords;

        public FrameDragListener(JDialog frame) {
            this.frame = frame;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            mouseDownCompCoords = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point currCoords = e.getLocationOnScreen();
            frame.setLocation(currCoords.x - mouseDownCompCoords.x, 
                              currCoords.y - mouseDownCompCoords.y);
        }
    }
    
    /**
     * Custom minimal scrollbar UI
     */
    private class MinimalScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), 100);
            this.trackColor = new Color(0, 0, 0, 0);
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fill rounded rectangle for thumb
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 1, 
                            thumbBounds.width - 2, thumbBounds.height - 2, 
                            8, 8);
            
            g2.dispose();
        }
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // Paint nothing for track
        }
    }
} 