package frontend.overview;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A vertical navigation bar that displays buttons for each position category
 */
public class NavigationBar extends JPanel {
    // UI Components
    private JPanel buttonsPanel;
    private JScrollPane scrollPane;
    
    // Fonts
    private Font interRegular;
    private Font interSemiBold;
    
    // Colors
    private Color primaryBlue = new Color(0x2B, 0x37, 0x80); // #2B3780
    private Color textColor = new Color(0x47, 0x55, 0x69); // #475569
    private Color lightGray = new Color(0xF1, 0xF5, 0xF9); // #F1F5F9
    private Color dividerColor = new Color(0xE2, 0xE8, 0xF0); // #E2E8F0
    private Color panelBackground = new Color(255, 255, 255, 245); // Slightly transparent white
    
    // Navigation callback
    private Consumer<String> onPositionSelected;
    
    // Currently selected position
    private String selectedPosition = null;
    
    /**
     * Create a navigation bar for positions
     * 
     * @param interRegular Regular font
     * @param interSemiBold SemiBold font
     * @param onPositionSelected Callback when a position is selected
     */
    public NavigationBar(Font interRegular, Font interSemiBold, Consumer<String> onPositionSelected) {
        this.interRegular = interRegular;
        this.interSemiBold = interSemiBold;
        this.onPositionSelected = onPositionSelected;
        
        // Initialize UI
        initializeUI();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        // Set up the main panel with BorderLayout
        setLayout(new BorderLayout());
        setOpaque(false);
        
        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Create title label
        JLabel titleLabel = new JLabel("Positions");
        titleLabel.setFont(interSemiBold.deriveFont(16f));
        titleLabel.setForeground(primaryBlue);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Create buttons panel
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);
        
        // Create scroll pane
        scrollPane = new JScrollPane(buttonsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Add components to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Remove border and background
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Set preferred size
        setPreferredSize(new Dimension(150, 200));
    }
    
    /**
     * Update the navigation bar with positions
     * 
     * @param positions List of position names
     */
    public void setPositions(List<String> positions) {
        // Clear existing buttons
        buttonsPanel.removeAll();
        
        // Add a button for each position
        for (String position : positions) {
            // Create button panel
            JPanel buttonPanel = createPositionButton(position);
            buttonsPanel.add(buttonPanel);
            buttonsPanel.add(Box.createRigidArea(new Dimension(0, 2))); // Reduced spacing between buttons
        }
        
        // Update UI
        revalidate();
        repaint();
    }
    
    /**
     * Set positions with a specific order
     * 
     * @param unsortedPositions List of position names that need ordering
     */
    public void setPositionsWithOrder(List<String> unsortedPositions) {
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
        List<String> sortedPositions = new ArrayList<>();
        
        // First add positions in the specified order if they exist in our data
        for (String position : orderedPositions) {
            if (unsortedPositions.contains(position)) {
                sortedPositions.add(position);
            }
        }
        
        // Then add any remaining positions alphabetically
        List<String> remainingPositions = new ArrayList<>(unsortedPositions);
        remainingPositions.removeAll(orderedPositions);
        Collections.sort(remainingPositions);
        sortedPositions.addAll(remainingPositions);
        
        // Set the sorted positions
        setPositions(sortedPositions);
    }
    
    /**
     * Create a button for a position
     * 
     * @param position Position name
     * @return Button panel
     */
    private JPanel createPositionButton(String position) {
        // Create button panel with hover effect
        JPanel buttonPanel = new JPanel() {
            private boolean isHovered = false;
            
            {
                // Make the entire panel detect mouse events
                enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
                
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
                        // Set as selected
                        selectedPosition = position;
                        
                        // Notify callback
                        if (onPositionSelected != null) {
                            onPositionSelected.accept(position);
                        }
                        
                        // Update all buttons
                        updateButtonStates();
                    }
                });
                
                // Add mouse motion listener for better hover detection
                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        if (!isHovered) {
                            isHovered = true;
                            setCursor(new Cursor(Cursor.HAND_CURSOR));
                            repaint();
                        }
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Draw hover effect if hovered - fill the entire panel area
                if (isHovered) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(0xF1, 0xF5, 0xF9)); // Light gray background
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
            }
        };
        
        // Set layout and properties
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8)); // Increased vertical padding
        
        // Create text area for wrapped text instead of label
        JTextArea textArea = new JTextArea(position);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setOpaque(false);
        textArea.setBorder(null);
        
        // Set font and color based on selection state - always use interRegular except for selected
        if (position.equals(selectedPosition)) {
            textArea.setFont(interSemiBold.deriveFont(14f)); // Selected items use semibold
            textArea.setForeground(primaryBlue);
        } else {
            textArea.setFont(interRegular.deriveFont(14f)); // Non-selected items use regular
            textArea.setForeground(textColor);
        }
        
        // Add text area to button panel
        buttonPanel.add(textArea, BorderLayout.CENTER);
        
        // Set minimum height to ensure larger clickable area
        int preferredHeight = textArea.getPreferredSize().height;
        buttonPanel.setMinimumSize(new Dimension(0, 45)); // Minimum height of 45px
        buttonPanel.setPreferredSize(new Dimension(buttonPanel.getPreferredSize().width, Math.max(preferredHeight + 20, 45)));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(preferredHeight + 20, 45)));
        
        return buttonPanel;
    }
    
    /**
     * Update the visual state of all buttons
     */
    private void updateButtonStates() {
        // Repaint all buttons
        for (Component comp : buttonsPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                // Find the text area component
                for (Component c : panel.getComponents()) {
                    if (c instanceof JTextArea) {
                        JTextArea textArea = (JTextArea) c;
                        String position = textArea.getText();
                        
                        // Update font and color based on selection
                        if (position.equals(selectedPosition)) {
                            textArea.setFont(interSemiBold.deriveFont(14f)); // Selected items use semibold
                            textArea.setForeground(primaryBlue);
                        } else {
                            textArea.setFont(interRegular.deriveFont(14f)); // Non-selected items use regular
                            textArea.setForeground(textColor);
                        }
                        
                        // Recalculate preferred height
                        int preferredHeight = textArea.getPreferredSize().height;
                        panel.setMinimumSize(new Dimension(0, 45)); // Minimum height of 45px
                        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, Math.max(preferredHeight + 20, 45)));
                        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(preferredHeight + 20, 45)));
                    }
                }
                panel.revalidate();
                panel.repaint();
            }
        }
    }
    
    /**
     * Set the selected position
     * 
     * @param position Position name
     */
    public void setSelectedPosition(String position) {
        this.selectedPosition = position;
        updateButtonStates();
    }
    
    /**
     * Set the navigation bar's preferred size
     * 
     * @param width Bar width
     * @param height Bar height
     */
    public void setBarSize(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        revalidate();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        // No background painting - completely transparent
        super.paintComponent(g);
    }
} 