package frontend.comparison;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Component for displaying focused advocacies comparison between two candidates
 */
public class FocusedAdvocaciesCompare extends JPanel {
    // Colors
    private Color bgColor = new Color(0xF7, 0xF9, 0xFC); // Light background color
    private Color textColor = new Color(0x47, 0x55, 0x69); // Text color
    private Color borderColor = new Color(0xE5, 0xE7, 0xEB); // Border color
    private Color orangeAccent = new Color(0xF9, 0xB4, 0x47); // Orange accent #F9B447
    
    // Fonts
    private Font interRegular;
    private Font interMedium;
    private Font interSemiBold;
    
    // Candidate data
    private String leftCandidateName = "";
    private String rightCandidateName = "";
    private BufferedImage leftCandidateImage;
    private BufferedImage rightCandidateImage;
    
    // Content areas
    private JPanel leftContentPanel;
    private JPanel rightContentPanel;
    
    // Profile image dimensions (adjustable)
    private int profileImageWidth = 250;
    private int profileImageHeight = 120;
    
    // Advocacy section keys
    private static final String[] ADVOCACY_SECTIONS = {
        "Platforms", "Supported Issues", "Opposed Issues", "Notable Laws"
    };
    
    /**
     * Creates the focused advocacies comparison component
     * @param interRegular Regular font
     * @param interMedium Medium font
     * @param interSemiBold SemiBold font
     */
    public FocusedAdvocaciesCompare(Font interRegular, Font interMedium, Font interSemiBold) {
        this.interRegular = interRegular;
        this.interMedium = interMedium;
        this.interSemiBold = interSemiBold;
        
        setLayout(new BorderLayout(20, 0));
        setOpaque(false);
        
        // Create the side-by-side panels
        createComparisonPanels();
        
        // Load placeholder images
        loadPlaceholderImages();
    }
    
    /**
     * Create the side-by-side comparison panels
     */
    private void createComparisonPanels() {
        // Create container for the two panels with some spacing
        JPanel container = new JPanel(new GridLayout(1, 2, 20, 0));
        container.setOpaque(false);
        
        // Create left candidate panel
        leftContentPanel = createCandidatePanel();
        container.add(leftContentPanel);
        
        // Create right candidate panel
        rightContentPanel = createCandidatePanel();
        container.add(rightContentPanel);
        
        // Add to this panel
        add(container, BorderLayout.CENTER);
    }
    
    /**
     * Create an individual candidate panel
     */
    private JPanel createCandidatePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw subtle border
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        
        panel.setLayout(new BorderLayout(0, 0));
        panel.setOpaque(false);
        
