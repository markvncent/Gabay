package frontend.utils;

import javax.swing.*;
import frontend.landingpage.LandingPageUI;

/**
 * Main application launcher class.
 * Shows the splash screen on first launch, then starts the application.
 */
public class SplashScreenLauncher {
    
    /**
     * Main entry point for the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Check if this is the first launch
            if (SplashScreen.isFirstLaunch()) {
                // Show splash screen
                SplashScreen splash = new SplashScreen();
                splash.showSplash();
            } else {
                // Skip splash screen and launch directly
                LandingPageUI ui = new LandingPageUI();
                ui.setVisible(true);
            }
        });
    }
} 