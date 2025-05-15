package frontend.comparison;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Custom scrollbar UI with minimal design
 */
public class MinimalScrollBarUI extends BasicScrollBarUI {
    private final Color customThumbColor = new Color(0xC0, 0xC0, 0xC0, 200); // Semi-transparent light gray
    private final Color thumbHoverColor = new Color(0xA0, 0xA0, 0xA0, 220); // Darker when hovered
    private final int THUMB_WIDTH = 8; // Thinner scrollbar width (was 6)

    @Override
    protected void configureScrollBarColors() {
        this.trackColor = new Color(0, 0, 0, 0); // Transparent track
        // Not setting thumbColor as we handle it in paintThumb
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createEmptyButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createEmptyButton();
    }

    private JButton createEmptyButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        // Don't paint the track
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        if(r.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Determine if thumb is being hovered
        boolean isHovered = isThumbRollover();
        
        // Use different color based on state
        if (isHovered) {
            g2.setColor(thumbHoverColor);
        } else {
            g2.setColor(customThumbColor);
        }

        // Adjust size of the thumb
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            // Center the thumb horizontally in the track
            int x = r.x + (r.width - THUMB_WIDTH) / 2;
            int y = r.y;
            int width = THUMB_WIDTH;
            int height = r.height;
            
            // Ensure minimum thumb height
            height = Math.max(height, THUMB_WIDTH * 2);
            
            // Draw with rounded corners
            g2.fillRoundRect(x, y, width, height, width, width);
        } else {
            // Center the thumb vertically in the track
            int x = r.x;
            int y = r.y + (r.height - THUMB_WIDTH) / 2;
            int width = r.width;
            int height = THUMB_WIDTH;
            
            // Ensure minimum thumb width
            width = Math.max(width, THUMB_WIDTH * 2);
            
            // Draw with rounded corners
            g2.fillRoundRect(x, y, width, height, height, height);
        }
        
        g2.dispose();
    }
    
    // Override to ensure the scrollbar is always visible when needed
    @Override
    protected void setThumbBounds(int x, int y, int width, int height) {
        super.setThumbBounds(x, y, width, height);
        scrollbar.repaint();
    }
} 