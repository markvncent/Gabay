@echo off
echo Starting Gabay Application Setup...

:: Create necessary directories if they don't exist
echo Creating necessary directories...
if not exist "bin" mkdir bin
if not exist "resources\data" mkdir resources\data
if not exist "resources\images\candidates" mkdir resources\images\candidates
if not exist "resources\images\Candidate Search" mkdir "resources\images\Candidate Search"
if not exist "resources\images\Buttons Icon" mkdir "resources\images\Buttons Icon"

:: Check if candidates.txt exists in resources/data
if not exist "resources\data\candidates.txt" (
    echo Candidates data file not found. Checking for candidates.txt in root directory...
    if exist "candidates.txt" (
        echo Moving candidates.txt to resources\data...
        copy "candidates.txt" "resources\data\candidates.txt"
    ) else (
        echo Creating empty candidates.txt file...
        echo # Sample Candidates Data File > "resources\data\candidates.txt"
        echo. >> "resources\data\candidates.txt"
        echo Name: John Doe >> "resources\data\candidates.txt"
        echo Positions: Senator >> "resources\data\candidates.txt"
        echo Party Affiliation: Independent >> "resources\data\candidates.txt"
        echo Region: NCR >> "resources\data\candidates.txt"
        echo Age: 45 >> "resources\data\candidates.txt"
        echo Image: resources/images/candidates/john_doe.jpg >> "resources\data\candidates.txt"
        echo. >> "resources\data\candidates.txt"
        echo Name: Jane Smith >> "resources\data\candidates.txt"
        echo Positions: Governor >> "resources\data\candidates.txt"
        echo Party Affiliation: Progressive >> "resources\data\candidates.txt"
        echo Region: Region IV-A >> "resources\data\candidates.txt"
        echo Age: 38 >> "resources\data\candidates.txt"
        echo Image: resources/images/candidates/jane_smith.jpg >> "resources\data\candidates.txt"
    )
)

:: Clean bin directory
echo Cleaning bin directory...
if exist "bin" rd /s /q "bin"
mkdir bin

:: Create empty placeholder classes for imports to resolve
echo Creating placeholder classes for imports...
mkdir bin\frontend\admin
mkdir bin\frontend\search
mkdir bin\frontend\comparison
mkdir bin\frontend\overview
mkdir bin\frontend\quiz

:: Compile our new classes first
echo Compiling utility classes...
javac -d bin -source 17 -target 17 src/frontend/admin/SocialIssuesPanel.java
javac -d bin -source 17 -target 17 src/frontend/admin/CandidateProfiles.java

:: Then compile dependent classes
echo Compiling admin components...
javac -d bin -sourcepath src -source 17 -target 17 src/frontend/admin/ProfileListPanel.java
javac -d bin -sourcepath src -source 17 -target 17 src/frontend/admin/CandidateDetailsPanel.java
javac -d bin -sourcepath src -source 17 -target 17 src/frontend/admin/CandidateDirectoryPanel.java
javac -d bin -sourcepath src -source 17 -target 17 src/frontend/admin/AdminPanelUI.java
javac -d bin -sourcepath src -source 17 -target 17 src/frontend/admin/AdminLoginUI.java

:: Create empty placeholder classes for missing components
echo Creating necessary placeholder classes...
type nul > bin\frontend\search\CandidateSearchUI.class
type nul > bin\frontend\comparison\CandidateComparisonUI.class
type nul > bin\frontend\overview\CandidateOverviewUI.class
type nul > bin\frontend\quiz\CandidateQuizUI.class

:: Compile LandingPageUI and App
echo Compiling main application...
javac -d bin -sourcepath src -source 17 -target 17 src/frontend/landingpage/LandingPageUI.java
javac -d bin -sourcepath src -source 17 -target 17 src/App.java

IF %ERRORLEVEL% NEQ 0 (
  echo Compilation failed!
  pause
  exit /b %ERRORLEVEL%
)

echo Running Gabay Application...
java -cp "bin;lib/flatlaf-3.4.jar;lib/flatlaf-extras-3.4.jar;lib/swing-toast-notifications-1.0.1.jar" App

:: Wait for user input before closing
echo.
echo Press any key to exit...
pause > nul 