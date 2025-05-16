@echo off
REM go to this directory
cd /d "%~dp0"
REM Check if Java is installed
REM Create bin directory if it doesn't exist
if not exist bin mkdir bin
REM Compile all required files
echo Compiling application files...
javac -source 1.8 -target 1.8 -d bin src/frontend/search/*.java src/frontend/landingpage/*.java src/frontend/admin/*.java src/frontend/comparison/*.java src/frontend/overview/*.java src/frontend/quiz/*.java src/frontend/utils/*.java src/backend/model/*.java src/util/*.java 2>compile_errors.txt
if %ERRORLEVEL% NEQ 0 (
  echo Compilation failed! See compile_errors.txt for details.
  exit /b %ERRORLEVEL%
)

REM Copy resources to bin directory
echo Copying resources...
xcopy /E /I /Y resources bin\resources

echo Starting the Gabay application...
java -cp bin frontend.utils.SplashScreenLauncher

echo Application closed. 
