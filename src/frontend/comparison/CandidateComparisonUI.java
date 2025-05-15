package frontend.comparison;

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
import frontend.comparison.SearchCandidateCompare;
import frontend.comparison.CompareSelection;
import frontend.comparison.CandidateDataManager;

/**
 * Candidate Comparison UI for the Gabay application
 * Simplified template with just backdrop and header logo
 */
public class CandidateComparisonUI extends JFrame {
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
    private Color orangeAccent = new Color(0xF8, 0xB3, 0x48); // #F8B348
    
    // Background image
    private BufferedImage backgroundImage;
    private boolean showBackgroundImage = true;
    private int BACKDROP_WIDTH = DEFAULT_BACKDROP_WIDTH;
    private int BACKDROP_HEIGHT = DEFAULT_BACKDROP_HEIGHT;
    private int BACKDROP_X = DEFAULT_BACKDROP_X;
    private int BACKDROP_Y = DEFAULT_BACKDROP_Y;
    
    // Header logo
    private BufferedImage headerLogoImage;
    
    // Text positioning constants
    private final int TITLE_X = 140; // Base X position in the reference window size
    private final int TITLE_Y = 181; // Adjusted Y position to be closer to rectangles
    private final int PARAGRAPH_WIDTH = 1154; // Base width in reference window size
    private final int ELEMENT_SPACING = -5; // Negative spacing to create overlap
    private final Color TITLE_COLOR = new Color(0xF8, 0xB3, 0x48); // #F8B348
    
    // Window dimensions
    private int initialWindowWidth = 1411; // Fixed window width
    private int initialWindowHeight = 970; // Fixed window height
    
    // Add searchPanel variable declaration after the header logo
    private SearchCandidateCompare searchPanel;
    
    // Add selection panel for comparison categories
    private CompareSelection selectionPanel;
    
    // Add comparison content panels
    private ProfileBackgroundCompare profilePanel;
    private FocusedAdvocaciesCompare advocaciesPanel;
    private SocialStancesCompare socialPanel;
    private JPanel currentContentPanel; // Currently displayed panel
    
    // Add a container for the content panels
    private JPanel comparisonContentContainer;
    
    // Add these variables to make position adjustable
    private int searchPanelXOffset = DEFAULT_SEARCH_PANEL_X_OFFSET;
    private int searchPanelY = 180; // Custom value, different from default
    
    // Add opacity control
    private float backdropOpacity = DEFAULT_BACKDROP_OPACITY;
    
    // Original default values for positions
    private static final int DEFAULT_BACKDROP_X = -1250;
    private static final int DEFAULT_BACKDROP_Y = -242;
    private static final int DEFAULT_BACKDROP_WIDTH = 2555;
    private static final int DEFAULT_BACKDROP_HEIGHT = 2154;
    private static final float DEFAULT_BACKDROP_OPACITY = 0.05f;
    private static final int DEFAULT_SEARCH_PANEL_X_OFFSET = 0;
    private static final int DEFAULT_SEARCH_PANEL_Y = 270;
    
    public CandidateComparisonUI() {
        // Load fonts
        loadFonts();
        
        // Load background image
        loadBackgroundImage();
        
        // Load header logo
        loadHeaderLogoImage();
        
        // Set up the window
        setTitle("Gabáy - Candidate Comparison");
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
                    
                    // Set the opacity to the value specified
                    AlphaComposite alphaComposite = AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, backdropOpacity);
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
                g2d.drawString("Compare Candidates.", scaledTitleX, scaledTitleY);
                
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
                String paragraphText = "Compare multiple candidates side by side to see how their " +
                    "platforms, positions, and voting records stack up. This tool helps you make " +
                    "informed decisions by highlighting similarities and differences.";
                
