@echo off
echo Cleaning up .class files from src directory...

:: Remove all .class files from src directory and its subdirectories
del /S /Q src\*.class

echo Done! 