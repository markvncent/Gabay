@echo off
REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Compile all required files
echo Compiling application files...
javac -d bin src/frontend/search/*.java src/frontend/landingpage/*.java src/frontend/admin/*.java src/backend/model/*.java src/util/*.java 2>compile_errors.txt
if %ERRORLEVEL% NEQ 0 (
  echo Compilation failed! See compile_errors.txt for details.
  exit /b %ERRORLEVEL%
)

echo Starting the Gabay application...
java -cp bin frontend.landingpage.LandingPageUI

echo Application closed. 