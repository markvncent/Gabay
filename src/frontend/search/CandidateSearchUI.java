package frontend.search;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import backend.model.CandidateDataLoader;
import java.awt.geom.Rectangle2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
// Uncomment LandingPageUI import for proper redirection
import frontend.landingpage.LandingPageUI;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CandidateSearchUI extends JFrame {
    // Font variables
    private Font interRegular;
    private Font interBlack;
    private Font interSemiBold;
    private Font interBold;
    private Font interMedium;
    
    // Colors matching the main UI
    private Color primaryBlue = new Color(0x2F, 0x39, 0x8E); // #2f398e
    private Color filterBlue = new Color(0x2B, 0x37, 0x80); // #2B3780 - matching filter dropdown
    private Color accentOrange = new Color(0xF9, 0xB3, 0x45); // #f9b345
    private Color accentRed = new Color(0xEB, 0x42, 0x3E); // #eb423e
    private Color headingColor = new Color(0x47, 0x55, 0x69); // #475569
    private Color paragraphColor = new Color(0x8D, 0x8D, 0x8D); // #8D8D8D
    private Color dividerColor = new Color(0xD9, 0xD9, 0xD9); // #D9D9D9
    private Color searchBorderColor = new Color(0xCB, 0xD5, 0xE1); // #CBD5E1
    
    // Background image
    private BufferedImage backgroundImage;
    private boolean showBackgroundImage = true;
    private final int BACKDROP_WIDTH = 2555;
    private final int BACKDROP_HEIGHT = 2154;
    private final int BACKDROP_X = 206;
    private final int BACKDROP_Y = -242;
    
    // Header logo
    private BufferedImage headerLogoImage;
    
    // Search icon
    private BufferedImage searchIconImage;
    
    // Clear icon for search field
    private BufferedImage clearIconImage;
    private JLabel clearIconLabel;
    private boolean clearIconVisible = false;
    
    // Text positioning constants
    private final int TITLE_X = 140; // Base X position in the reference window size
    private final int TITLE_Y = 181; // Adjusted Y position to be closer to rectangles (was 141)
    private final int PARAGRAPH_WIDTH = 1154; // Base width in reference window size
    private final int ELEMENT_SPACING = -5; // Negative spacing to create overlap
    private final Color TITLE_COLOR = new Color(0x2B, 0x37, 0x80); // #2B3780
    
    // Center positioning references
    private final boolean CENTER_CONTENT = true; // Enable center positioning for the content block
    private final boolean CENTER_PARAGRAPH_TEXT = false; // Keep paragraph text left-aligned
    private final int CONTENT_SIDE_MARGIN = 140; // Side margin to maintain on each side
    
    // Divider line properties - adding these for better responsiveness
    private int initialWindowWidth = 1440;
    private int initialWindowHeight = 1024;
    
    // Filter dropdown component
    private FilterDropdown filterDropdown;
    
    // Province dropdown component
    private ProvinceDropdown provinceDropdown;
    
    // UI Components
    private JPanel secondRectangle;
    private JPanel thirdRectangle;
    private JTextField searchField;
    
    // Repositioning variables
    private int cardMarginTop = 20; // Margin between divider and cards
    private int cardMarginHorizontal = 15; // Horizontal spacing between cards
    private int cardsPerRow = 4; // Number of cards per row
    
    // CandidateCardPanel for displaying cards in a scrollable container
    private CandidateCardPanel cardPanel;
    
    public CandidateSearchUI() {
        // Load fonts
        loadFonts();
        
        // Load background image
        loadBackgroundImage();
        
        // Load header logo
        loadHeaderLogoImage();
        
        // Load search icon
        loadSearchIconImage();
        
        // Set up the window
        setTitle("Gabáy - Candidate Search");
        setSize(1440, 1024);
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
                scaledParagraphWidth = Math.min(scaledParagraphWidth, availableWidth - 2 * (int)(CONTENT_SIDE_MARGIN * widthScaleFactor));
                
                // Calculate X position for centered content
                int scaledTitleX;
                // Always center the content in the available space
                scaledTitleX = (availableWidth - scaledParagraphWidth) / 2;
                
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
                g2d.drawString("Search Candidate.", scaledTitleX, scaledTitleY);
                
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
                String paragraphText = "Enter keywords to search for specific political issues or candidate positions. "
                    + "This search tool allows you to quickly find where each candidate stands on matters important to you. "
                    + "Results will show highlighted stances directly from candidates' statements and platforms.";
                
                // Calculate paragraph position below title - make them super close
                FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);
                // Use negative spacing to push paragraph up into title space
                int paragraphY = scaledTitleY + (titleMetrics.getHeight() / 2) + scaledSpacing;
                
                // Draw wrapped paragraph text
                drawWrappedText(g2d, paragraphText, scaledTitleX, paragraphY, scaledParagraphWidth);
            }
            
            /**
             * Draw wrapped text with specified width, left-aligned text in a centered content block
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
                            String lineToRender = currentLine.toString();
                            int lineWidth = fm.stringWidth(lineToRender);
                            
                            int lineX = x;
                            if (CENTER_PARAGRAPH_TEXT) {
                                // Center this line within the paragraph width
                                lineX = x + (maxWidth - lineWidth) / 2;
                            }
                            
                            g2d.drawString(lineToRender, lineX, currentY);
                            currentY += lineHeight;
                            currentLine = new StringBuilder(word);
                        }
                    } else {
                        currentLine.append(word);
                    }
                }
                
                // Draw the last line
                if (currentLine.length() > 0) {
                    String lineToRender = currentLine.toString();
                    int lineWidth = fm.stringWidth(lineToRender);
                    
                    int lineX = x;
                    if (CENTER_PARAGRAPH_TEXT) {
                        // Center this line within the paragraph width
                        lineX = x + (maxWidth - lineWidth) / 2;
                    }
                    
                    g2d.drawString(lineToRender, lineX, currentY);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(true); // Make panel opaque with white background
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Create logo panel instead of back button
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
                    // Navigate to landing page
                    navigateToLandingPage();
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    // Add hover effect - subtle glow around logo
                    logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    logoLabel.setBorder(null);
                    setCursor(Cursor.getDefaultCursor());
                }
            });
        } else {
            // If image couldn't be loaded, create a blank placeholder of appropriate size
            // No text fallback - using a blank placeholder instead
            logoLabel.setPreferredSize(new Dimension(120, 40));
            logoLabel.setBackground(new Color(0, 0, 0, 0)); // Transparent
            
            // Still make it clickable
            logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            logoLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Navigate to landing page
                    navigateToLandingPage();
                }
            });
            
            // Log an error that we couldn't find the image
            System.err.println("ERROR: Could not load HeaderLogo.png from any location. Using blank placeholder.");
        }
        
        logoPanel.add(logoLabel);
        
        // Move title setup from header to the custom painting
        // Title is now painted directly at the specified position (140,141)
        JLabel titleLabel = new JLabel("");  // Empty placeholder for layout
        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Add content panel (empty for now)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null); // Use null layout for precise positioning
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Calculate the initial position - 5px below paragraph text
        int paragraphHeight = TITLE_Y + calculateTextHeight(getGraphics());
        
        // Ensure minimum position even if calculation fails, and move up by 80px total
        int rectanglesY = Math.max(200, paragraphHeight + 5) - 80;
        
        // Calculate total width of all rectangles and spacing for center alignment
        int totalWidth = 217 + 827 + 90; // Sum of rectangle widths
        int gap1 = 10; // Gap between first and second rectangle
        int gap2 = 10; // Gap between second and third rectangle
        totalWidth += gap1 + gap2; // Add gaps to total width

        // Calculate starting X position to center all rectangles based on current window width
        int startX = (getWidth() - totalWidth) / 2;

        // Create the modular filter dropdown component
        filterDropdown = new FilterDropdown(contentPanel, interMedium, interRegular, this::handleFilterSelection) {
            @Override
            public void toggleDropdown() {
                super.toggleDropdown();
                // Ensure divider is below any open dropdowns
                adjustDividerForDropdowns();
            }
        };
        JPanel filterRectangle = filterDropdown.getFilterRectangle();
        filterRectangle.setBounds(startX, rectanglesY, 217, 45);
        
        // Create the province dropdown component - position it below the Find button
        provinceDropdown = new ProvinceDropdown(contentPanel, interMedium, interRegular, this::handleProvinceSelection) {
            @Override
            public void toggleDropdown() {
                super.toggleDropdown();
                // Ensure divider is below any open dropdowns
                adjustDividerForDropdowns();
            }
        };
        JPanel provinceRectangle = provinceDropdown.getProvinceRectangle();
        // Position aligned with right edge of Find button, 5px below it, with 200px width
        provinceRectangle.setBounds(startX + 217 + gap1 + 827 + gap2 - 200 + 90, rectanglesY + 45 + 5, 200, 45);
        
        // Second rectangle: 827x45
        secondRectangle = createSimpleRectangle(827, 45);
        secondRectangle.setBounds(startX + 217 + gap1, rectanglesY, 827, 45);
        
        // Third rectangle: 90x45 - create a custom "Find" button
        thirdRectangle = createFindButton(90, 45);
        thirdRectangle.setBounds(startX + 217 + gap1 + 827 + gap2, rectanglesY, 90, 45);
        
        // Add the rectangles directly to the content panel
        contentPanel.add(filterRectangle);
        contentPanel.add(secondRectangle);
        contentPanel.add(thirdRectangle);
        contentPanel.add(provinceRectangle);
        
        // Set the correct z-order for the province rectangle
        contentPanel.setComponentZOrder(provinceRectangle, 0);
        
        // Add a divider line 20px below the buttons
        JPanel dividerLine = createDividerLine();
        // Calculate position for divider - center it with 80% of window width
        int windowWidth = getWidth();
        int dividerWidth = (int)(windowWidth * 0.8);
        int dividerX = (windowWidth - dividerWidth) / 2;
        int dividerY = rectanglesY + 45 + 5 + 45 + 10;
        dividerLine.setBounds(dividerX, dividerY, dividerWidth, 1);
        contentPanel.add(dividerLine);

        // Set the z-order to ensure dropdowns appear above the divider line
        contentPanel.setComponentZOrder(dividerLine, contentPanel.getComponentCount() - 1);

        // Add candidate card below the divider
        addCandidateCardSection(contentPanel, dividerX, dividerLine.getBounds().y + 30, dividerWidth);
        
        // Final adjustment to ensure proper z-ordering
        adjustDividerForDropdowns();

        // Set content pane
        setContentPane(mainPanel);
        
        // Add component listener to handle resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // First recenter the rectangles based on the new window width
                recenterRectangles();
                
                // Then do the general layout adjustments
                adjustLayoutForWindowSize();
                revalidate();
                repaint();
            }
        });

        // Add key listener to search field for live filtering
        searchField.addKeyListener(new KeyAdapter() {
            // Delay timer for smoother search experience
            private Timer searchTimer = new Timer(300, e -> performSearch());
            
            @Override
            public void keyReleased(KeyEvent e) {
                // Reset and restart timer on each keystroke
                if (searchTimer.isRunning()) {
                    searchTimer.restart();
                } else {
                    searchTimer.start();
                }
            }
            
            private void performSearch() {
                String query = searchField.getText();
                if (query.equals("Search for candidates or issues...")) {
                    query = "";
                }
                
                // Apply search to card panel
                if (cardPanel != null) {
                    cardPanel.filterCards(query);
                }
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
    
    private void setUIFont(Font font) {
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("ComboBox.font", font);
    }
    
    /**
     * Creates a simple plain gray rectangle panel
     */
    private JPanel createSimpleRectangle(int width, int height) {
        // For search rectangle, use a different approach
        if (width > 500) {
            // Create a panel with custom layout
            JPanel searchPanel = new JPanel(new BorderLayout());
            searchPanel.setPreferredSize(new Dimension(width, height));
            
            // Create a custom search bar with border and icon
            JPanel searchBarPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Fill with white background using rounded corners
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    
                    // Draw border with color #CBD5E1, 1px width with rounded corners
                    g2d.setColor(searchBorderColor);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                    
                    // Draw search icon if available
                    if (searchIconImage != null) {
                        // Set icon to exactly 20px height
                        int iconHeight = 20; // Fixed 20px height
                        int iconWidth = (int)((double)searchIconImage.getWidth() / searchIconImage.getHeight() * iconHeight);
                        
                        // Draw icon on left side with padding
                        g2d.drawImage(searchIconImage, 15, (getHeight() - iconHeight) / 2, 
                                    iconWidth, iconHeight, this);
                    }
                }
            };
            searchBarPanel.setLayout(new BorderLayout());
            
            // Create text field
            searchField = new JTextField("Search for candidates or issues...");
            searchField.setForeground(Color.GRAY);
            searchField.setFont(interRegular.deriveFont(14f));
            searchField.setOpaque(false);
            searchField.setBorder(null);
            
            // Calculate icon width for proper positioning
            int iconWidth = 0;
            if (searchIconImage != null) {
                int iconHeight = 20;
                iconWidth = (int)((double)searchIconImage.getWidth() / searchIconImage.getHeight() * iconHeight);
            }
            
            // Create clear icon label
            clearIconLabel = new JLabel();
            if (clearIconImage != null) {
                int clearIconHeight = 16; // Slightly smaller than search icon
                int clearIconWidth = (int)((double)clearIconImage.getWidth() / clearIconImage.getHeight() * clearIconHeight);
                
                // Create scaled icon
                Image scaledImage = clearIconImage.getScaledInstance(clearIconWidth, clearIconHeight, Image.SCALE_SMOOTH);
                clearIconLabel.setIcon(new ImageIcon(scaledImage));
                clearIconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                clearIconLabel.setVisible(false); // Initially hidden
                
                // Add click handler to clear the search text
                clearIconLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        searchField.setText("");
                        searchField.requestFocus();
                        clearIconLabel.setVisible(false);
                        clearIconVisible = false;
                    }
                });
            }
            
            // Add padding panel
            JPanel paddingPanel = new JPanel(new BorderLayout());
            paddingPanel.setOpaque(false);
            paddingPanel.setBorder(BorderFactory.createEmptyBorder(0, 15 + iconWidth + 20, 0, 10));
            paddingPanel.add(searchField, BorderLayout.CENTER);
            
            // Add clear icon to the right side with some padding
            if (clearIconLabel != null) {
                JPanel clearIconPanel = new JPanel(new BorderLayout());
                clearIconPanel.setOpaque(false);
                clearIconPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15)); // Right padding
                clearIconPanel.add(clearIconLabel, BorderLayout.CENTER);
                paddingPanel.add(clearIconPanel, BorderLayout.EAST);
            }
            
            // Add components
            searchBarPanel.add(paddingPanel, BorderLayout.CENTER);
            searchPanel.add(searchBarPanel, BorderLayout.CENTER);
            
            // Add focus listener for placeholder behavior
            searchField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (searchField.getText().equals("Search for candidates or issues...")) {
                        searchField.setText("");
                        searchField.setForeground(Color.BLACK);
                        updateClearIconVisibility(false);
                    } else if (!searchField.getText().isEmpty()) {
                        updateClearIconVisibility(true);
                    }
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    if (searchField.getText().isEmpty()) {
                        searchField.setText("Search for candidates or issues...");
                        searchField.setForeground(Color.GRAY);
                        updateClearIconVisibility(false);
                    }
                }
            });
            
            // Add document listener to handle text changes
            searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    updateClearIconVisibility(!searchField.getText().equals("Search for candidates or issues..."));
                }
                
                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    updateClearIconVisibility(!searchField.getText().isEmpty());
                }
                
                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    updateClearIconVisibility(!searchField.getText().isEmpty() && 
                                             !searchField.getText().equals("Search for candidates or issues..."));
                }
            });
            
            return searchPanel;
        } else {
            // For non-search rectangles, use the original implementation
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    // Fill with plain gray color
                    g.setColor(new Color(200, 200, 200)); // Plain gray
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            
            panel.setPreferredSize(new Dimension(width, height));
            panel.setOpaque(false);
            
            return panel;
        }
    }
    
    /**
     * Handle filter selection changes
     */
    private void handleFilterSelection(String selectedFilter) {
        // Handle the filter selection here
        System.out.println("Selected filter: " + (selectedFilter != null ? selectedFilter : "None"));
        
        // Apply filters to card panel
        if (cardPanel != null) {
            // Filter cards by category (if relevant functionality is implemented)
            // For now, just refresh the data
            cardPanel.filterCards(searchField.getText());
        }
    }
    
    /**
     * Handle province selection changes
     */
    private void handleProvinceSelection(String selectedProvince) {
        // Handle the province selection here
        System.out.println("Selected province: " + (selectedProvince != null ? selectedProvince : "None"));
        
        // Apply province filter to card panel
        if (cardPanel != null && selectedProvince != null) {
            cardPanel.filterByProvince(selectedProvince);
        }
    }
    
    /**
     * Adjust layout components based on window size
     */
    private void adjustLayoutForWindowSize() {
        // Get current window dimensions
        int windowWidth = getWidth();
        int windowHeight = getHeight();
        
        // Calculate scale factors
        double widthScaleFactor = Math.min(1.0, windowWidth / (double)initialWindowWidth);
        double heightScaleFactor = Math.min(1.0, windowHeight / (double)initialWindowHeight);
        
        // Adjust header panel padding
        int topBottomPadding = (int)(20 * heightScaleFactor);
        int leftRightPadding = (int)(30 * widthScaleFactor);
        
        // Get the header panel and update its border
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel)comp;
                
                // Check if the panel's layout is actually a BorderLayout
                if (panel.getLayout() instanceof BorderLayout) {
                    Component[] panelComps = panel.getComponents();
                    for (Component panelComp : panelComps) {
                        if (panelComp instanceof JPanel && 
                            ((BorderLayout)panel.getLayout()).getConstraints(panelComp) == BorderLayout.NORTH) {
                            // Found the header panel
                            JPanel headerPanel = (JPanel)panelComp;
                            headerPanel.setBorder(BorderFactory.createEmptyBorder(
                                topBottomPadding, leftRightPadding, topBottomPadding, leftRightPadding));
                            break;
                        }
                    }
                    
                    // Check if this panel has the rectangles
                    if (((BorderLayout)panel.getLayout()).getConstraints(comp) == BorderLayout.CENTER) {
                        JPanel contentPanel = (JPanel)comp;
                        
                        // Calculate paragraph bottom position - scale the title Y position
                        int scaledTitleY = (int)(TITLE_Y * heightScaleFactor);
                        int paragraphY = scaledTitleY + calculateTextHeight(this.getGraphics());
                        
                        // Add 5 pixels below the paragraph, then move up by 80px total
                        // Scale these adjustments too for better responsiveness
                        int scaledAdjustment = (int)(80 * heightScaleFactor);
                        int rectanglesY = Math.max((int)(200 * heightScaleFactor), paragraphY + (int)(5 * heightScaleFactor)) - scaledAdjustment;
                        
                        // Get the filter rectangle panel
                        JPanel filterRectangle = filterDropdown.getFilterRectangle();
                        
                        // Update to scale the height proportionally
                        int height = (int)(45 * heightScaleFactor);
                        
                        // Calculate total width of all rectangles and spacing for center alignment
                        int firstWidth = (int)(217 * widthScaleFactor);
                        int secondWidth = (int)(827 * widthScaleFactor);
                        int thirdWidth = (int)(90 * widthScaleFactor);
                        int gap1 = (int)(10 * widthScaleFactor); // Gap between first and second rectangle
                        int gap2 = (int)(10 * widthScaleFactor); // Gap between second and third rectangle
                        int totalWidth = firstWidth + secondWidth + thirdWidth + gap1 + gap2;
                        
                        // Calculate starting X position to center all rectangles
                        int startX = (windowWidth - totalWidth) / 2;
                        
                        // First rectangle (217x45)
                        filterRectangle.setBounds(startX, rectanglesY, firstWidth, height);
                        
                        // Second rectangle (827x45)
                        secondRectangle.setBounds(startX + firstWidth + gap1, rectanglesY, secondWidth, height);
                        
                        // Third rectangle (90x45)
                        thirdRectangle.setBounds(startX + firstWidth + gap1 + secondWidth + gap2, rectanglesY, thirdWidth, height);
                        
                        // Update province dropdown position
                        if (provinceDropdown != null) {
                            JPanel provinceRectangle = provinceDropdown.getProvinceRectangle();
                            // Position with right edge aligned to Find button, 5px below, 200px width
                            int findButtonRightEdge = startX + firstWidth + gap1 + secondWidth + gap2 + thirdWidth;
                            int provinceWidth = (int)(200 * widthScaleFactor);
                            int provinceX = findButtonRightEdge - provinceWidth;
                            provinceRectangle.setBounds(provinceX, rectanglesY + height + 5, provinceWidth, height);
                            provinceDropdown.updatePositionOnResize();
                        }
                        
                        // Find and update divider line position
                        for (Component dividerComp : contentPanel.getComponents()) {
                            // Check if this component is not one of our main UI elements
                            if (dividerComp != filterRectangle && 
                                dividerComp != secondRectangle && 
                                dividerComp != thirdRectangle && 
                                dividerComp != provinceDropdown.getProvinceRectangle()) {
                                
                                // This should be the divider line
                                // Calculate proper position below all elements
                                JPanel provinceRect = provinceDropdown.getProvinceRectangle();
                                Rectangle provinceBounds = provinceRect.getBounds();
                                int dividerY = Math.max(rectanglesY + height + 10, 
                                                      provinceBounds.y + provinceBounds.height + 10);
                                
                                // Position divider with the same width as the button group
                                dividerComp.setBounds(startX, dividerY, totalWidth, 1);
                                break;
                            }
                        }
                        
                        // Update filter dropdown position
                        filterDropdown.updatePositionOnResize();

                        // Update candidate card position if it exists
                        if (cardPanel == null) {
                            // No candidate cards to update
                        } else {
                            // Update candidate cards positioning
                            updateCandidateCardsLayout(startX, totalWidth);
                        }
                    }
                } else {
                    // This panel doesn't have a BorderLayout, so we can't safely access constraints
                    // This is likely the content panel with null layout
                    // Just continue to the next component
                    continue;
                }
            }
        }
    }
    
    /**
     * Calculate the height of the paragraph text to determine where to place the rectangles
     */
    private int calculateTextHeight(Graphics graphics) {
        if (graphics == null) {
            return 100; // Default height if graphics context isn't available
        }
        
        Graphics2D g2d = (Graphics2D) graphics.create();
        
        // Set up title font
        Font titleFont = interBlack;
        if (titleFont != null) {
            float titleFontSize = 70f;
            titleFont = titleFont.deriveFont(titleFontSize);
        } else {
            titleFont = new Font("Sans-Serif", Font.BOLD, 70);
        }
        
        // Set up paragraph font
        Font paragraphFont = interSemiBold;
        if (paragraphFont != null) {
            float paragraphFontSize = 18f;
            paragraphFont = paragraphFont.deriveFont(paragraphFontSize);
        } else {
            paragraphFont = new Font("Sans-Serif", Font.PLAIN, 18);
        }
        
        // Get title metrics
        g2d.setFont(titleFont);
        FontMetrics titleMetrics = g2d.getFontMetrics();
        int titleHeight = titleMetrics.getHeight();
        
        // Get paragraph metrics
        g2d.setFont(paragraphFont);
        FontMetrics paragraphMetrics = g2d.getFontMetrics();
        
        // Example paragraph text - this should match the one in paintComponent
        String paragraphText = "Enter keywords to search for specific political issues or candidate positions. "
            + "This search tool allows you to quickly find where each candidate stands on matters important to you. "
            + "Results will show highlighted stances directly from candidates' statements and platforms.";
        
        // Calculate wrapped text lines
        int maxWidth = PARAGRAPH_WIDTH;
        String[] words = paragraphText.split("\\s+");
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (currentLine.length() > 0) {
                String testLine = currentLine + " " + word;
                if (paragraphMetrics.stringWidth(testLine) <= maxWidth) {
                    currentLine.append(" ").append(word);
                } else {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                }
            } else {
                currentLine.append(word);
            }
        }
        
        // Add the last line
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        // Calculate total height with tight spacing
        int totalHeight = titleHeight + (lines.size() * paragraphMetrics.getHeight()) + ELEMENT_SPACING;
        
        g2d.dispose();
        return totalHeight;
    }
    
    /**
     * Load the header logo image from resources
     */
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
            
            // If we still couldn't find the logo, use a different approach with resource stream
            if (headerLogoImage == null) {
                // Do not use text, continue trying to find the image
                System.out.println("Still looking for the logo in additional locations...");
                
                // Try using class loader resource stream as a last resort
                try {
                    ClassLoader classLoader = getClass().getClassLoader();
                    java.net.URL resourceURL = classLoader.getResource("resources/images/Candidate Search/HeaderLogo.png");
                    if (resourceURL != null) {
                        headerLogoImage = ImageIO.read(resourceURL);
                        System.out.println("Header logo loaded using class loader.");
                    }
                } catch (Exception e) {
                    System.out.println("Error loading with class loader: " + e.getMessage());
                }
            }
            
            // Last resort - create a blank image for the logo
            // This ensures we always have an image instead of falling back to text
            if (headerLogoImage == null) {
                System.err.println("WARNING: Could not load HeaderLogo.png from any location. Creating blank image.");
                // Create a blank image with appropriate dimensions for a logo
                headerLogoImage = new BufferedImage(150, 40, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = headerLogoImage.createGraphics();
                g.setColor(new Color(0, 0, 0, 0)); // Transparent
                g.fillRect(0, 0, 150, 40);
                g.dispose();
            }
        } catch (IOException e) {
            System.out.println("Error loading header logo: " + e.getMessage());
            e.printStackTrace();
            
            // Create blank image even after exception
            headerLogoImage = new BufferedImage(150, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = headerLogoImage.createGraphics();
            g.setColor(new Color(0, 0, 0, 0)); // Transparent
            g.fillRect(0, 0, 150, 40);
            g.dispose();
        }
    }
    
    /**
     * Load the search icon image from resources
     */
    private void loadSearchIconImage() {
        try {
            // Try to load the search icon image from the specific path
            File searchFile = new File("resources/images/Candidate Search/search.png");
            if (searchFile.exists()) {
                searchIconImage = ImageIO.read(searchFile);
                System.out.println("Search icon loaded successfully from: " + searchFile.getAbsolutePath());
            } else {
                System.out.println("Search icon not found at: " + searchFile.getAbsolutePath());
                
                // Try alternative locations as backup
                String[] alternativePaths = {
                    "resources/images/Buttons Icon/search_masked.png",
                    "resources/images/search.png",
                    "search.png",
                    "images/search.png"
                };
                
                for (String path : alternativePaths) {
                    File altFile = new File(path);
                    if (altFile.exists()) {
                        searchIconImage = ImageIO.read(altFile);
                        System.out.println("Search icon loaded from alternative path: " + altFile.getAbsolutePath());
                        break;
                    }
                }
            }
            
            // Load clear icon (x.png)
            File clearFile = new File("resources/images/Candidate Search/x.png");
            if (clearFile.exists()) {
                clearIconImage = ImageIO.read(clearFile);
                System.out.println("Clear icon loaded successfully from: " + clearFile.getAbsolutePath());
            }
            
            // If we still couldn't find the icon, use a different approach with resource stream
            if (searchIconImage == null) {
                System.out.println("Still looking for search icon in additional locations...");
                
                // Try using class loader resource stream as a last resort
                try {
                    ClassLoader classLoader = getClass().getClassLoader();
                    java.net.URL resourceURL = classLoader.getResource("resources/images/Candidate Search/search.png");
                    if (resourceURL != null) {
                        searchIconImage = ImageIO.read(resourceURL);
                        System.out.println("Search icon loaded using class loader.");
                    }
                } catch (Exception e) {
                    System.out.println("Error loading search icon with class loader: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading search icon: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Immediately recenters the rectangles based on current window width
     */
    private void recenterRectangles() {
        // Find all rectangles in the content panel
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (((BorderLayout)getContentPane().getLayout()).getConstraints(comp) == BorderLayout.CENTER) {
                    JPanel contentPanel = (JPanel) panel;
                    
                    // Get the filter rectangle from our modular component
                    JPanel filterRect = filterDropdown.getFilterRectangle();
                    
                    // Get their current bounds
                    Rectangle firstBounds = filterRect.getBounds();
                    Rectangle secondBounds = secondRectangle.getBounds();
                    Rectangle thirdBounds = thirdRectangle.getBounds();
                    
                    // Calculate total width including gaps
                    int width1 = firstBounds.width;
                    int width2 = secondBounds.width;
                    int width3 = thirdBounds.width;
                    int gap1 = secondBounds.x - (firstBounds.x + firstBounds.width);
                    int gap2 = thirdBounds.x - (secondBounds.x + secondBounds.width);
                    int totalWidth = width1 + width2 + width3 + gap1 + gap2;
                    
                    // Calculate new starting X to center
                    int newStartX = (getWidth() - totalWidth) / 2;
                    
                    // Pre-calculate divider properties for use later
                    int windowWidth = getWidth();
                    int dividerWidth = (int)(windowWidth * 0.8);
                    int dividerX = (windowWidth - dividerWidth) / 2;
                    
                    // Update positions
                    filterRect.setBounds(newStartX, firstBounds.y, width1, firstBounds.height);
                    secondRectangle.setBounds(newStartX + width1 + gap1, secondBounds.y, width2, secondBounds.height);
                    thirdRectangle.setBounds(newStartX + width1 + gap1 + width2 + gap2, thirdBounds.y, width3, thirdBounds.height);
                    
                    // Update divider line position
                    Component[] components = contentPanel.getComponents();
                    for (Component c : components) {
                        if (c instanceof JPanel && c != filterRect && c != secondRectangle && 
                            c != thirdRectangle && c != provinceDropdown.getProvinceRectangle() &&
                            "dividerLine".equals(c.getName())) {
                            
                            // Calculate position 10px below province dropdown
                            JPanel provinceRect = provinceDropdown.getProvinceRectangle();
                            Rectangle provinceBounds = provinceRect.getBounds();
                            int dividerY = Math.max(thirdBounds.y + thirdBounds.height + 10, 
                                                  provinceBounds.y + provinceBounds.height + 10);
                            
                            // Update divider position with centered position
                            c.setBounds(dividerX, dividerY, dividerWidth, c.getHeight());
                            break;
                        }
                    }
                    
                    // Update dropdown position
                    filterDropdown.updatePositionOnResize();
                    
                    // Update province dropdown position - place under Find button
                    if (provinceDropdown != null) {
                        JPanel provinceRectangle = provinceDropdown.getProvinceRectangle();
                        // Position with right edge aligned to Find button, 5px gap below, 200px width
                        int findButtonRightEdge = newStartX + width1 + gap1 + width2 + gap2 + width3;
                        int provinceX = findButtonRightEdge - 200; // Right align with 200px width
                        provinceRectangle.setBounds(provinceX, thirdBounds.y + thirdBounds.height + 5, 200, 45);
                        provinceDropdown.updatePositionOnResize();
                        
                        // Ensure province rectangle is at the top of the z-order
                        contentPanel.setComponentZOrder(provinceRectangle, 0);
                    }
                    
                    // Also update the candidate card position if it exists
                    if (cardPanel == null) {
                        // No candidate cards to update
                    } else {
                        // Update candidate cards positioning, centering them below the divider line
                        updateCandidateCardsLayout(dividerX, dividerWidth);
                    }
                    
                    // Ensure proper z-ordering of all components
                    adjustDividerForDropdowns();
                    
                    break;
                }
            }
        }
    }
    
    /**
     * Creates a "Find" button with the same style as the filter dropdown
     */
    private JPanel createFindButton(int width, int height) {
        // Create darker shades for hover and click effects
        final Color hoverBlue = new Color(0x22, 0x2C, 0x66); // Darker shade for hover
        final Color clickBlue = new Color(0x1A, 0x21, 0x4D); // Even darker for click
        
        // Track mouse states with boolean flags
        final boolean[] isHovering = {false};
        final boolean[] isClicking = {false};
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Choose color based on mouse state
                if (isClicking[0]) {
                    g2d.setColor(clickBlue);
                } else if (isHovering[0]) {
                    g2d.setColor(hoverBlue);
                } else {
                    g2d.setColor(filterBlue);
                }
                
                // Draw rounded rectangle with 10px corner radius to match filter dropdown
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw "Find" text in white, centered
                g2d.setColor(Color.WHITE);
                
                // Use Inter Medium font if available
                Font findFont = interMedium != null ? 
                    interMedium.deriveFont(16f) : 
                    new Font("Sans-Serif", Font.BOLD, 16);
                g2d.setFont(findFont);
                
                // Center the text - shift down 1px when clicking for "push" effect
                FontMetrics fm = g2d.getFontMetrics();
                String text = "Find";
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = ((getHeight() - textHeight) / 2) + fm.getAscent();
                
                // Apply 1px downward shift when clicking for push effect
                if (isClicking[0]) {
                    y += 1;
                }
                
                g2d.drawString(text, x, y);
            }
        };
        
        // Set cursor to hand to indicate it's clickable
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setPreferredSize(new Dimension(width, height));
        panel.setOpaque(false);
        
        // Add mouse listeners for interaction effects
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
                // Perform search when Find button is clicked
                String query = searchField.getText();
                if (query.equals("Search for candidates or issues...")) {
                    query = "";
                }
                
                // Apply search to card panel
                if (cardPanel != null) {
                    cardPanel.filterCards(query);
                }
                
                System.out.println("Find button clicked: Searching for '" + query + "'");
            }
        });
        
        return panel;
    }
    
    /**
     * Creates a horizontal divider line
     */
    private JPanel createDividerLine() {
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(dividerColor); // Use the defined divider color
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        divider.setOpaque(false);
        divider.setName("dividerLine"); // Add name for easy identification
        return divider;
    }
    
    /**
     * Adjusts the divider line position to ensure it's below any open dropdowns and centered in the window
     */
    private void adjustDividerForDropdowns() {
        // Find content panel (the BorderLayout.CENTER component of the main panel)
        Component[] mainComponents = getContentPane().getComponents();
        JPanel contentPanel = null;
        
        for (Component comp : mainComponents) {
            if (comp instanceof JPanel && 
                ((BorderLayout)getContentPane().getLayout()).getConstraints(comp) == BorderLayout.CENTER) {
                contentPanel = (JPanel) comp;
                break;
            }
        }
        
        if (contentPanel == null) {
            return; // Content panel not found
        }
        
        // Find the divider line component
        Component[] components = contentPanel.getComponents();
        JPanel dividerLine = null;
        
        for (Component comp : components) {
            if (comp instanceof JPanel && "dividerLine".equals(comp.getName())) {
                dividerLine = (JPanel) comp;
                break;
            }
        }
        
        if (dividerLine != null) {
            // Calculate position for divider based on other components and window size
            int windowWidth = getWidth();
            
            // Calculate the divider width - use 80% of window width for better proportions
            int dividerWidth = (int)(windowWidth * 0.8);
            
            // Center the divider horizontally in the window
            int dividerX = (windowWidth - dividerWidth) / 2;
            
            // Find the current Y position, which should be below all top elements
            Rectangle bounds = dividerLine.getBounds();
            
            // Update the divider position to be centered horizontally
            dividerLine.setBounds(dividerX, bounds.y, dividerWidth, bounds.height);
            
            // Make sure the divider is at the bottom of the z-order
            contentPanel.setComponentZOrder(dividerLine, contentPanel.getComponentCount() - 1);
        }
        
        // Ensure the card panel is below the divider but above other components
        if (cardPanel != null && contentPanel != null) {
            contentPanel.setComponentZOrder(cardPanel, contentPanel.getComponentCount() - 2);
        }
        
        // FIRST: Process regular dropdowns
        for (Component comp : components) {
            String name = comp.getName();
            if (name != null && (
                name.contains("dropdown") || 
                name.contains("Dropdown") || 
                name.equals("dropdown") || 
                name.equals("dropdown-content")
            )) {
                // Regular dropdown components get z-order 1
                contentPanel.setComponentZOrder(comp, 1);
            }
        }
        
        // SECOND: Process the provinceRect separately
        JPanel provinceRect = provinceDropdown.getProvinceRectangle();
        if (provinceRect != null) {
            contentPanel.setComponentZOrder(provinceRect, 0);
        }
        
        // THIRD: Process province dropdowns - they get highest priority
        for (Component comp : components) {
            String name = comp.getName();
            if (name != null && (
                name.equals("province-dropdown-content") ||
                name.equals("province-dropdown-scrollpane")
            )) {
                // Province dropdown components always on top (z-order 0)
                contentPanel.setComponentZOrder(comp, 0);
            }
        }
        
        // Request repainting for changes to take effect
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Adds a candidate card section below the divider line
     */
    private void addCandidateCardSection(JPanel parentPanel, int startX, int startY, int width) {
        // Create the card panel instead of managing individual cards
        cardPanel = new CandidateCardPanel(
            interRegular,
            interSemiBold,
            interMedium,
            this::handleViewProfile
        );
        
        // Set very low background opacity (10%)
        cardPanel.setBackgroundOpacity(100f);
        
        // Calculate proper height - either fixed height or fill available space
        int availableHeight = parentPanel.getHeight() - startY - 20;
        // Minimum height of 400px to ensure visibility
        int cardPanelHeight = Math.max(400, availableHeight);
        
        // Set position and size
        cardPanel.setBounds(startX, startY, width, cardPanelHeight);
        
        // Set top margin
        cardPanel.setTopMargin(5);
        
        // Add the panel to the parent
        parentPanel.add(cardPanel);
        
        // Set component priority to ensure proper layering
        parentPanel.setComponentZOrder(cardPanel, parentPanel.getComponentCount() - 2);
        
        // Force visualization
        cardPanel.revalidate();
        parentPanel.revalidate();
        parentPanel.repaint();
        
        // Log successful addition for debugging
        System.out.println("Successfully added CandidateCardPanel: " + 
                         "x=" + startX + ", y=" + startY + 
                         ", width=" + width + ", height=" + cardPanelHeight);
    }
    
    /**
     * Handle view profile button click
     */
    private void handleViewProfile(String candidateName) {
        System.out.println("View profile clicked for: " + candidateName);
        // TODO: Navigate to candidate profile page
    }
    
    /**
     * Updates the layout of candidate cards based on window size
     */
    private void updateCandidateCardsLayout(int startX, int totalWidth) {
        if (cardPanel != null) {
            // Get current position and size
            Rectangle bounds = cardPanel.getBounds();
            
            // Calculate available height from current position to bottom of window
            Component contentPanel = null;
            for (Component comp : getContentPane().getComponents()) {
                if (comp instanceof JPanel && 
                    ((BorderLayout)getContentPane().getLayout()).getConstraints(comp) == BorderLayout.CENTER) {
                    contentPanel = comp;
                    break;
                }
            }
            
            int availableHeight = contentPanel != null ? 
                contentPanel.getHeight() - bounds.y - 20 : 400;
            
            // Update bounds of the card panel - maintain current Y position
            cardPanel.setBounds(startX, bounds.y, totalWidth, Math.max(400, availableHeight));
            
            // Force update
            cardPanel.revalidate();
            cardPanel.repaint();
        }
    }
    
    /**
     * Update clear icon visibility based on search field content
     */
    private void updateClearIconVisibility(boolean visible) {
        if (clearIconLabel != null && clearIconVisible != visible) {
            clearIconLabel.setVisible(visible);
            clearIconVisible = visible;
        }
    }
    
    // Add the method to handle redirection to landing page
    private void navigateToLandingPage() {
        // Save current window size before closing
        Dimension currentSize = getSize();
        Point currentLocation = getLocation();
        
        // Close this window
        dispose();
        
        // Create and show the landing page with same size and position
        LandingPageUI landingPage = new LandingPageUI();
        landingPage.setSize(currentSize);
        landingPage.setLocation(currentLocation);
        landingPage.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CandidateSearchUI searchPage = new CandidateSearchUI();
                searchPage.setVisible(true);
            }
        });
    }
} 