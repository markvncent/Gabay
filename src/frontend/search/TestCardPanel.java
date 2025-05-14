package frontend.search;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Simple test application to verify the CandidateCardPanel works properly
 */
public class TestCardPanel {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create and set up the window
            JFrame frame = new JFrame("CandidateCardPanel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            
            // Create a panel with BorderLayout
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);
            
            // Create a panel for the top section
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            topPanel.setBackground(new Color(200, 220, 240));
            JLabel titleLabel = new JLabel("Candidate Card Panel Test");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            topPanel.add(titleLabel);
            
            // Create panel for the cards
            CandidateCardPanel cardPanel = new CandidateCardPanel(
                new Font("Arial", Font.PLAIN, 12),
                new Font("Arial", Font.BOLD, 14),
                new Font("Arial", Font.PLAIN, 13),
                name -> System.out.println("Selected: " + name)
            );
            
            // Add panels to main panel
            mainPanel.add(topPanel, BorderLayout.NORTH);
            mainPanel.add(cardPanel, BorderLayout.CENTER);
            
            // Add main panel to frame
            frame.setContentPane(mainPanel);
            
            // Display the window
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
} 