                // Calculate paragraph position below title - make them super close
                FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);
                // Use negative spacing to push paragraph up into title space
                int paragraphY = scaledTitleY + (titleMetrics.getHeight() / 2) + scaledSpacing;
                
                // Draw wrapped paragraph text
                drawWrappedText(g2d, paragraphText, scaledTitleX, paragraphY, scaledParagraphWidth);
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
                    // Go back to landing page
                    Dimension currentSize = getSize();
                    dispose();
                    LandingPageUI landingPage = new LandingPageUI();
                    landingPage.setSize(currentSize); // Set the same size as current window
                    landingPage.setLocationRelativeTo(null); // Center on screen
                    landingPage.setVisible(true);
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
                    // Go back to landing page
                    Dimension currentSize = getSize();
                    dispose();
                    LandingPageUI landingPage = new LandingPageUI();
                    landingPage.setSize(currentSize);
                    landingPage.setLocationRelativeTo(null);
                    landingPage.setVisible(true);
                }
            });
        }
        
        logoPanel.add(logoLabel);
        
        // Add components to panels
        headerPanel.add(logoPanel, BorderLayout.WEST);
        
        // Add a content panel (empty for now)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null); // Use null layout for precise positioning
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Create the search panel
        searchPanel = new SearchCandidateCompare(interRegular, interMedium, interSemiBold, this::handleCompareClicked);
        
        // Calculate paragraph bottom position - for proper placement below the title
        FontMetrics titleMetrics = getFontMetrics(interBlack != null ? 
                                                 interBlack.deriveFont(70f) : 
                                                 new Font("Sans-Serif", Font.BOLD, 70));
        int titleHeight = titleMetrics.getHeight();
        FontMetrics paraMetrics = getFontMetrics(interSemiBold != null ? 
                                                  interSemiBold.deriveFont(18f) : 
                                                  new Font("Sans-Serif", Font.PLAIN, 18));
        int paraHeight = paraMetrics.getHeight() * 3; // Approximate for 3 lines
        int topMargin = 270; // Position below title content with some margin
        
        // Position the search panel
        searchPanel.setBounds((getWidth() - 940) / 2 + searchPanelXOffset, searchPanelY, 940, 70);
        contentPanel.add(searchPanel);
        
        // Add a divider line 20px below the search panel
        JPanel divider = createDividerLine(940);
        divider.setBounds((getWidth() - 940) / 2 + searchPanelXOffset, searchPanelY + 70 + 20, 940, 1);
        contentPanel.add(divider);
        
        // Create and add the category selection panel 30px below the divider
        selectionPanel = new CompareSelection(interRegular, interMedium, interSemiBold, this::handleCategorySelected);
        int selectionWidth = 640; // Total width of the three buttons with spacing
        selectionPanel.setBounds((getWidth() - selectionWidth) / 2, searchPanelY + 70 + 20 + 30, selectionWidth, 45);
        contentPanel.add(selectionPanel);
        
        // Create the comparison content container 30px below the selection buttons
        comparisonContentContainer = new JPanel();
        comparisonContentContainer.setLayout(new BorderLayout());
        comparisonContentContainer.setOpaque(false);
        comparisonContentContainer.setBounds(100, searchPanelY + 70 + 20 + 30 + 45 + 30, getWidth() - 200, 350);
        contentPanel.add(comparisonContentContainer);
        
        // Create all content panels
        profilePanel = new ProfileBackgroundCompare(interRegular, interMedium, interSemiBold);
        advocaciesPanel = new FocusedAdvocaciesCompare(interRegular, interMedium, interSemiBold);
        socialPanel = new SocialStancesCompare(interRegular, interMedium, interSemiBold);
        
        // Default to profile panel
        currentContentPanel = profilePanel;
        comparisonContentContainer.add(currentContentPanel, BorderLayout.CENTER);
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Set content pane
        setContentPane(mainPanel);
        
        // Add component listener to handle resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Update search panel position on resize
                if (searchPanel != null) {
                    searchPanel.setBounds((getWidth() - 940) / 2 + searchPanelXOffset, searchPanelY, 940, 70);
                }
                
                // Update selection panel position on resize
                if (selectionPanel != null) {
                    int selectionWidth = 640;
                    selectionPanel.setBounds((getWidth() - selectionWidth) / 2, searchPanelY + 70 + 20 + 30, selectionWidth, 45);
                }
                
                // Update comparison content container
                if (comparisonContentContainer != null) {
                    comparisonContentContainer.setBounds(100, searchPanelY + 70 + 20 + 30 + 45 + 30, getWidth() - 200, 350);
                }
                
                // Update divider line if present
                for (Component c : getContentPane().getComponents()) {
                    if (c instanceof JPanel) {
                        JPanel panel = (JPanel) c;
                        if (panel.getLayout() instanceof BorderLayout) {
                            for (Component contentComp : panel.getComponents()) {
                                if (contentComp instanceof JPanel && 
                                    BorderLayout.CENTER.equals(((BorderLayout)panel.getLayout()).getConstraints(contentComp))) {
                                    
                                    // This should be the content panel
                                    JPanel contentPanel = (JPanel) contentComp;
                                    for (Component comp : contentPanel.getComponents()) {
                                        // Check if this is the divider line
                                        if (comp.getHeight() == 1 && comp.getWidth() > 500) {
                                            comp.setBounds((getWidth() - 940) / 2 + searchPanelXOffset, comp.getY(), 940, 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
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
    
    private void handleCompareClicked(String[] candidates) {
        // Handle the comparison request
        System.out.println("Comparing candidates: " + candidates[0] + " vs " + candidates[1]);
        
        // Set the candidate names in all comparison panels
        if (!candidates[0].isEmpty() && !candidates[1].isEmpty()) {
            // Load candidate images if available
            BufferedImage leftImage = loadCandidateImage(candidates[0]);
            BufferedImage rightImage = loadCandidateImage(candidates[1]);
            
            if (profilePanel != null) {
                profilePanel.setLeftCandidate(candidates[0], leftImage);
                profilePanel.setRightCandidate(candidates[1], rightImage);
            }
            
            if (advocaciesPanel != null) {
                advocaciesPanel.setLeftCandidate(candidates[0], leftImage);
                advocaciesPanel.setRightCandidate(candidates[1], rightImage);
            }
            
            if (socialPanel != null) {
                socialPanel.setLeftCandidate(candidates[0], leftImage);
                socialPanel.setRightCandidate(candidates[1], rightImage);
            }
        } else {
            // Show error message if candidates are not selected
            JOptionPane.showMessageDialog(this, 
                "Please select two candidates to compare.", 
                "Selection Required", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Load a candidate's image from the data
     * @param candidateName Name of the candidate
     * @return BufferedImage of candidate or null if not found
     */
    private BufferedImage loadCandidateImage(String candidateName) {
        // Use the CandidateDataManager to get the candidate image
        return CandidateDataManager.getCandidateImage(candidateName);
    }
    
    /**
     * Creates a horizontal divider line
     */
    private JPanel createDividerLine(int width) {
        Color dividerColor = new Color(0xD9, 0xD9, 0xD9); // #D9D9D9
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(dividerColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        divider.setOpaque(false);
        divider.setPreferredSize(new Dimension(width, 1));
        return divider;
    }
    
    /**
     * Set the position of the search panel
     * @param xOffset horizontal offset from center (negative moves left, positive moves right)
     * @param y vertical position from top
     */
    public void setSearchPanelPosition(int xOffset, int y) {
        this.searchPanelXOffset = xOffset;
        this.searchPanelY = y;
        
        // Recalculate positions
        if (searchPanel != null) {
            int centerX = (getWidth() - 940) / 2;
            searchPanel.setBounds(centerX + xOffset, y, 940, 70);
            
            // Find and update divider line position as well
            for (Component comp : ((JPanel)getContentPane().getComponent(1)).getComponents()) {
                if (comp.getHeight() == 1 && comp.getWidth() > 500) {
                    comp.setBounds(centerX + xOffset, y + 70 + 20, 940, 1);
                    break;
                }
            }
            
            revalidate();
            repaint();
        }
    }
    
    /**
     * Set the position and size of the backdrop image
     * @param x X position of the backdrop
     * @param y Y position of the backdrop
     * @param width Width of the backdrop
     * @param height Height of the backdrop
     */
    public void setBackdropPosition(int x, int y, int width, int height) {
        this.BACKDROP_X = x;
        this.BACKDROP_Y = y;
        this.BACKDROP_WIDTH = width;
        this.BACKDROP_HEIGHT = height;
        
        // Force repaint to show the changes
        repaint();
    }
    
    /**
     * Adjust the backdrop position
     * @param xOffset X offset to apply
     * @param yOffset Y offset to apply
     */
    public void moveBackdrop(int xOffset, int yOffset) {
        this.BACKDROP_X += xOffset;
        this.BACKDROP_Y += yOffset;
        
        // Force repaint to show the changes
        repaint();
    }
    
    /**
     * Set the opacity of the backdrop image
     * @param opacity Value from 0.0 (transparent) to 1.0 (opaque)
     */
    public void setBackdropOpacity(float opacity) {
        if (opacity < 0.0f) opacity = 0.0f;
        if (opacity > 1.0f) opacity = 1.0f;
        
        this.backdropOpacity = opacity;
        repaint();
    }
    
    /**
     * Set the dimensions of the profile image in all comparison panels
     * @param width Width of the profile image
     * @param height Height of the profile image
     */
    public void setProfileImageDimensions(int width, int height) {
        // Update dimensions in all panels
        if (profilePanel != null) {
            profilePanel.setProfileImageDimensions(width, height);
        }
        
        if (advocaciesPanel != null) {
            advocaciesPanel.setProfileImageDimensions(width, height);
        }
        
        if (socialPanel != null) {
            socialPanel.setProfileImageDimensions(width, height);
        }
        
        // Refresh the UI
        if (comparisonContentContainer != null) {
            comparisonContentContainer.revalidate();
            comparisonContentContainer.repaint();
        }
    }
    
    /**
     * Reset all positions to their original default values
     */
    public void resetPositions() {
        // Reset backdrop
        this.BACKDROP_X = DEFAULT_BACKDROP_X;
        this.BACKDROP_Y = DEFAULT_BACKDROP_Y;
        this.BACKDROP_WIDTH = DEFAULT_BACKDROP_WIDTH;
        this.BACKDROP_HEIGHT = DEFAULT_BACKDROP_HEIGHT;
        this.backdropOpacity = DEFAULT_BACKDROP_OPACITY;
        
        // Reset search panel
        this.searchPanelXOffset = DEFAULT_SEARCH_PANEL_X_OFFSET;
        this.searchPanelY = DEFAULT_SEARCH_PANEL_Y;
        
        // Update positions and repaint
        if (searchPanel != null) {
            int centerX = (getWidth() - 940) / 2;
            searchPanel.setBounds(centerX, DEFAULT_SEARCH_PANEL_Y, 940, 70);
            
            // Find and update divider line position as well
            for (Component comp : ((JPanel)getContentPane().getComponent(1)).getComponents()) {
                if (comp.getHeight() == 1 && comp.getWidth() > 500) {
                    comp.setBounds(centerX, DEFAULT_SEARCH_PANEL_Y + 70 + 20, 940, 1);
                    break;
                }
            }
            
            // Update selection panel
            if (selectionPanel != null) {
                int selectionWidth = 640;
                selectionPanel.setBounds((getWidth() - selectionWidth) / 2, 
                                        DEFAULT_SEARCH_PANEL_Y + 70 + 20 + 30, 
                                        selectionWidth, 45);
            }
            
            // Update comparison content container
            if (comparisonContentContainer != null) {
                comparisonContentContainer.setBounds(100, 
                                               DEFAULT_SEARCH_PANEL_Y + 70 + 20 + 30 + 45 + 30, 
                                               getWidth() - 200, 350);
            }
        }
        
        revalidate();
        repaint();
    }
    
    // Add handling for selection changes
    private void handleCategorySelected(String category) {
        System.out.println("Category selected: " + category);
        
        if (comparisonContentContainer != null) {
            // Remove current content
            comparisonContentContainer.removeAll();
            
            // Determine which panel to show based on category
            if (category.equals(CompareSelection.PROFILE)) {
                currentContentPanel = profilePanel;
            } else if (category.equals(CompareSelection.ADVOCACIES)) {
                currentContentPanel = advocaciesPanel;
            } else if (category.equals(CompareSelection.SOCIAL)) {
                currentContentPanel = socialPanel;
            }
            
            // Add the selected panel
            if (currentContentPanel != null) {
                comparisonContentContainer.add(currentContentPanel, BorderLayout.CENTER);
                comparisonContentContainer.revalidate();
                comparisonContentContainer.repaint();
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CandidateComparisonUI comparisonUI = new CandidateComparisonUI();
                comparisonUI.setVisible(true);
            }
        });
    }
} 