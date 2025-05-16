# Gabay: Political Candidate Information Application
![GamayLanding](https://github.com/user-attachments/assets/327294f7-5400-4d89-bb76-fd0083c891ce)
## 🎨 Color Palette

<table>
  <tr>
    <td style="background-color:#2C3881; width:100px; height:100px;"></td>
    <td style="background-color:#E94540; width:100px; height:100px;"></td>
    <td style="background-color:#F8B346; width:100px; height:100px;"></td>
    <td style="background-color:#FFFFFF; width:100px; height:100px; border:1px solid #ccc;"></td>
  </tr>
  <tr align="center">
    <td><strong>#2C3881</strong><br>Deep Indigo</td>
    <td><strong>#E94540</strong><br>Vivid Red</td>
    <td><strong>#F8B346</strong><br>Amber Gold</td>
    <td><strong>#FFFFFF</strong><br>White</td>
  </tr>
</table>


## Overview
Gabay is a Java Swing application designed to help users learn about political candidates, their stances on various issues, and compare candidates to make informed voting decisions.

<h2 align="left">Languages and Tools:</h2>
<p align="left"> <a href="https://www.figma.com/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/figma/figma-icon.svg" alt="figma" width="40" height="40"/> </a> <a href="https://www.java.com" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="java" width="40" height="40"/> </a> </p>

## Features
- View detailed candidate information including personal details, party affiliation, and policy positions
- Administrative panel for adding and managing candidate data
- Social issue stance tracking with visual indicators (Agree, Disagree, Neutral)
- Modern, responsive UI with consistent styling
- Elegant page transitions with fading effects and emblem display
- Streamlined search interface with intuitive controls

## Code Structure
- **src/**: Source code directory
  - **frontend/**: UI components
    - **admin/**: Admin panel components
    - **search/**: Search functionality
    - **comparison/**: Candidate comparison features
    - **overview/**: Candidate overview pages
    - **landingpage/**: Landing page components
    - **utils/**: Utility UI components (transitions, splash screen)
    - **quiz/**: Quiz-related components
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
- Custom transition effects and animations
- First-time launch detection with splash screen

### Styling Approach
- Consistent color palette throughout the application
- Custom-painted components for modern appearance
- Rounded corners, drop shadows, and subtle animations
- Standardized fonts (Inter family) for consistent typography
- Smooth transitions between screens with fade effects

## Recent Updates
- Added intro splash screen with progress bar for first-time application launch
- Implemented elegant page transitions with fading effects and Gabay emblem display
- Streamlined the search interface by removing unnecessary buttons and extending the search box
- Added province filtering in the candidate search interface
- Improved UI responsiveness and visual consistency across all screens
- Enhanced button animations and hover effects

## Running the Application
1. Ensure you have Java 17 or higher installed
2. Run the application using the provided `run_app.bat` file
3. For development, you can compile and run manually:
   ```
   javac -d bin -cp bin src/frontend/utils/*.java src/frontend/landingpage/*.java src/frontend/admin/*.java src/frontend/search/*.java src/frontend/comparison/*.java src/frontend/overview/*.java src/frontend/quiz/*.java src/backend/database/*.java src/backend/models/*.java src/util/*.java
   java -cp bin frontend.utils.SplashScreenLauncher
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
Gabay/
├── bin/                  # Compiled class files
├── lib/                  # Libraries and fonts
│   └── fonts/            # Inter font family
├── resources/            # Application resources
│   └── images/           # Images and icons
│       ├── Buttons Icon/ # Button icons
│       ├── Candidate Search/ # Search-related icons
│       └── HeaderEmblem.png # Logo used in transitions and splash screen
├── src/                  # Source code
│   ├── frontend/         # UI components
│   │   ├── landingpage/  # Landing page components
│   │   ├── admin/        # Admin interface components
│   │   ├── search/       # Search interface components
│   │   ├── utils/        # Utility UI components
│   │   └── quiz/         # Quiz components
│   └── backend/          # Backend logic and data handling
└── run_app.bat           # Batch file to compile and run the application
```

## Setup & Installation

### Option 1: Running from Source

1. Ensure you have JDK 8 or higher installed
   ```
   java -version
   ```

2. Clone or download this repository
   ```
   git clone https://github.com/markvncent/Gabay.git
   ```

3. Navigate to the project directory
   ```
   cd Gabay
   ```

4. Run the application using the batch file
   ```
   run_app.bat
   ```

### Option 2: Manual Compilation

1. Ensure you have JDK 8 or higher installed
2. Compile the source code
   ```
   javac -d bin -cp bin src/frontend/utils/*.java src/frontend/landingpage/*.java src/frontend/admin/*.java src/frontend/search/*.java src/frontend/comparison/*.java src/frontend/overview/*.java src/frontend/quiz/*.java src/backend/database/*.java src/backend/models/*.java src/util/*.java
   ```
3. Run the application
   ```
   java -cp bin frontend.utils.SplashScreenLauncher
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

### First Launch
On first launch, you'll see a splash screen with the Gabay emblem and a progress bar. This screen appears only on the first run of the application.

### Landing Page
The landing page displays four main options:
- **Search Candidate**: Find candidates by specific issue keywords
- **Compare Candidates**: Compare platforms and positions between candidates
- **Candidates Overview**: View a comprehensive list of all candidates
- **Gabáy Quiz Match**: Take a quiz to find candidates that match your views

### Candidate Search
1. Click the "Search Candidate" button from the landing page
2. Type keywords into the search box to find specific issue positions
3. Use the filter dropdown to narrow results by name, party, or issue
4. Use the province dropdown to filter by geographic region
5. Click on a candidate card for more detailed information

### Admin Page
1. Click the "Admin" button in the top-right corner of the landing page
2. Log in with the appropriate credentials (contact administrator)
3. Access administrative functions for managing candidate data

## Development

### Technology Stack
- Java SE 8+
- Java Swing for UI
- Custom animation framework
- Custom transition manager

### Project Structure
- `SplashScreenLauncher.java`: Entry point that shows splash screen on first launch
- `LandingPageUI.java`: Main UI with buttons to navigate to different features
- `CandidateSearchUI.java`: Interface for searching candidate information
- `AdminLoginUI.java`: Administrative login interface for authentication
- `WindowTransitionManager.java`: Handles transitions between screens
- Supporting UI components for filters, cards, and other elements

## License

All Rights Reserved, 2025

## Contact & Support

For questions, issues, or support, please contact the development team.

---

*Note: Gabáy is a platform designed to help voters make informed decisions by providing accessible and user-friendly information about political candidates.*
