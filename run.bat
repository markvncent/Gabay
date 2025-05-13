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
        echo WARNING: candidates.txt not found. Default candidates will be used.
    )
)

:: Clean bin directory to ensure clean build
echo Cleaning bin directory...
if exist "bin" (
    del /S /Q bin\*.class > nul 2>&1
) else (
    mkdir bin
)

:: Compile all source files
echo Compiling all Java source files...
javac -d bin -cp "bin;src;lib\fonts;lib\images;." src/util/ResourceHelper.java
javac -d bin -cp "bin;src;lib\fonts;lib\images;." src/backend/model/CandidateDataLoader.java
javac -d bin -cp "bin;src;lib\fonts;lib\images;." src/frontend/search/CandidateCard.java
javac -d bin -cp "bin;src;lib\fonts;lib\images;." src/frontend/search/FilterDropdown.java
javac -d bin -cp "bin;src;lib\fonts;lib\images;." src/frontend/search/ProvinceDropdown.java
javac -d bin -cp "bin;src;lib\fonts;lib\images;." src/frontend/search/CandidateSearchUI.java
javac -d bin -cp "bin;src;lib\fonts;lib\images;." src/frontend/admin/AdminPanelUI.java
javac -d bin -cp "bin;src;lib\fonts;lib\images;." src/frontend/comparison/CandidateComparisonUI.java
javac -d bin -cp "bin;src;lib\fonts;lib\images;." src/frontend/overview/CandidateOverviewUI.java
javac -d bin -cp "bin;src;lib\fonts;lib\images;." src/frontend/quiz/CandidateQuizUI.java
javac -d bin -cp "bin;src;lib\fonts;lib\images;." src/frontend/landingpage/LandingPageUI.java

:: Compile the main App class
echo Compiling main App class...
javac -d bin -cp "bin;src;lib\fonts;lib\images;." src/App.java

:: Run the application using App as the main entry point
echo Running Gabay application...

:: Check if App class exists and use it
if exist "bin\App.class" (
    echo Starting application using App main class...
    java -cp "bin;src;lib\fonts;lib\images;." App
) else (
    echo App.class not found. Using component selection menu...
    echo Choose which UI to start:
    echo 1. Landing Page (Main Application)
    echo 2. Candidate Search
    echo 3. Candidate Comparison
    echo 4. Candidate Overview
    echo 5. Candidate Quiz
    echo 6. Admin Panel

    set /p choice="Enter your choice (1-6): "

    if "%choice%"=="1" (
        java -cp "bin;src;lib\fonts;lib\images;." frontend.landingpage.LandingPageUI
    ) else if "%choice%"=="2" (
        java -cp "bin;src;lib\fonts;lib\images;." frontend.search.CandidateSearchUI
    ) else if "%choice%"=="3" (
        java -cp "bin;src;lib\fonts;lib\images;." frontend.comparison.CandidateComparisonUI
    ) else if "%choice%"=="4" (
        java -cp "bin;src;lib\fonts;lib\images;." frontend.overview.CandidateOverviewUI
    ) else if "%choice%"=="5" (
        java -cp "bin;src;lib\fonts;lib\images;." frontend.quiz.CandidateQuizUI
    ) else if "%choice%"=="6" (
        java -cp "bin;src;lib\fonts;lib\images;." frontend.admin.AdminPanelUI
    ) else (
        echo Invalid choice. Starting Landing Page...
        java -cp "bin;src;lib\fonts;lib\images;." frontend.landingpage.LandingPageUI
    )
)

echo Application closed.
pause 