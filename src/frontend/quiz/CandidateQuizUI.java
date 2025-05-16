package frontend.quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import frontend.landingpage.LandingPageUI;
import frontend.comparison.MinimalScrollBarUI;
import frontend.utils.WindowTransitionManager;

/**
 * Candidate Quiz UI for the Gabay application
 * Simplified template with just backdrop and header logo
 */
public class CandidateQuizUI extends JFrame {
    // Font variables
    private Font interRegular;
    private Font interBlack;
    private Font interSemiBold;
    private Font interBold;
    private Font interMedium;
    
    // Colors matching the main UI
    private Color primaryBlue = new Color(0x2F, 0x39, 0x8E); // #2f398e
    private Color headingColor = new Color(0x47, 0x55, 0x69); // #475569
    private Color paragraphColor = new Color(0x8D, 0x8D, 0x8D); // #8D8D8D
    
    // Background image
    private BufferedImage backgroundImage;
    private boolean showBackgroundImage = true;
    private final int BACKDROP_WIDTH = 2555;
    private final int BACKDROP_HEIGHT = 2154;
    private final int BACKDROP_X = -1250;
    private final int BACKDROP_Y = -1250;
    
    // Header logo
    private BufferedImage headerLogoImage;
    
    // Text positioning constants
    private final int TITLE_X = 140; // Base X position in the reference window size
    private final int TITLE_Y = 181; // Adjusted Y position to be closer to rectangles
    private final int PARAGRAPH_WIDTH = 1154; // Base width in reference window size
    private final int ELEMENT_SPACING = -5; // Negative spacing to create overlap
    private final Color TITLE_COLOR = new Color(0xE9, 0x45, 0x40); // #E94540 - Changed from blue to red
    
    // Window dimensions
    private int initialWindowWidth = 1411; // Fixed window width
    private int initialWindowHeight = 1050; // Increased height to fit quiz area
    
    // Quiz area component
    private QuizArea quizArea;
    
    public CandidateQuizUI() {
        // Load fonts
        loadFonts();
        
        // Load background image
        loadBackgroundImage();
        
        // Load header logo
        loadHeaderLogoImage();
        
        // Set up the window
        setTitle("Gabáy - Candidate Quiz");
        setSize(initialWindowWidth, initialWindowHeight);
        setResizable(false); // Make window non-resizable
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set default font for all UI elements
        setUIFont(interRegular);
        
        // Create main panel with custom background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Fill the background with white
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw background image with reduced opacity if enabled
                if (backgroundImage != null && showBackgroundImage) {
                    Graphics2D g2d = (Graphics2D)g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    
                    // Calculate backdrop position, adjusting for window scaling
                    double widthScaleFactor = Math.min(1.0, getWidth() / (double)initialWindowWidth);
                    double heightScaleFactor = Math.min(1.0, getHeight() / (double)initialWindowHeight);
                    double scaleFactor = Math.min(widthScaleFactor, heightScaleFactor);
                    
                    // Calculate position using the specific coordinates and scaling
                    int imageX = (int)(BACKDROP_X * widthScaleFactor);
                    int imageY = (int)(BACKDROP_Y * heightScaleFactor);
                    
                    // Calculate scaled dimensions
                    int scaledWidth = (int)(BACKDROP_WIDTH * scaleFactor);
                    int scaledHeight = (int)(BACKDROP_HEIGHT * scaleFactor);
                    
                    // Set the opacity to 3%
                    AlphaComposite alphaComposite = AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 0.03f);
                    g2d.setComposite(alphaComposite);
                    
                    g2d.drawImage(backgroundImage, imageX, imageY, scaledWidth, scaledHeight, this);
                    
                    // Reset composite for other components
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
                
                // Draw title text at specified position
                Graphics2D g2d = (Graphics2D)g;
                drawTitleAndParagraph(g2d);
            }
            
