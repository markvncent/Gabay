@echo off
REM go to this directory
cd /d "%~dp0"
REM Check if Java is installed
REM Create bin directory if it doesn't exist
if not exist bin mkdir bin
REM Compile all required files
echo Compiling Java files...
javac -d bin -cp bin src/frontend/utils/*.java src/frontend/landingpage/*.java src/frontend/admin/*.java src/frontend/search/*.java src/frontend/comparison/*.java src/frontend/overview/*.java src/frontend/quiz/*.java src/backend/database/*.java src/backend/models/*.java src/util/*.java

REM Copy resources to bin directory
echo Copying resources...
xcopy /E /I /Y resources bin\resources

echo Running application...
java -cp bin frontend.utils.SplashScreenLauncher

echo Application closed. 
