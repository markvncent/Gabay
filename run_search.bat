@echo off
REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Compile all source files
javac -d bin src/frontend/search/*.java src/backend/model/*.java src/frontend/landingpage/*.java src/util/*.java

REM Run the application
java -cp bin frontend.search.CandidateSearchUI 