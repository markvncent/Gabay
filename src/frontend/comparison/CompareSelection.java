package frontend.comparison;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Selection component for comparison categories
 */
public class CompareSelection extends JPanel {
    // Colors
    private Color orangeAccent = new Color(0xF9, 0xB4, 0x47); // #F9B447
    private Color buttonTextColor = new Color(0x47, 0x55, 0x69); // #475569
    private Color whiteBackground = Color.WHITE;
    
    // Fonts
    private Font interRegular;
    private Font interMedium;
    private Font interSemiBold;
    
    // Button options
    public static final String PROFILE = "Profile Background";
    public static final String ADVOCACIES = "Focused Advocacies";
    public static final String SOCIAL = "Social Stances";
    
    // Selection buttons
    private ArrayList<SelectionButton> buttons = new ArrayList<>();
    private int selectedIndex = 0; // Default selected button
    
    // Callback
    private CategorySelectionListener selectionListener;
    
    /**
     * Interface for handling category selection changes
     */
    public interface CategorySelectionListener {
        void onCategorySelected(String category);
    }
    
    /**
     * Create a comparison selection component
     * @param interRegular Regular font
     * @param interMedium Medium font
     * @param interSemiBold SemiBold font
     * @param listener Callback for selection changes
     */
    public CompareSelection(Font interRegular, Font interMedium, Font interSemiBold, CategorySelectionListener listener) {
        this.interRegular = interRegular;
        this.interMedium = interMedium;
        this.interSemiBold = interSemiBold;
        this.selectionListener = listener;
        
        // Set up panel
        setLayout(null); // Use null layout for precise positioning
        setOpaque(false);
        
        // Create selection buttons
        createButtons();
    }
    
    /**
     * Create and position the selection buttons
     */
    private void createButtons() {
        // Calculate button widths based on text
        int buttonHeight = 45;
        int buttonWidth = 200;
        int spacing = 20;
        int startX = 0;
        
        // Create the three buttons
        SelectionButton profileButton = new SelectionButton(PROFILE, buttonWidth, buttonHeight);
        profileButton.setBounds(startX, 0, buttonWidth, buttonHeight);
        buttons.add(profileButton);
        add(profileButton);
        
        SelectionButton advocaciesButton = new SelectionButton(ADVOCACIES, buttonWidth, buttonHeight);
        advocaciesButton.setBounds(startX + buttonWidth + spacing, 0, buttonWidth, buttonHeight);
        buttons.add(advocaciesButton);
        add(advocaciesButton);
        
        SelectionButton socialButton = new SelectionButton(SOCIAL, buttonWidth, buttonHeight);
        socialButton.setBounds(startX + (buttonWidth + spacing) * 2, 0, buttonWidth, buttonHeight);
        buttons.add(socialButton);
        add(socialButton);
        
        // Set default selection
        setSelectedButton(0);
        
        // Calculate the total width needed
        int totalWidth = (buttonWidth * 3) + (spacing * 2);
        
        // Set preferred size
        setPreferredSize(new Dimension(totalWidth, buttonHeight));
    }
    
    /**
     * Set the selected button by index
     * @param index Index of the button to select
     */
    public void setSelectedButton(int index) {
        if (index >= 0 && index < buttons.size()) {
            // Deselect previous button
            if (selectedIndex >= 0 && selectedIndex < buttons.size()) {
                buttons.get(selectedIndex).setSelected(false);
            }
            
            // Select new button
            selectedIndex = index;
            buttons.get(selectedIndex).setSelected(true);
            
            // Notify listener
            if (selectionListener != null) {
                selectionListener.onCategorySelected(buttons.get(selectedIndex).getText());
            }
            
            repaint();
        }
    }
    
    /**
     * Get the currently selected category
     * @return The selected category text
     */
    public String getSelectedCategory() {
        if (selectedIndex >= 0 && selectedIndex < buttons.size()) {
            return buttons.get(selectedIndex).getText();
        }
        return PROFILE; // Default
    }
    
    /**
     * Custom button class for selection options
     */
    private class SelectionButton extends JPanel {
        private String text;
        private boolean selected = false;
        private boolean hovering = false;
        private boolean pressing = false;
        
        public SelectionButton(String text, int width, int height) {
            this.text = text;
            setPreferredSize(new Dimension(width, height));
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Add mouse listeners for interaction
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovering = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    hovering = false;
                    pressing = false;
                    repaint();
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    pressing = true;
                    repaint();
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    pressing = false;
                    repaint();
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    for (int i = 0; i < buttons.size(); i++) {
                        if (buttons.get(i) == SelectionButton.this) {
                            setSelectedButton(i);
                            break;
                        }
                    }
                }
            });
        }
        
        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }
        
        public String getText() {
            return text;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Add subtle shadow for depth (only for non-pressed state)
            if (!pressing) {
                g2d.setColor(new Color(0, 0, 0, 15)); // Very subtle shadow color
                g2d.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 10, 10);
            }
            
            // Draw button background
            if (selected) {
                g2d.setColor(orangeAccent);
            } else if (pressing) {
                g2d.setColor(new Color(0xF0, 0xF0, 0xF0)); // Slight gray when pressing
            } else if (hovering) {
                g2d.setColor(new Color(0xF8, 0xF8, 0xF8)); // Nearly white when hovering
            } else {
                g2d.setColor(whiteBackground);
            }
            
            // Draw rounded rectangle button
            g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
            
            // Draw text
            Font buttonFont = interMedium;
            if (buttonFont == null) {
                buttonFont = new Font("Sans-Serif", Font.BOLD, 14); // Use BOLD for fallback
            } else {
                // Apply letter spacing
                Map<TextAttribute, Object> attributes = new HashMap<>();
                attributes.put(TextAttribute.TRACKING, -0.02); // -2% letter spacing
                buttonFont = buttonFont.deriveFont(14f).deriveFont(attributes);
            }
            
            g2d.setFont(buttonFont);
            
            // Set text color
            if (selected) {
                g2d.setColor(Color.WHITE); // White text on orange background
            } else {
                g2d.setColor(buttonTextColor); // Dark text on white background
            }
            
            // Center the text
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textX = (getWidth() - textWidth) / 2;
            int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            
            // Draw with slight shadow effect if pressing
            if (pressing && !selected) {
                textY += 1; // Move text down 1px for press effect
            }
            
            g2d.drawString(text, textX, textY);
        }
    }
    
    // Add a method to set the font to be used
    public void setButtonFont(Font font) {
        if (font != null) {
            this.interMedium = font;
            repaint();
        }
    }
} 