            /**
             * Draw the title and paragraph at specified position
             */
            private void drawTitleAndParagraph(Graphics2D g2d) {
                // Calculate scaling factors based on window size
                double widthScaleFactor = Math.min(1.0, getWidth() / (double)initialWindowWidth);
                double heightScaleFactor = Math.min(1.0, getHeight() / (double)initialWindowHeight);
                
                // Calculate content width
                int availableWidth = getWidth();
                int scaledParagraphWidth = (int)(PARAGRAPH_WIDTH * widthScaleFactor);
                
                // Ensure paragraph width is not too wide for the window
                scaledParagraphWidth = Math.min(scaledParagraphWidth, availableWidth - 2 * (int)(140 * widthScaleFactor));
                
                // Calculate X position for centered content
                int scaledTitleX = (availableWidth - scaledParagraphWidth) / 2;
                
                int scaledTitleY = (int)(TITLE_Y * heightScaleFactor);
                int scaledSpacing = (int)(ELEMENT_SPACING * heightScaleFactor);
                
                // Draw title text
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Apply Inter Black font for title
                Font titleFont = interBlack;
                if (titleFont != null) {
                    // Use 70pt size for title
                    float titleFontSize = 70f * (float)widthScaleFactor;
                    titleFontSize = Math.max(40f, titleFontSize); // Minimum size
                    
                    Map<TextAttribute, Object> attributes = new HashMap<>();
                    attributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
                    titleFont = titleFont.deriveFont(titleFontSize).deriveFont(attributes);
                } else {
                    titleFont = new Font("Sans-Serif", Font.BOLD, (int)(70 * widthScaleFactor));
                }
                
                g2d.setFont(titleFont);
                g2d.setColor(TITLE_COLOR);
                
                // Draw the title at calculated position
                g2d.drawString("Gabáy Quiz Match", scaledTitleX, scaledTitleY);
                
                // Draw paragraph text below the title
                Font paragraphFont = interSemiBold;
                if (paragraphFont != null) {
                    // Use exact 18pt size for paragraph as requested, with scaling
                    float paragraphFontSize = 18f * (float)widthScaleFactor;
                    paragraphFontSize = Math.max(14f, paragraphFontSize); // Minimum size
                    
                    Map<TextAttribute, Object> attributes = new HashMap<>();
                    attributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
                    paragraphFont = paragraphFont.deriveFont(paragraphFontSize).deriveFont(attributes);
                } else {
                    paragraphFont = new Font("Sans-Serif", Font.PLAIN, (int)(18 * widthScaleFactor));
                }
                
                g2d.setFont(paragraphFont);
                g2d.setColor(paragraphColor);
                
                // Example paragraph text - replace with your desired content
                String paragraphText = "Take this interactive quiz to find candidates who match your values and priorities. " +
                    "Answer questions about key issues to see which candidates align most closely with your " +
                    "political views and personal values.";
                
                // Calculate paragraph position below title - make them super close
                FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);
                // Use negative spacing to push paragraph up into title space
                int paragraphY = scaledTitleY + (titleMetrics.getHeight() / 2) + scaledSpacing;
                
                // Draw wrapped paragraph text
                drawWrappedText(g2d, paragraphText, scaledTitleX, paragraphY, scaledParagraphWidth);
                
                // Draw a divider line below the header text
                int dividerY = paragraphY + 60; // Position below the paragraph text
                int dividerWidth = Math.min(scaledParagraphWidth, getWidth() - 280);
                int dividerX = (getWidth() - dividerWidth) / 2;
                
