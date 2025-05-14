# Gabay: Political Candidate Information Application

## Overview
Gabay is a Java Swing application designed to help users learn about political candidates, their stances on various issues, and compare candidates to make informed voting decisions.

## Features
- View detailed candidate information including personal details, party affiliation, and policy positions
- Administrative panel for adding and managing candidate data
- Social issue stance tracking with visual indicators (Agree, Disagree, Neutral)
- Modern, responsive UI with consistent styling

## Components
The application follows a modular design pattern with separate components for different aspects of the UI:

### Admin Panel
- **AdminPanelUI**: Main container for the admin interface
- **AdminLoginUI**: Login interface for administrators
- **CandidateDirectoryPanel**: Directory listing all candidates with search functionality
- **CandidateDetailsPanel**: Form for viewing and editing candidate details
- **ProfileListPanel**: Reusable component for displaying lists of candidate profiles
- **SocialIssuesPanel**: Modular component for displaying and editing candidate positions on social issues

### Main Application
- **LandingPage**: Entry point to the application with navigation to main features
- **CandidateSearch**: Interface for searching and filtering candidates
- **CandidateCard**: Card component for displaying candidate summaries
- **ProvinceDropdown**: Dropdown for selecting provinces/regions

## Code Structure
- **src/**: Source code directory
  - **frontend/**: UI components
    - **admin/**: Admin panel components
    - **search/**: Search functionality
    - **comparison/**: Candidate comparison features
    - **overview/**: Candidate overview pages
    - **landingpage/**: Landing page components
  - **backend/**: Data handling and business logic
  - **util/**: Utility classes and helpers
- **resources/**: Resource files
  - **images/**: UI images and icons
  - **data/**: Data files
- **lib/**: External libraries and fonts
  - **fonts/**: Custom fonts (Inter family)

## Technical Implementation
The application uses:
- Java Swing for UI components
- Custom-styled components with modern design
- Modular architecture for reusable components
- FlatLaf for improved Swing look and feel

### Styling Approach
- Consistent color palette throughout the application
- Custom-painted components for modern appearance
- Rounded corners, drop shadows, and subtle animations
- Standardized fonts (Inter family) for consistent typography

## Recent Updates
- Added modular SocialIssuesPanel component for better code organization
- Implemented pagination in CandidateDetailsPanel with basic info and social stances
- Added form validation and user feedback
- Improved component reusability

## Running the Application
1. Ensure you have Java 17 or higher installed
2. Run the application using the provided `run.bat` file
3. For development, you can compile and run manually:
   ```
   javac -d bin -sourcepath src src/App.java
   java -cp "bin;lib/flatlaf-3.4.jar;lib/flatlaf-extras-3.4.jar;lib/swing-toast-notifications-1.0.1.jar" App
   ```

## Development Guidelines
When contributing to the project:
1. Follow the existing component structure and naming conventions
2. Maintain consistent styling (colors, fonts, padding)
3. Create modular, reusable components
4. Document classes and methods with JavaDoc comments

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
