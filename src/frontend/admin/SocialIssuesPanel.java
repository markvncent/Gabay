package frontend.admin;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * SocialIssuesPanel - A modular component for displaying and selecting social issue stances
 * This can be reused in different parts of the application where social stances need to be displayed or edited.
 */
public class SocialIssuesPanel extends JPanel {
    private final int width;
    private final Font regularFont;
    private final Font mediumFont;
    private final JPanel contentPanel;
    private final JScrollPane scrollPane;
    
    // List of social stances
    private final Map<String, String> selectedStances = new HashMap<>();
    
    // Define stance options
    private final String[] STANCE_OPTIONS = {"Agree", "Disagree", "Neutral", "No Data"};
    
    // Define stance colors
    private final Color AGREE_COLOR = new Color(0x10, 0xB9, 0x81); // Green
    private final Color DISAGREE_COLOR = new Color(0xEF, 0x44, 0x44); // Red
    private final Color NEUTRAL_COLOR = new Color(0x94, 0xA3, 0xB8); // Gray-blue
    private final Color NO_DATA_COLOR = new Color(0xE2, 0xE8, 0xF0); // Light gray
    
    // Define the list of social issues
    private final String[] SOCIAL_ISSUES = {
        "Legalization of Divorce",
        "Passing the SOGIE Equality Bill",
        "Reinstating the Death Penalty",
        "Lowering the Age of Criminal Responsibility",
        "Federalism",
        "Mandatory ROTC for Senior High Students",
        "Same-Sex Marriage",
        "Anti-Terror Law",
        "Jeepney Modernization Program",
        "Foreign Investment in Land Ownership",
        "Universal Healthcare Funding",
        "Mandatory Sex Education",
        "Minimum Wage Standardization"
    };
    
