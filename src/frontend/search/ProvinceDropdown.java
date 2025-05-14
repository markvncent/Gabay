package frontend.search;

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
    private JPanel dropdownContent;
    private JScrollPane scrollPane;
    private JPanel containerPanel;
    
    // State variables
    private boolean isOpen = false;
    
    // Styling properties
    private final Color primaryBlue = new Color(0x2B, 0x37, 0x80); // #2B3780
    private final Color hoverBlue = new Color(0x22, 0x2C, 0x66); // Darker blue for hover
    private final Color searchBorderColor = new Color(0xCB, 0xD5, 0xE1); // #CBD5E1 - matching search box
    private final int CORNER_RADIUS = 8; // Corner radius for dropdown elements
    
    // Selection callback
    private Consumer<String> onSelectionChanged;
    
    // Fonts
    private Font interMedium;
    private Font interRegular;
    
    // Currently selected province option
    private String selectedProvince = null;
    
    // Regions array
    private final String[] regions = {
        "Region I (Ilocos Region)",
        "Region II (Cagayan Valley)",
        "Region III (Central Luzon)",
        "Region IV-A (CALABARZON)",
        "Region IV-B (MIMAROPA)",
        "Region V (Bicol Region)",
        "Region VI (Western Visayas)",
        "Region VII (Central Visayas)",
        "Region VIII (Eastern Visayas)",
        "Region IX (Zamboanga Peninsula)",
        "Region X (Northern Mindanao)",
        "Region XI (Davao Region)",
        "Region XII (SOCCSKSARGEN)",
        "Region XIII (Caraga)",
        "NCR (National Capital Region)",
        "CAR (Cordillera Administrative Region)",
        "BARMM (Bangsamoro Autonomous Region in Muslim Mindanao)"
    };
    
    // Global mouse listener for clicks outside the dropdown
    private MouseAdapter globalClickListener;
    
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
        
        // Create the rectangle for the dropdown
        provinceRectangle = createProvinceRectangle();
    }
    
    /**
     * Creates the province rectangle with white background, border and arrow icons
     */
    private JPanel createProvinceRectangle() {
        // Use same border color as text fields
        final Color borderColor = searchBorderColor;
        
        // Track mouse states with boolean flags
        final boolean[] isHovering = {false};
        final boolean[] isClicking = {false};
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill with white background using rounded corners
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                
                // Draw border with same color as text fields
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, CORNER_RADIUS, CORNER_RADIUS);
                
                // Draw the selected text in default text color, not white
                g2d.setColor(isHovering[0] ? Color.BLACK : Color.GRAY);
                
                // Use provided font or fall back
                Font font = interRegular != null ? 
                    interRegular.deriveFont(13f) : 
                    new Font("Sans-Serif", Font.PLAIN, 13);
                g2d.setFont(font);
                
                // Determine text to display
                String displayText = selectedProvince != null ? selectedProvince : "Select Region";
                
                // Draw the text with left padding
                FontMetrics fm = g2d.getFontMetrics();
                int textHeight = fm.getHeight();
                int textY = (getHeight() - textHeight) / 2 + fm.getAscent();
                g2d.drawString(displayText, 10, textY);
                
                // Draw dropdown arrow in gray
                int arrowSize = 5;
                int arrowX = getWidth() - arrowSize * 3;
                int arrowY = getHeight() / 2;
                    
                // Draw triangle pointing down (or up if open)
                g2d.setColor(new Color(0x94, 0xA3, 0xB8)); // Gray arrow
                int[] xPoints = {arrowX - arrowSize, arrowX + arrowSize, arrowX};
                int[] yPoints;
                if (isOpen) {
                    // Arrow pointing up when open
                    yPoints = new int[]{arrowY + arrowSize / 2, arrowY + arrowSize / 2, arrowY - arrowSize / 2};
                } else {
                    // Arrow pointing down when closed
                    yPoints = new int[]{arrowY - arrowSize / 2, arrowY - arrowSize / 2, arrowY + arrowSize / 2};
                }
                g2d.fillPolygon(xPoints, yPoints, 3);
            }
        };
        
        // Set cursor to hand to indicate it's clickable
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setOpaque(false);
        panel.setName("province-rectangle");
        
        // Set preferred size
        panel.setPreferredSize(new Dimension(217, 45));
        
        // Add mouse listeners for interaction
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovering[0] = true;
                panel.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovering[0] = false;
                isClicking[0] = false; // Reset clicking state when mouse exits
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
                toggleDropdown();
            }
        });
        
        return panel;
    }
    
    /**
     * Toggle the dropdown visibility
     */
    public void toggleDropdown() {
        if (isOpen) {
            closeDropdown();
            } else {
            openDropdown();
                    }
                }
                
    /**
     * Open the dropdown
     */
    private void openDropdown() {
        if (isOpen) return;
        isOpen = true;
        
        // Get the rectangle position
        Rectangle rectBounds = provinceRectangle.getBounds();
            
        // Create dropdown content panel
        dropdownContent = new JPanel();
        dropdownContent.setLayout(new BoxLayout(dropdownContent, BoxLayout.Y_AXIS));
        dropdownContent.setBackground(Color.WHITE);
        dropdownContent.setName("province-dropdown-content");
            
        // Add region options
        for (String region : regions) {
            JPanel optionPanel = createRegionOption(region);
            dropdownContent.add(optionPanel);
                    }
                
        // Create scrollable panel
        scrollPane = new JScrollPane(dropdownContent);
                scrollPane.setName("province-dropdown-scrollpane");
                scrollPane.setBorder(BorderFactory.createEmptyBorder());
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
                
                // Customize scrollbar appearance
                scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                    @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(0xE2, 0xE8, 0xF0);
                this.trackColor = Color.WHITE;
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
                        return button;
                    }
                });
                
        // Set bounds for dropdown - same width as the rectangle, height based on content
        int dropdownHeight = Math.min(350, regions.length * 35); // Limit height
        scrollPane.setBounds(rectBounds.x, rectBounds.y + rectBounds.height, rectBounds.width, dropdownHeight);
                
        // Add to parent panel
        containerPanel.add(scrollPane);
        containerPanel.setComponentZOrder(scrollPane, 0); // Put on top
        containerPanel.revalidate();
                    containerPanel.repaint();
        
        // Repaint the rectangle to show the open state
        provinceRectangle.repaint();
                
        // Add click listener to close when clicking outside
        addGlobalClickListener();
    }
    
    /**
     * Close the dropdown
     */
    public void closeDropdown() {
        if (!isOpen) return;
        isOpen = false;
        
        // Remove dropdown content
        if (scrollPane != null) {
            containerPanel.remove(scrollPane);
            scrollPane = null;
            dropdownContent = null;
            containerPanel.revalidate();
                    containerPanel.repaint();
        }
        
        // Repaint the rectangle to show the closed state
        provinceRectangle.repaint();
        
        // Remove the global click listener
        removeGlobalClickListener();
        }
        
    /**
     * Create a region option panel
     */
    private JPanel createRegionOption(String region) {
        JPanel option = new JPanel(new BorderLayout());
        option.setBackground(Color.WHITE);
        option.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        option.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JLabel label = new JLabel(region);
        if (interRegular != null) {
            label.setFont(interRegular.deriveFont(13f));
        } else {
            label.setFont(new Font("Sans-Serif", Font.PLAIN, 13));
        }
        label.setForeground(new Color(0x47, 0x55, 0x69));
        option.add(label, BorderLayout.WEST);
        
        // Add hover effect
        option.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                option.setBackground(new Color(0xF1, 0xF5, 0xF9));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                option.setBackground(Color.WHITE);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedProvince = region;
                onSelectionChanged.accept(region);
                closeDropdown();
                provinceRectangle.repaint();
            }
        });
        
        return option;
    }
    
    /**
     * Global click listener to close dropdown when clicking outside
     */
    private void addGlobalClickListener() {
        if (globalClickListener == null) {
            globalClickListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Component clicked = e.getComponent();
                    boolean clickedOnDropdown = false;
                    
                    // Check if the click was on the dropdown or its components
                    if (clicked == provinceRectangle) {
                        clickedOnDropdown = true;
                    } else if (scrollPane != null) {
                        Component clickedComponent = SwingUtilities.getDeepestComponentAt(scrollPane, e.getX(), e.getY());
                        if (clickedComponent != null) {
                            clickedOnDropdown = true;
                        }
                    }
                    
                    if (!clickedOnDropdown) {
                        closeDropdown();
                    }
                }
            };
            
            // Add listener to parent panel
            containerPanel.addMouseListener(globalClickListener);
        }
    }
    
    private void removeGlobalClickListener() {
        if (globalClickListener != null) {
            containerPanel.removeMouseListener(globalClickListener);
            globalClickListener = null;
        }
    }
    
    /**
     * Update the position of the dropdown when the container is resized
     */
    public void updatePositionOnResize() {
        if (isOpen && scrollPane != null) {
            Rectangle bounds = provinceRectangle.getBounds();
            scrollPane.setBounds(bounds.x, bounds.y + bounds.height, bounds.width, scrollPane.getHeight());
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
} 