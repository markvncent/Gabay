# GabáyApp: Political Candidate Information Platform

GabáyApp is a Java Swing application designed to help voters explore and learn about political candidates, their platforms, and their stances on various issues. The app provides a clean, intuitive interface for discovering candidate information.

## Features

- **Candidate Search**: Search for candidates based on specific issues or keywords
- **Interactive UI**: Modern interface with smooth animations and responsive design
- **Cross-platform**: Works on Windows, macOS, and Linux

## Screenshots

(Screenshots would be here - consider adding screenshots of your application's main screens)

## System Requirements

- Java Runtime Environment (JRE) 8 or higher
- Minimum 4GB RAM recommended
- 50MB disk space

## Directory Structure

```
GabayApp/
├── lib/                  # Libraries and fonts
│   └── fonts/            # Inter font family
├── resources/            # Application resources
│   └── images/           # Images and icons
│       ├── Buttons Icon/ # Button icons
│       └── Candidate Search/ # Search-related icons
├── src/                  # Source code
│   ├── App.java          # Main application entry point
│   ├── LandingPage.java  # Landing page UI
│   ├── AdminLogin.java    # Admin login interface
│   └── CandidateSearch.java # Candidate search interface
└── README.md             # This file
```

## Setup & Installation

### Option 1: Running from Source

1. Ensure you have JDK 8 or higher installed
   ```
   java -version
   ```

2. Clone or download this repository
   ```
   git clone https://github.com/yourusername/GabayApp.git
   ```

3. Navigate to the project directory
   ```
   cd GabayApp
   ```

4. Compile the source code
   ```
   javac -d . src/*.java
   ```

5. Run the application
   ```
   java App
   ```

### Option 2: Running the JAR file (if available)

1. Ensure you have JRE 8 or higher installed
   ```
   java -version
   ```

2. Download the `GabayApp.jar` file

3. Run the JAR file
   ```
   java -jar GabayApp.jar
   ```

## Troubleshooting

### Common Issues

#### Missing Font Files
The application uses the Inter font family. If the fonts are missing:

1. Create a `lib/fonts` directory in the root of the project
2. Download the Inter font family (specifically Inter_18pt variations)
3. Place the font files in the `lib/fonts` directory

#### Missing Image Files
If images aren't displaying properly:

1. Ensure the `resources/images` directory exists with all required images
2. Check console output for specific missing files
3. Check file permissions to ensure Java can read the image files

#### Window Sizing Issues
If the UI elements appear misaligned or cut off:

1. Resize the window to at least 1024x768 resolution
2. Try maximizing the window for the best experience

## Usage Guide

### Landing Page
The landing page displays four main options:
- **Search Candidate**: Find candidates by specific issue keywords
- **Compare Candidates**: Compare platforms and positions between candidates
- **Candidates Overview**: View a comprehensive list of all candidates
- **Gabáy Quiz Match**: Take a quiz to find candidates that match your views

### Candidate Search
1. Click the "Search Candidate" button from the landing page
2. Type keywords into the search box to find specific issue positions
3. Use the filters to narrow down results by region or other criteria
4. Click on a candidate card for more detailed information

### Admin Page
1. Click the "Admin" button in the top-right corner of the landing page
2. Log in with the appropriate credentials (contact administrator)
3. Access administrative functions for managing candidate data

## Development

### Technology Stack
- Java SE 8+
- Java Swing for UI
- Custom animation framework

### Building from Source
To build the application from source:

```
javac -d build/ src/*.java
```

This will compile all Java files and place the resulting class files in the `build` directory.

### Project Structure
- `LandingPage.java`: Main UI with buttons to navigate to different features
- `CandidateSearch.java`: Interface for searching candidate information
- `AdminLogin.java`: Administrative login interface for authentication
- Supporting UI components for filters, cards, and other elements

## License

All Rights Reserved, 2025

## Contact & Support

For questions, issues, or support, please contact [your contact information here].

---

*Note: Gabáy is a platform designed to help voters make informed decisions by providing accessible and user-friendly information about political candidates.*