    /**
     * Creates a new SocialIssuesPanel
     * 
     * @param width      The width of the panel
     * @param regularFont The regular font to use
     * @param mediumFont  The medium font to use
     */
    public SocialIssuesPanel(int width, Font regularFont, Font mediumFont) {
        this.width = width;
        this.regularFont = regularFont;
        this.mediumFont = mediumFont;
        
        // Use BorderLayout for the main panel
        setLayout(new BorderLayout());
        setOpaque(false);
        
        // Create content panel with all the issues
        contentPanel = createContentPanel();
        
        // Create scroll pane
        scrollPane = createScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Creates the panel with all social issues
     */
    private JPanel createContentPanel() {
        // Create a panel with vertical BoxLayout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        // Add issue rows
        for (int i = 0; i < SOCIAL_ISSUES.length; i++) {
            String issue = SOCIAL_ISSUES[i];
            
            // Add the issue row
            panel.add(createIssueRow(issue));
            
            // Add separator if not the last item
            if (i < SOCIAL_ISSUES.length - 1) {
                panel.add(createSeparator());
            }
        }
        
        return panel;
    }
    
    /**
     * Creates a single issue row with stance buttons
     */
    private JPanel createIssueRow(String issue) {
        int rowHeight = 40;
        
        // Create row panel
        JPanel issueRow = new JPanel(null);
        issueRow.setOpaque(false);
        issueRow.setPreferredSize(new Dimension(width, rowHeight));
        issueRow.setMaximumSize(new Dimension(width, rowHeight));
        issueRow.setMinimumSize(new Dimension(width, rowHeight));
        
        // Create issue label
        JLabel issueLabel = new JLabel(issue);
        issueLabel.setBounds(0, 0, width / 2, rowHeight);
        issueLabel.setForeground(new Color(0x47, 0x55, 0x69)); // #475569
        issueLabel.setFont(regularFont != null ? 
                         regularFont.deriveFont(14f) : 
                         new Font("Sans-Serif", Font.PLAIN, 14));
        issueRow.add(issueLabel);
        
        // Create the stance buttons
        ButtonGroup stanceGroup = new ButtonGroup();
        int stanceColumnWidth = width / 2;
        int stanceOptionWidth = stanceColumnWidth / STANCE_OPTIONS.length;
        
        // Get the currently selected stance for this issue, if any
        String selectedStance = selectedStances.getOrDefault(issue, "No Data");
        
        for (int j = 0; j < STANCE_OPTIONS.length; j++) {
            String stance = STANCE_OPTIONS[j];
            Color stanceColor = getStanceColor(stance);
            
            StanceButton stanceButton = new StanceButton(stance, stanceColor);
            stanceButton.setBounds(
                width / 2 + (j * stanceOptionWidth), 
                (rowHeight - 30) / 2, 
                stanceOptionWidth - 10, 
                30
            );
            
            // Set selection based on the candidate's data
            boolean isThisStanceSelected = stance.equals(selectedStance);
            stanceButton.setSelected(isThisStanceSelected);
            
            // If this is the selected stance, add it to the map
            if (isThisStanceSelected) {
                selectedStances.put(issue, stance);
            }
            
            // Add action listener to track selections
            final String finalStance = stance;
            stanceButton.addActionListener(e -> {
                selectedStances.put(issue, finalStance);
            });
            
            stanceGroup.add(stanceButton);
            issueRow.add(stanceButton);
        }
        
        return issueRow;
    }
    
    /**
     * Creates a separator line
     */
    private JPanel createSeparator() {
        JPanel separatorPanel = new JPanel();
        separatorPanel.setOpaque(false);
        separatorPanel.setPreferredSize(new Dimension(width, 1));
        separatorPanel.setMaximumSize(new Dimension(width, 1));
        separatorPanel.setMinimumSize(new Dimension(width, 1));
        separatorPanel.setLayout(new BorderLayout());
        
        JPanel separatorLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0xE2, 0xE8, 0xF0, 128)); // Light gray with transparency
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine(0, 0, getWidth(), 0);
            }
        };
        separatorLine.setOpaque(false);
        separatorPanel.add(separatorLine, BorderLayout.CENTER);
        
        return separatorPanel;
    }
    
    /**
     * Creates a scroll pane with the content panel
     */
    private JScrollPane createScrollPane(JPanel contentPanel) {
        JScrollPane pane = new JScrollPane(contentPanel);
        pane.setOpaque(false);
        pane.getViewport().setOpaque(false);
        pane.setBorder(null);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Customize scroll bar
        JScrollBar verticalScrollBar = pane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16); // Faster scrolling
        verticalScrollBar.setUI(new BasicScrollBarUI() {
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
        });
        
        return pane;
    }
    
    /**
     * Get the appropriate color for a stance
     */
    private Color getStanceColor(String stance) {
        switch (stance) {
            case "Agree":
                return AGREE_COLOR;
            case "Disagree":
                return DISAGREE_COLOR;
            case "Neutral":
                return NEUTRAL_COLOR;
            default: // No Data
                return NO_DATA_COLOR;
        }
    }
    
    /**
     * Get all selected stances as a map
     */
    public Map<String, String> getSelectedStances() {
        return new HashMap<>(selectedStances);
    }
    
    /**
     * Set existing stance selections, for example when editing a candidate
     * @param stances Map of issues to stances
     */
    public void setSelectedStances(Map<String, String> stances) {
        if (stances == null) {
            return;
        }
        
        // Update our selected stances map
        selectedStances.clear();
        selectedStances.putAll(stances);
        
        // Need to redraw the panel to reflect changes
        contentPanel.removeAll();
        
        // Add issue rows
        for (int i = 0; i < SOCIAL_ISSUES.length; i++) {
            String issue = SOCIAL_ISSUES[i];
            
            // Add the issue row
            contentPanel.add(createIssueRow(issue));
            
            // Add separator if not the last item
            if (i < SOCIAL_ISSUES.length - 1) {
                contentPanel.add(createSeparator());
            }
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Custom JToggleButton for social stance selection
     */
    private class StanceButton extends JToggleButton {
        private final Color stanceColor;
        private final String stanceText;
        
        public StanceButton(String text, Color color) {
            super(text);
            this.stanceText = text;
            this.stanceColor = color;
            
            // Set button styling
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            
            // Set font
            if (mediumFont != null) {
                setFont(mediumFont.deriveFont(12f));
            } else {
                setFont(new Font("Sans-Serif", Font.PLAIN, 12));
            }
            
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Calculate dimensions
            int width = getWidth();
            int height = getHeight();
            int cornerRadius = 8;
            
            // Draw backgrounds
            if (isSelected()) {
                // Selected state - full color with stronger visual treatment
                g2d.setColor(stanceColor);
                g2d.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
                
                // Add a glow effect around the button to make it stand out
                Color glowColor = new Color(
                    stanceColor.getRed(), 
                    stanceColor.getGreen(), 
                    stanceColor.getBlue(), 
                    100); // Semi-transparent
                
                // Draw outer glow
                g2d.setColor(glowColor);
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRoundRect(-2, -2, width + 3, height + 3, cornerRadius + 2, cornerRadius + 2);
                
                // Draw text in white with bold font for more emphasis
                g2d.setColor(Color.WHITE);
                
                // Use a slightly bolder font for selected stance
                if (mediumFont != null) {
                    g2d.setFont(mediumFont.deriveFont(Font.BOLD, 12f));
                } else {
                    g2d.setFont(new Font("Sans-Serif", Font.BOLD, 12));
                }
            } else if (getModel().isPressed()) {
                // Pressed state - lighter color
                g2d.setColor(new Color(
                    Math.min(255, stanceColor.getRed() + 20),
                    Math.min(255, stanceColor.getGreen() + 20),
                    Math.min(255, stanceColor.getBlue() + 20)
                ));
                g2d.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
            } else if (getModel().isRollover()) {
                // Hover state - very light tint
                g2d.setColor(new Color(
                    stanceColor.getRed(),
                    stanceColor.getGreen(),
                    stanceColor.getBlue(),
                    50
                ));
                g2d.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
                
                // Draw border
                g2d.setColor(stanceColor);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
                
                // Draw text
                g2d.setColor(stanceColor);
                g2d.setFont(getFont());
            } else {
                // Normal state - just outline
                g2d.setColor(new Color(
                    stanceColor.getRed(),
                    stanceColor.getGreen(),
                    stanceColor.getBlue(),
                    40
                ));
                g2d.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
                
                // Draw text
                g2d.setColor(new Color(0x47, 0x55, 0x69)); // Slate gray
                g2d.setFont(getFont());
            }
            
            // Draw text
            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = metrics.stringWidth(stanceText);
            int textHeight = metrics.getHeight();
            
            g2d.drawString(stanceText, 
                          (width - textWidth) / 2, 
                          (height - textHeight) / 2 + metrics.getAscent());
            
            g2d.dispose();
        }
    }
} 