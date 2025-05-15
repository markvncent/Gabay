package frontend.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;
import java.util.HashMap;

public class AdminLoginUI extends JFrame {
    private BufferedImage backdropImage;
    private BufferedImage headerImage;
    
    // Colors matching the main UI
    private Color searchBorderColor = new Color(0xCB, 0xD5, 0xE1); // #CBD5E1
    private Color dividerColor = new Color(0xD9, 0xD9, 0xD9); // #D9D9D9
    private Color placeholderColor = Color.GRAY;
    
    // UI Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Font interBlack;
    private Font interRegular;
    private Font interSemiBold;
    private float originalButtonFontSize = 38f; // Original font size for buttons
    
    public AdminLoginUI() {
        // Load fonts
        loadFonts();
        
        // Set up the window
        setTitle("Gabáy - Admin Login");
        setSize(1411, 970); // Fixed window size
        setResizable(false); // Make window non-resizable
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Load images
        loadImages();
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backdropImage != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    
                    // Enable high-quality rendering
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                       RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
                                       RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                       RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Set opacity to 3%
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.03f));
                    
                    // Calculate dimensions for 200% scaling
                    int scaledWidth = (int)(backdropImage.getWidth() * 2);
                    int scaledHeight = (int)(backdropImage.getHeight() * 2);
                    
                    // Calculate position to center the image
                    int x = (getWidth() - scaledWidth) / 2;
                    int y = (getHeight() - scaledHeight) / 2;
                    
                    // Draw the scaled image centered with opacity
                    g2d.drawImage(backdropImage, x, y, scaledWidth, scaledHeight, this);
                    
                    // Reset composite for header image
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    
                    if (headerImage != null) {
                        // Calculate header dimensions (25% of window width)
                        int headerWidth = (int)(getWidth() * 0.25);
                        int headerHeight = (int)((double)headerImage.getHeight() / headerImage.getWidth() * headerWidth);
                        
                        // Position header image at top center, 50px from top
                        int headerX = (getWidth() - headerWidth) / 2;
                        int headerY = 50;
                        
                        // Draw the header image with high quality
                        g2d.drawImage(headerImage, headerX, headerY, headerWidth, headerHeight, this);
                    }
                    
                    g2d.dispose();
                }
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(null); // Use null layout for absolute positioning
        
        // Calculate positions based on header
        int headerHeight = headerImage != null ? (int)((double)headerImage.getHeight() / headerImage.getWidth() * (getWidth() * 0.25)) : 0;
        int startY = 50 + headerHeight + 30; // 30px gap after header
        
        // Add divider line
        JPanel dividerLine = createDividerLine();
        int dividerWidth = (int)(getWidth() * 0.8); // 80% of window width
        int dividerX = (getWidth() - dividerWidth) / 2;
        dividerLine.setBounds(dividerX, startY, dividerWidth, 1);
        mainPanel.add(dividerLine);
        
        // Create input fields
        int fieldWidth = 500;
        int fieldHeight = 55;
        int fieldX = (getWidth() - fieldWidth) / 2;
        int fieldGap = 20;
        
        // Username field
        JPanel usernameWrapper = createStyledInputField("Username", true);
        usernameWrapper.setBounds(fieldX, startY + 30, fieldWidth, fieldHeight);
        mainPanel.add(usernameWrapper);
        
        // Password field
        JPanel passwordWrapper = createStyledInputField("Password", false);
        passwordWrapper.setBounds(fieldX, startY + 30 + fieldHeight + fieldGap, fieldWidth, fieldHeight);
        mainPanel.add(passwordWrapper);
        
        // Add admin button - positioned below password field and centered
        JButton adminButton = createStyledAdminButton("Login", new Color(0x2F, 0x39, 0x8E));
        int adminButtonWidth = 250;
        int adminButtonHeight = 50;
        int adminButtonY = startY + 30 + (fieldHeight * 2) + (fieldGap * 2) + 20;
        int adminButtonX = (getWidth() - adminButtonWidth) / 2;
        adminButton.setBounds(adminButtonX, adminButtonY, adminButtonWidth, adminButtonHeight);
        mainPanel.add(adminButton);
        
        setContentPane(mainPanel);
        
        // Add component listener for window resize
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Recalculate positions
                int newHeaderHeight = headerImage != null ? 
                    (int)((double)headerImage.getHeight() / headerImage.getWidth() * (getWidth() * 0.25)) : 0;
                int newStartY = 50 + newHeaderHeight + 30;
                
                // Update divider
                int newDividerWidth = (int)(getWidth() * 0.8);
                int newDividerX = (getWidth() - newDividerWidth) / 2;
                dividerLine.setBounds(newDividerX, newStartY, newDividerWidth, 1);
                
                // Update input fields
                int newFieldX = (getWidth() - fieldWidth) / 2;
                usernameWrapper.setBounds(newFieldX, newStartY + 30, fieldWidth, fieldHeight);
                passwordWrapper.setBounds(newFieldX, newStartY + 30 + fieldHeight + fieldGap, fieldWidth, fieldHeight);
                
                // Update admin button position
                int newAdminButtonX = (getWidth() - adminButtonWidth) / 2;
                int newAdminButtonY = newStartY + 30 + (fieldHeight * 2) + (fieldGap * 2) + 20;
                adminButton.setBounds(newAdminButtonX, newAdminButtonY, adminButtonWidth, adminButtonHeight);
                
                revalidate();
                repaint();
            }
        });
    }
    
    private JPanel createDividerLine() {
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(dividerColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        divider.setOpaque(false);
        return divider;
    }
    
    private JPanel createStyledInputField(String placeholder, boolean isUsername) {
        JPanel wrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill with white background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2d.setColor(searchBorderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
            }
        };
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(false);
        
        // Create text field
        JTextField field;
        if (isUsername) {
            field = new JTextField(placeholder);
            usernameField = field;
        } else {
            field = new JPasswordField(placeholder);
            passwordField = (JPasswordField) field;
        }
        
        field.setForeground(placeholderColor);
        field.setFont(interRegular.deriveFont(16f));
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        // Add focus listener for placeholder behavior
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (!isUsername) {
                        ((JPasswordField)field).setEchoChar('•');
                    }
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(placeholderColor);
                    if (!isUsername) {
                        ((JPasswordField)field).setEchoChar((char)0);
                    }
                }
            }
        });
        
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }
    
    private void loadImages() {
        try {
            // Load backdrop image
            String backdropPath = "resources/images/Landing-Backdrop.png";
            File backdropFile = new File(backdropPath);
            if (backdropFile.exists()) {
                backdropImage = ImageIO.read(backdropFile);
                System.out.println("Backdrop image loaded successfully from: " + backdropPath);
            } else {
                System.err.println("Backdrop image not found at: " + backdropPath);
            }
            
            // Load header image
            String headerPath = "resources/images/Landing_Header.png";
            File headerFile = new File(headerPath);
            if (headerFile.exists()) {
                headerImage = ImageIO.read(headerFile);
                System.out.println("Header image loaded successfully from: " + headerPath);
            } else {
                System.err.println("Header image not found at: " + headerPath);
            }
        } catch (IOException e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadFonts() {
        try {
            // Load Inter-Black font
            File interBlackFile = new File("lib/fonts/Inter_18pt-Black.ttf");
            if (interBlackFile.exists()) {
                interBlack = Font.createFont(Font.TRUETYPE_FONT, interBlackFile);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(interBlack);
            } else {
                System.err.println("Inter-Black font not found, falling back to system font");
                interBlack = new Font("Sans-Serif", Font.BOLD, 12);
            }
            
            // Load Inter-Regular font
            File interRegularFile = new File("lib/fonts/Inter_18pt-Regular.ttf");
            if (interRegularFile.exists()) {
                interRegular = Font.createFont(Font.TRUETYPE_FONT, interRegularFile);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(interRegular);
            } else {
                System.err.println("Inter-Regular font not found, falling back to system font");
                interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
            }
            
            // Load Inter-SemiBold font
            File interSemiBoldFile = new File("lib/fonts/Inter_18pt-SemiBold.ttf");
            if (interSemiBoldFile.exists()) {
                interSemiBold = Font.createFont(Font.TRUETYPE_FONT, interSemiBoldFile);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(interSemiBold);
            } else {
                System.err.println("Inter-SemiBold font not found, falling back to system font");
                interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
            }
        } catch (FontFormatException | IOException e) {
            System.err.println("Error loading fonts: " + e.getMessage());
            interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
            interBlack = new Font("Sans-Serif", Font.BOLD, 12);
            interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
        }
    }
    
    private JButton createStyledAdminButton(String text, Color bgColor) {
        class StyledAdminButton extends JButton {
            private boolean isHovered = false;
            private int slideOffset = 0;
            private final int MAX_SLIDE = 250;
            private final int ANIMATION_DURATION = 300;
            private Timer animationTimer;
            private String frontText = "Login";
            private String beneathText = "Enter";
            
            private long animationStartTime = 0;
            private int animationStartOffset = 0;
            private int animationTargetOffset = 0;
            private float zoomFactor = 1.0f;
            private final float MAX_ZOOM = 1.08f;
            
            public StyledAdminButton(String text) {
                super(text);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                animationTimer = new Timer(16, e -> {
                    long currentTime = System.currentTimeMillis();
                    float progress = Math.min(1.0f, (currentTime - animationStartTime) / (float)ANIMATION_DURATION);
                    
                    float easedProgress = bezierEase(progress);
                    slideOffset = animationStartOffset + (int)((animationTargetOffset - animationStartOffset) * easedProgress);
                    
                    if (isHovered) {
                        float zoomProgress = Math.min(1.0f, progress / 0.4f);
                        zoomFactor = 1.0f + (MAX_ZOOM - 1.0f) * bezierEase(zoomProgress);
                    } else {
                        float zoomProgress = Math.min(1.0f, progress / 0.4f);
                        zoomFactor = MAX_ZOOM - (MAX_ZOOM - 1.0f) * bezierEase(zoomProgress);
                    }
                    
                    if (progress >= 1.0f) {
                        slideOffset = animationTargetOffset;
                        zoomFactor = isHovered ? MAX_ZOOM : 1.0f;
                        ((Timer)e.getSource()).stop();
                    }
                    
                    repaint();
                });
                
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        startAnimation(true);
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        startAnimation(false);
                    }
                });
            }
            
            private float bezierEase(float t) {
                if (t <= 0) return 0;
                if (t >= 1) return 1;
                return cubicBezier(t, 0.33f, 0.0f, 0.67f, 1.0f);
            }
            
            private float cubicBezier(float t, float p1x, float p1y, float p2x, float p2y) {
                float cx = 3.0f * p1x;
                float bx = 3.0f * (p2x - p1x) - cx;
                float ax = 1.0f - cx - bx;
                
                float cy = 3.0f * p1y;
                float by = 3.0f * (p2y - p1y) - cy;
                float ay = 1.0f - cy - by;
                
                return ((ay * t + by) * t + cy) * t;
            }
            
            private void startAnimation(boolean hovering) {
                isHovered = hovering;
                animationStartTime = System.currentTimeMillis();
                animationStartOffset = slideOffset;
                animationTargetOffset = hovering ? MAX_SLIDE : 0;
                
                if (!animationTimer.isRunning()) {
                    animationTimer.start();
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int cornerRadius = 8;
                
                if (zoomFactor > 1.0f) {
                    float scaleX = zoomFactor;
                    float scaleY = zoomFactor;
                    float transX = width * (1 - scaleX) / 2;
                    float transY = height * (1 - scaleY) / 2;
                    g2d.translate(transX, transY);
                    g2d.scale(scaleX, scaleY);
                }
                
                g2d.setColor(getBackground());
                
                if (zoomFactor > 1.01f) {
                    Color bgColor = getBackground();
                    float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
                    float brightnessFactor = Math.min(1.0f, hsb[2] * 1.15f);
                    Color brighterBg = Color.getHSBColor(hsb[0], hsb[1], brightnessFactor);
                    g2d.setColor(brighterBg);
                }
                
                g2d.fill(new RoundRectangle2D.Double(0, 0, width, height, cornerRadius, cornerRadius));
                
                if (slideOffset > 0) {
                    g2d.setColor(getBackground());
                    g2d.setClip(new RoundRectangle2D.Double(-slideOffset, 0, width, height, cornerRadius, cornerRadius));
                    g2d.fillRect(-slideOffset, 0, width, height);
                    g2d.setClip(null);
                    
                    Color darkerBg = getBackground().darker();
                    g2d.setColor(darkerBg);
                    g2d.fill(new RoundRectangle2D.Double(0, 0, width, height, cornerRadius, cornerRadius));
                    
                    g2d.setColor(Color.WHITE);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(beneathText);
                    int textX = (width - textWidth) / 2;
                    int textY = (height + fm.getAscent() - fm.getDescent()) / 2;
                    g2d.drawString(beneathText, textX, textY);
                    
                    g2d.setColor(getBackground());
                    g2d.fill(new RoundRectangle2D.Double(-slideOffset, 0, width, height, cornerRadius, cornerRadius));
                    
                    if (slideOffset < width) {
                        drawButtonText(g2d, -slideOffset);
                    }
                } else {
                    drawButtonText(g2d, 0);
                }
                
                g2d.dispose();
            }
            
            private void drawButtonText(Graphics2D g2d, int xOffset) {
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(frontText);
                int textX = (getWidth() - textWidth) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.drawString(frontText, textX + xOffset + 1, textY + 1);
                
                g2d.setColor(getForeground());
                g2d.drawString(frontText, textX + xOffset, textY);
            }
        }
        
        StyledAdminButton button = new StyledAdminButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        if (interSemiBold != null) {
            Map<TextAttribute, Object> attributes = new HashMap<>();
            attributes.put(TextAttribute.TRACKING, -0.05);
            Font adminFont = interSemiBold.deriveFont(16f).deriveFont(attributes);
            button.setFont(adminFont);
        } else {
            button.setFont(new Font("Sans-Serif", Font.BOLD, 16));
        }
        
        // Add action listener to redirect to AdminPanelUI when clicked
        button.addActionListener(e -> {
            // Directly redirect to AdminPanelUI without checking credentials
            Dimension currentSize = getSize();
            dispose(); // Close current window
            AdminPanelUI adminPanel = new AdminPanelUI();
            adminPanel.setSize(currentSize); // Set the same size as current window
            adminPanel.setLocationRelativeTo(null); // Center on screen
            adminPanel.setVisible(true);
        });
        
        return button;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminLoginUI adminLogin = new AdminLoginUI();
            adminLogin.setVisible(true);
        });
    }
} 