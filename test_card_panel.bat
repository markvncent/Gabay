@echo off
REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Compile only the required classes
javac -d bin src/frontend/search/TestCardPanel.java src/frontend/search/CandidateCardPanel.java src/frontend/search/CandidateCard.java src/backend/model/CandidateDataLoader.java

REM Run the test application
java -cp bin frontend.search.TestCardPanel 