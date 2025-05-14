@echo off
echo Building Gabay UI...

:: Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Find all .java files and compile them together
del sources.txt 2>nul
for /r src %%f in (*.java) do @echo %%f >> sources.txt

javac -d bin -cp "lib/*" @sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    del sources.txt
    exit /b %ERRORLEVEL%
)

del sources.txt

echo Build successful!
echo To run the application, use: run.bat 