        // Create image panel (left side) with rounded borders
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);
        
        // Create a panel to contain the image with margins
        JPanel imageBorderPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // This panel will be responsible for drawing the rounded rectangle around the image
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background for image
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        imageBorderPanel.setOpaque(false);
        
        // Set the size of the image panel
        imageBorderPanel.setPreferredSize(new Dimension(profileImageWidth, profileImageHeight));
        
        // Add the image border panel to the image panel with padding
        imagePanel.add(imageBorderPanel, BorderLayout.CENTER);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 0));
        
        // Create content panel (right side)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Create a panel for the header with name
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        
        // Add name label
        JLabel nameLabel = new JLabel("Select a candidate");
        nameLabel.setFont(interSemiBold != null ? 
                          interSemiBold.deriveFont(16f) : 
                          new Font("Sans-Serif", Font.BOLD, 16));
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(nameLabel);
        
        // Add tagline
        JLabel tagLine = new JLabel("Key advocacy platforms:");
        tagLine.setFont(interRegular != null ? 
                          interRegular.deriveFont(12f) : 
                          new Font("Sans-Serif", Font.ITALIC, 12));
        tagLine.setForeground(new Color(0x64, 0x64, 0x64));
        tagLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(tagLine);
        
        // Create a panel for scrollable content
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        scrollContent.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        // Create placeholder panels for each advocacy section
        for (String section : ADVOCACY_SECTIONS) {
            JPanel sectionPanel = createBulletListPanel(section, new String[0]);
            sectionPanel.setName("section_" + section);
            scrollContent.add(sectionPanel);
            scrollContent.add(Box.createRigidArea(new Dimension(0, 15)));
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
        
        // Add the header and scrollpane to the content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to panel
        panel.add(imagePanel, BorderLayout.WEST);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create a section with title and bullet points
     */
    private JPanel createBulletListPanel(String title, String[] items) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(interMedium != null ? 
                           interMedium.deriveFont(14f) : 
                           new Font("Sans-Serif", Font.BOLD, 14));
        titleLabel.setForeground(orangeAccent);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        
        // Add spacing
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Container for bullet items (for updating later)
        JPanel itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setOpaque(false);
        itemsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add items if provided
        if (items.length > 0) {
            for (String item : items) {
                itemsContainer.add(createBulletItem(item));
                itemsContainer.add(Box.createRigidArea(new Dimension(0, 3)));
            }
        } else {
            // Add placeholder "No data" text
            JLabel noDataLabel = new JLabel("No data available");
            noDataLabel.setFont(interRegular != null ? 
                              interRegular.deriveFont(14f) : 
                              new Font("Sans-Serif", Font.ITALIC, 14));
            noDataLabel.setForeground(textColor);
            noDataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            itemsContainer.add(noDataLabel);
        }
        
        panel.add(itemsContainer);
        
        return panel;
    }
    
    /**
     * Create a single bullet point item
     */
    private JPanel createBulletItem(String text) {
        JPanel itemPanel = new JPanel(new BorderLayout(5, 0));
        itemPanel.setOpaque(false);
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Remove fixed height to allow for text wrapping
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        // Bullet point
        JLabel bulletLabel = new JLabel("\u2022"); // Unicode bullet character
        bulletLabel.setFont(interRegular != null ? 
                          interRegular.deriveFont(14f) : 
                          new Font("Sans-Serif", Font.PLAIN, 14));
        bulletLabel.setForeground(textColor);
        bulletLabel.setVerticalAlignment(JLabel.TOP); // Align to top for multi-line text
        
        // Create a text area for wrapped text instead of a label
        JTextArea itemText = new JTextArea(text);
        itemText.setFont(interRegular != null ? 
                        interRegular.deriveFont(14f) : 
                        new Font("Sans-Serif", Font.PLAIN, 14));
        itemText.setForeground(textColor);
        itemText.setWrapStyleWord(true);
        itemText.setLineWrap(true);
        itemText.setEditable(false);
        itemText.setOpaque(false);
        itemText.setBorder(null);
        itemText.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        
        // Add components
        itemPanel.add(bulletLabel, BorderLayout.WEST);
        itemPanel.add(itemText, BorderLayout.CENTER);
        
        // Add some padding below each item
        itemPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        return itemPanel;
    }
    
    /**
     * Load placeholder images
     */
    private void loadPlaceholderImages() {
        // Get default profile image from CandidateDataManager
        leftCandidateImage = CandidateDataManager.getDefaultProfileImage();
        rightCandidateImage = CandidateDataManager.getDefaultProfileImage();
    }
    
    /**
     * Set the profile image dimensions
     * @param width Width of the profile image
     * @param height Height of the profile image
     */
    public void setProfileImageDimensions(int width, int height) {
        this.profileImageWidth = width;
        this.profileImageHeight = height;
        
        // Update the panels with new dimensions
        updateImagePanelSizes(leftContentPanel);
        updateImagePanelSizes(rightContentPanel);
        
        revalidate();
        repaint();
    }
    
    /**
     * Update the size of the image panel in a content panel
     * @param contentPanel The panel containing the image panel to update
     */
    private void updateImagePanelSizes(JPanel contentPanel) {
        if (contentPanel != null) {
            Component[] components = contentPanel.getComponents();
            
            // Find the image panel (should be the first component)
            if (components.length > 0 && components[0] instanceof JPanel) {
                JPanel imagePanel = (JPanel) components[0];
                
                // Find the image border panel within the image panel
                Component[] imagePanelComps = imagePanel.getComponents();
                for (Component comp : imagePanelComps) {
                    if (comp instanceof JPanel) {
                        JPanel imageBorderPanel = (JPanel) comp;
                        imageBorderPanel.setPreferredSize(new Dimension(profileImageWidth, profileImageHeight));
                    }
                }
            }
        }
    }
    
    /**
     * Set the left candidate data
     * @param name Candidate name
     * @param image Candidate image
     */
    public void setLeftCandidate(String name, BufferedImage image) {
        this.leftCandidateName = name;
        if (image != null) {
            this.leftCandidateImage = image;
        }
        updateLeftPanel();
    }
    
    /**
     * Set the right candidate data
     * @param name Candidate name
     * @param image Candidate image
     */
    public void setRightCandidate(String name, BufferedImage image) {
        this.rightCandidateName = name;
        if (image != null) {
            this.rightCandidateImage = image;
        }
        updateRightPanel();
    }
    
    /**
     * Update the left panel content
     */
    private void updateLeftPanel() {
        if (leftContentPanel != null) {
            Component[] components = leftContentPanel.getComponents();
            
            // Update image panel
            if (components.length > 0 && components[0] instanceof JPanel) {
                JPanel imagePanel = (JPanel) components[0];
                
                // Find the image border panel
                Component[] imagePanelComps = imagePanel.getComponents();
                for (Component comp : imagePanelComps) {
                    if (comp instanceof JPanel) {
                        final JPanel imageBorderPanel = (JPanel) comp;
                        imageBorderPanel.removeAll();
                        
                        // Create image draw panel
                        if (leftCandidateImage != null) {
                            JPanel imageDrawPanel = new JPanel() {
                                @Override
                                protected void paintComponent(Graphics g) {
                                    super.paintComponent(g);
                                    if (leftCandidateImage != null) {
                                        Graphics2D g2d = (Graphics2D) g;
                                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                        
                                        // Create a rounded rectangle clip
                                        Shape roundRect = new java.awt.geom.RoundRectangle2D.Float(
                                            0, 0, getWidth(), getHeight(), 8, 8);
                                        g2d.setClip(roundRect);
                                        
                                        // Scale image to fill the box while maintaining aspect ratio
                                        float imageRatio = (float) leftCandidateImage.getWidth() / leftCandidateImage.getHeight();
                                        float boxRatio = (float) getWidth() / getHeight();
                                        
                                        int targetWidth, targetHeight;
                                        
                                        if (imageRatio > boxRatio) {
                                            // Image is wider than the box (relative to height)
                                            // Scale based on height, width will overflow
                                            targetHeight = getHeight();
                                            targetWidth = Math.round(targetHeight * imageRatio);
                                        } else {
                                            // Image is taller than the box (relative to width)
                                            // Scale based on width, height will overflow
                                            targetWidth = getWidth();
                                            targetHeight = Math.round(targetWidth / imageRatio);
                                        }
                                        
                                        // Center the image (may be partially off-screen due to filling)
                                        int x = (getWidth() - targetWidth) / 2;
                                        int y = (getHeight() - targetHeight) / 2;
                                        
                                        // Draw the image
                                        g2d.drawImage(leftCandidateImage, x, y, targetWidth, targetHeight, this);
                                    }
                                }
                            };
                            imageDrawPanel.setOpaque(false);
                            imageBorderPanel.add(imageDrawPanel, BorderLayout.CENTER);
                        }
                    }
                }
                
                imagePanel.revalidate();
                imagePanel.repaint();
            }
            
            // Update content panel - name label and advocacy sections
            if (components.length > 1 && components[1] instanceof JPanel) {
                JPanel contentPanel = (JPanel) components[1];
                
                // Find the header panel with name label
                if (contentPanel.getLayout() instanceof BorderLayout) {
                    Component headerComp = ((BorderLayout) contentPanel.getLayout()).getLayoutComponent(BorderLayout.NORTH);
                    if (headerComp instanceof JPanel) {
                        JPanel headerPanel = (JPanel) headerComp;
                        if (headerPanel.getComponentCount() > 0 && headerPanel.getComponent(0) instanceof JLabel) {
                            JLabel nameLabel = (JLabel) headerPanel.getComponent(0);
                            nameLabel.setText(leftCandidateName.isEmpty() ? "Select a candidate" : leftCandidateName);
                        }
                    }
                    
                    // Find the scroll pane
                    Component centerComp = ((BorderLayout) contentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    if (centerComp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) centerComp;
                        Component viewComp = scrollPane.getViewport().getView();
                        if (viewComp instanceof JPanel) {
                            JPanel scrollContent = (JPanel) viewComp;
                            
                            // If a candidate is selected, update advocacy section values
                            if (!leftCandidateName.isEmpty()) {
                                updateAdvocacySections(scrollContent, leftCandidateName);
                            }
                        }
                    }
                }
                
                contentPanel.revalidate();
                contentPanel.repaint();
            }
            
            leftContentPanel.revalidate();
            leftContentPanel.repaint();
        }
    }
    
    /**
     * Update the right panel content
     */
    private void updateRightPanel() {
        if (rightContentPanel != null) {
            Component[] components = rightContentPanel.getComponents();
            
            // Update image panel
            if (components.length > 0 && components[0] instanceof JPanel) {
                JPanel imagePanel = (JPanel) components[0];
                
                // Find the image border panel
                Component[] imagePanelComps = imagePanel.getComponents();
                for (Component comp : imagePanelComps) {
                    if (comp instanceof JPanel) {
                        final JPanel imageBorderPanel = (JPanel) comp;
                        imageBorderPanel.removeAll();
                        
                        // Create image draw panel
                        if (rightCandidateImage != null) {
                            JPanel imageDrawPanel = new JPanel() {
                                @Override
                                protected void paintComponent(Graphics g) {
                                    super.paintComponent(g);
                                    if (rightCandidateImage != null) {
                                        Graphics2D g2d = (Graphics2D) g;
                                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                        
                                        // Create a rounded rectangle clip
                                        Shape roundRect = new java.awt.geom.RoundRectangle2D.Float(
                                            0, 0, getWidth(), getHeight(), 8, 8);
                                        g2d.setClip(roundRect);
                                        
                                        // Scale image to fill the box while maintaining aspect ratio
                                        float imageRatio = (float) rightCandidateImage.getWidth() / rightCandidateImage.getHeight();
                                        float boxRatio = (float) getWidth() / getHeight();
                                        
                                        int targetWidth, targetHeight;
                                        
                                        if (imageRatio > boxRatio) {
                                            // Image is wider than the box (relative to height)
                                            // Scale based on height, width will overflow
                                            targetHeight = getHeight();
                                            targetWidth = Math.round(targetHeight * imageRatio);
                                        } else {
                                            // Image is taller than the box (relative to width)
                                            // Scale based on width, height will overflow
                                            targetWidth = getWidth();
                                            targetHeight = Math.round(targetWidth / imageRatio);
                                        }
                                        
                                        // Center the image (may be partially off-screen due to filling)
                                        int x = (getWidth() - targetWidth) / 2;
                                        int y = (getHeight() - targetHeight) / 2;
                                        
                                        // Draw the image
                                        g2d.drawImage(rightCandidateImage, x, y, targetWidth, targetHeight, this);
                                    }
                                }
                            };
                            imageDrawPanel.setOpaque(false);
                            imageBorderPanel.add(imageDrawPanel, BorderLayout.CENTER);
                        }
                    }
                }
                
                imagePanel.revalidate();
                imagePanel.repaint();
            }
            
            // Update content panel - name label and advocacy sections
            if (components.length > 1 && components[1] instanceof JPanel) {
                JPanel contentPanel = (JPanel) components[1];
                
                // Find the header panel with name label
                if (contentPanel.getLayout() instanceof BorderLayout) {
                    Component headerComp = ((BorderLayout) contentPanel.getLayout()).getLayoutComponent(BorderLayout.NORTH);
                    if (headerComp instanceof JPanel) {
                        JPanel headerPanel = (JPanel) headerComp;
                        if (headerPanel.getComponentCount() > 0 && headerPanel.getComponent(0) instanceof JLabel) {
                            JLabel nameLabel = (JLabel) headerPanel.getComponent(0);
                            nameLabel.setText(rightCandidateName.isEmpty() ? "Select a candidate" : rightCandidateName);
                        }
                    }
                    
                    // Find the scroll pane
                    Component centerComp = ((BorderLayout) contentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    if (centerComp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) centerComp;
                        Component viewComp = scrollPane.getViewport().getView();
                        if (viewComp instanceof JPanel) {
                            JPanel scrollContent = (JPanel) viewComp;
                            
                            // If a candidate is selected, update advocacy section values
                            if (!rightCandidateName.isEmpty()) {
                                updateAdvocacySections(scrollContent, rightCandidateName);
                            }
                        }
                    }
                }
                
                contentPanel.revalidate();
                contentPanel.repaint();
            }
            
            rightContentPanel.revalidate();
            rightContentPanel.repaint();
        }
    }
    
    /**
     * Update advocacy sections with candidate data
     * @param contentPanel The content panel containing section components
     * @param candidateName The name of the candidate
     */
    private void updateAdvocacySections(JPanel contentPanel, String candidateName) {
        // For each advocacy section, find its panel and update the items
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel sectionPanel = (JPanel) comp;
                String name = sectionPanel.getName();
                
                if (name != null && name.startsWith("section_")) {
                    String sectionKey = name.substring("section_".length());
                    
                    // Get all components in this section
                    Component[] sectionComps = sectionPanel.getComponents();
                    
                    // Find the items container (should be the last component)
                    if (sectionComps.length >= 3 && sectionComps[2] instanceof JPanel) {
                        JPanel itemsContainer = (JPanel) sectionComps[2];
                        
                        // Clear existing items
                        itemsContainer.removeAll();
                        
                        // Get data from CandidateDataManager based on section
                        List<String> items = null;
                        
                        switch (sectionKey) {
                            case "Platforms":
                                items = CandidateDataManager.getCandidatePlatforms(candidateName);
                                break;
                            case "Supported Issues":
                                items = CandidateDataManager.getCandidateSupportedIssues(candidateName);
                                break;
                            case "Opposed Issues":
                                items = CandidateDataManager.getCandidateOpposedIssues(candidateName);
                                break;
                            case "Notable Laws":
                                items = CandidateDataManager.getCandidateNotableLaws(candidateName);
                                // Also check "Notable Laws Enacted" if no data
                                if (items.isEmpty()) {
                                    items = parseNotableLaws(candidateName);
                                }
                                break;
                        }
                        
                        // Add items to container
                        if (items != null && !items.isEmpty()) {
                            for (String item : items) {
                                itemsContainer.add(createBulletItem(item));
                                itemsContainer.add(Box.createRigidArea(new Dimension(0, 3)));
                            }
                        } else {
                            // Add "No data" text
                            JLabel noDataLabel = new JLabel("No data available");
                            noDataLabel.setFont(interRegular != null ? 
                                              interRegular.deriveFont(14f) : 
                                              new Font("Sans-Serif", Font.ITALIC, 14));
                            noDataLabel.setForeground(textColor);
                            noDataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                            itemsContainer.add(noDataLabel);
                        }
                        
                        itemsContainer.revalidate();
                        itemsContainer.repaint();
                    }
                }
            }
        }
    }
    
    /**
     * Parse "Notable Laws Enacted" attribute which uses a different format in some data entries
     */
    private List<String> parseNotableLaws(String candidateName) {
        String notableLaws = CandidateDataManager.getCandidateAttribute(candidateName, "Notable Laws Enacted");
        if (notableLaws.equals("No Data")) {
            return List.of();
        }
        
        return CandidateDataManager.parseListAttribute(candidateName, "Notable Laws Enacted");
    }
} 