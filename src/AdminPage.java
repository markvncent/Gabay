import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * AdminPage - A simple admin interface for the GabáyApp
 */
public class AdminPage extends JFrame {
    // Font variables
    private Font interRegular;
    private Font interBlack;
    private Font interSemiBold;
    private Font interBold;
    private Font interMedium;
    
    // Header logo
    private BufferedImage headerLogoImage;
    
    // Colors
    private Color primaryBlue = new Color(0x2F, 0x39, 0x8E); // #2f398e
    private Color dividerColor = new Color(0xD9, 0xD9, 0xD9); // #D9D9D9
    private Color headingColor = new Color(0x47, 0x55, 0x69); // #475569
    
    public AdminPage() {
        // Load fonts
        loadFonts();
        
        // Load header logo
        loadHeaderLogoImage();
        
        // Set up the window
        setTitle("Gabáy - Admin Panel");
        setSize(1440, 1024);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set default font for all UI elements
        setUIFont(interRegular);
        
        // Create main panel with white background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Create logo panel with back button
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        
        // Create logo label with the header logo image
        JLabel logoLabel = new JLabel();
        if (headerLogoImage != null) {
            // Scale logo appropriately
            int logoHeight = 40; // Height in pixels
            int logoWidth = (int)((double)headerLogoImage.getWidth() / headerLogoImage.getHeight() * logoHeight);
            
            // Create scaled version of logo
            Image scaledLogo = headerLogoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledLogo));
            
            // Make logo clickable to go back to landing page
            logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            logoLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Go back to landing page
                    Dimension currentSize = getSize();
                    dispose();
                    LandingPage landingPage = new LandingPage();
                    landingPage.setSize(currentSize); // Set the same size as current window
                    landingPage.setLocationRelativeTo(null); // Center on screen
                    landingPage.setVisible(true);
                }
            });
        } else {
            // If image couldn't be loaded, use text
            logoLabel.setText("Gabáy");
            logoLabel.setFont(interBold.deriveFont(24f));
            logoLabel.setForeground(primaryBlue);
            
            // Still make it clickable
            logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            logoLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Go back to landing page
                    Dimension currentSize = getSize();
                    dispose();
                    LandingPage landingPage = new LandingPage();
                    landingPage.setSize(currentSize); // Set the same size as current window
                    landingPage.setLocationRelativeTo(null); // Center on screen
                    landingPage.setVisible(true);
                }
            });
        }
        
        logoPanel.add(logoLabel);
        
        // Create title label
        JLabel titleLabel = new JLabel("Admin Panel");
        if (interBlack != null) {
            titleLabel.setFont(interBlack.deriveFont(32f));
        } else {
            titleLabel.setFont(new Font("Sans-Serif", Font.BOLD, 32));
        }
        titleLabel.setForeground(headingColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add components to header panel
        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Create content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Add a welcome message
        JLabel welcomeLabel = new JLabel("Welcome to the Admin Panel", SwingConstants.CENTER);
        if (interSemiBold != null) {
            welcomeLabel.setFont(interSemiBold.deriveFont(24f));
        } else {
            welcomeLabel.setFont(new Font("Sans-Serif", Font.PLAIN, 24));
        }
        welcomeLabel.setForeground(headingColor);
        
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);
        
        // Add a divider between header and content
        JSeparator divider = new JSeparator();
        divider.setForeground(dividerColor);
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(divider, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Set content pane
        setContentPane(mainPanel);
    }
    
    /**
     * Load the header logo image from resources
     */
    private void loadHeaderLogoImage() {
        try {
            // Try to load the header logo image from various locations
            String[] paths = {
                "resources/images/Candidate Search/HeaderLogo.png",
                "resources/images/HeaderLogo.png",
                "HeaderLogo.png",
                "images/HeaderLogo.png",
            };
            
            for (String path : paths) {
                File logoFile = new File(path);
                if (logoFile.exists()) {
                    headerLogoImage = ImageIO.read(logoFile);
                    System.out.println("Header logo loaded from: " + path);
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading header logo: " + e.getMessage());
        }
    }
    
    /**
     * Load Inter fonts
     */
    private void loadFonts() {
        try {
            // Load Inter fonts
            File interBlackFile = new File("lib/fonts/Inter_18pt-Black.ttf");
            File interSemiBoldFile = new File("lib/fonts/Inter_18pt-SemiBold.ttf");
            File interBoldFile = new File("lib/fonts/Inter_18pt-Bold.ttf");
            File interMediumFile = new File("lib/fonts/Inter_18pt-Medium.ttf");
            File interRegularFile = new File("lib/fonts/Inter_18pt-Regular.ttf");
            
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            
            if (interBlackFile.exists()) {
                interBlack = Font.createFont(Font.TRUETYPE_FONT, interBlackFile);
                ge.registerFont(interBlack);
            } else {
                interBlack = new Font("Sans-Serif", Font.BOLD, 12);
            }
            
            if (interSemiBoldFile.exists()) {
                interSemiBold = Font.createFont(Font.TRUETYPE_FONT, interSemiBoldFile);
                ge.registerFont(interSemiBold);
            } else {
                interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
            }
            
            if (interBoldFile.exists()) {
                interBold = Font.createFont(Font.TRUETYPE_FONT, interBoldFile);
                ge.registerFont(interBold);
            } else {
                interBold = new Font("Sans-Serif", Font.BOLD, 12);
            }
            
            if (interMediumFile.exists()) {
                interMedium = Font.createFont(Font.TRUETYPE_FONT, interMediumFile);
                ge.registerFont(interMedium);
            } else {
                interMedium = new Font("Sans-Serif", Font.PLAIN, 12);
            }
            
            if (interRegularFile.exists()) {
                interRegular = Font.createFont(Font.TRUETYPE_FONT, interRegularFile);
                ge.registerFont(interRegular);
            } else {
                interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
            }
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            // Fallback to system fonts
            interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
            interBlack = new Font("Sans-Serif", Font.BOLD, 12);
            interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
            interBold = new Font("Sans-Serif", Font.BOLD, 12);
            interMedium = new Font("Sans-Serif", Font.PLAIN, 12);
        }
    }
    
    /**
     * Set default font for UI components
     */
    private void setUIFont(Font font) {
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("ComboBox.font", font);
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminPage adminPage = new AdminPage();
            adminPage.setVisible(true);
        });
    }
} 