                // Draw the line with gray color to match other pages
                g2d.setColor(new Color(0xD9, 0xD9, 0xD9)); // Light gray color #D9D9D9
                g2d.setStroke(new BasicStroke(2f)); // 2px thickness
                g2d.drawLine(dividerX, dividerY, dividerX + dividerWidth, dividerY);
            }
            
            /**
             * Draw wrapped text with specified width, left-aligned
             */
            private void drawWrappedText(Graphics2D g2d, String text, int x, int y, int maxWidth) {
                if (text == null || text.isEmpty()) {
                    return;
                }
                
                FontMetrics fm = g2d.getFontMetrics();
                int lineHeight = fm.getHeight();
                
                String[] words = text.split("\\s+");
                StringBuilder currentLine = new StringBuilder();
                int currentY = y;
                
                for (String word : words) {
                    if (currentLine.length() > 0) {
                        String testLine = currentLine + " " + word;
                        if (fm.stringWidth(testLine) <= maxWidth) {
                            currentLine.append(" ").append(word);
                        } else {
                            // Draw current line
                            g2d.drawString(currentLine.toString(), x, currentY);
                            currentY += lineHeight;
                            currentLine = new StringBuilder(word);
                        }
                    } else {
                        currentLine.append(word);
                    }
                }
                
                // Draw the last line
                if (currentLine.length() > 0) {
                    g2d.drawString(currentLine.toString(), x, currentY);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Create logo panel
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
                    // Redirect to LandingPageUI with fade transition
                    System.out.println("Redirecting to Landing Page...");
                    WindowTransitionManager.fadeTransition(CandidateQuizUI.this, () -> new LandingPageUI());
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    // Optional: Add hover effect
                    logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    logoLabel.setBorder(null);
                }
            });
        } else {
            // Fallback if image couldn't be loaded
            logoLabel.setPreferredSize(new Dimension(120, 40));
            logoLabel.setBackground(new Color(0, 0, 0, 0)); // Transparent
            
            // Still make it clickable
            logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            logoLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Redirect to LandingPageUI with fade transition
                    System.out.println("Redirecting to Landing Page...");
                    WindowTransitionManager.fadeTransition(CandidateQuizUI.this, () -> new LandingPageUI());
                }
            });
        }
        
        logoPanel.add(logoLabel);
        
        // Add components to panels
        headerPanel.add(logoPanel, BorderLayout.WEST);
        
        // Create a content panel with BorderLayout for proper centering
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Create a wrapper panel for the QuizArea with FlowLayout for centering
        JPanel quizWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        quizWrapper.setOpaque(false);
        
        // Create the QuizArea component
        quizArea = new QuizArea();
        quizWrapper.add(quizArea);
        
        // Add the wrapper to the content panel with NORTH alignment to position it below the header
        contentPanel.add(Box.createVerticalStrut(180), BorderLayout.NORTH); // Space below header
        contentPanel.add(quizWrapper, BorderLayout.CENTER);
        
        // Set content pane
        setContentPane(mainPanel);
        
        // Add component listener to handle resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Handle resizing if needed
                revalidate();
                repaint();
            }
        });
    }
    
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
    
    private void loadBackgroundImage() {
        try {
            File imageFile = new File("resources/images/Landing-Backdrop.png");
            if (imageFile.exists()) {
                backgroundImage = ImageIO.read(imageFile);
            } else {
                createFallbackImage();
            }
        } catch (IOException e) {
            e.printStackTrace();
            createFallbackImage();
        }
    }
    
    private void createFallbackImage() {
        // Create a simple colored rectangle as a fallback
        backgroundImage = new BufferedImage(BACKDROP_WIDTH, BACKDROP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = backgroundImage.createGraphics();
        g.setColor(new Color(230, 230, 250)); // Light lavender color
        g.fillRect(0, 0, BACKDROP_WIDTH, BACKDROP_HEIGHT);
        g.setColor(new Color(0x2F, 0x39, 0x8E, 100)); // Translucent blue
        int border = 20;
        g.fillRect(border, border, BACKDROP_WIDTH-2*border, BACKDROP_HEIGHT-2*border);
        g.dispose();
    }
    
    private void loadHeaderLogoImage() {
        try {
            // Try to load the header logo image from the specific path
            File logoFile = new File("resources/images/Candidate Search/HeaderLogo.png");
            if (logoFile.exists()) {
                headerLogoImage = ImageIO.read(logoFile);
                System.out.println("Header logo loaded successfully from: " + logoFile.getAbsolutePath());
            } else {
                System.out.println("Header logo file not found at: " + logoFile.getAbsolutePath());
                
                // Try alternative locations as backup
                String[] alternativePaths = {
                    "resources/images/HeaderLogo.png",
                    "HeaderLogo.png",
                    "images/HeaderLogo.png",
                    "images/Candidate Search/HeaderLogo.png",
                    "../resources/images/Candidate Search/HeaderLogo.png"
                };
                
                for (String path : alternativePaths) {
                    File altFile = new File(path);
                    if (altFile.exists()) {
                        headerLogoImage = ImageIO.read(altFile);
                        System.out.println("Header logo loaded from alternative path: " + altFile.getAbsolutePath());
                        break;
                    }
                }
            }
            
            // If still couldn't find the logo, create a blank image
            if (headerLogoImage == null) {
                headerLogoImage = new BufferedImage(150, 40, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = headerLogoImage.createGraphics();
                g.setColor(new Color(0, 0, 0, 0)); // Transparent
                g.fillRect(0, 0, 150, 40);
                g.dispose();
            }
        } catch (IOException e) {
            e.printStackTrace();
            
            // Create blank image even after exception
            headerLogoImage = new BufferedImage(150, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = headerLogoImage.createGraphics();
            g.setColor(new Color(0, 0, 0, 0)); // Transparent
            g.fillRect(0, 0, 150, 40);
            g.dispose();
        }
    }
    
    private void setUIFont(Font font) {
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("ComboBox.font", font);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CandidateQuizUI quizUI = new CandidateQuizUI();
                quizUI.setVisible(true);
            }
        });
    }
} 