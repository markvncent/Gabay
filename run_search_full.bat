@echo off
REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Compile required files (avoiding dependencies on other UI components)
echo Compiling required files...
javac -d bin src/frontend/search/CandidateSearchUI.java src/frontend/search/CandidateCardPanel.java src/frontend/search/CandidateCard.java src/frontend/search/FilterDropdown.java src/frontend/search/ProvinceDropdown.java src/backend/model/CandidateDataLoader.java

echo Starting the application...
java -cp bin frontend.search.CandidateSearchUI

echo Application closed. 