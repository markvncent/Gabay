@echo off
echo Compiling Java files from src to bin...

:: Create bin directory if it doesn't exist
if not exist bin mkdir bin

:: Remove all .class files from the bin directory to start fresh
del /Q bin\*.class 

:: Compile all Java files in src directory and place .class files in bin
:: Using --release 17 flag for compatibility with Java 17 runtime
javac --release 17 -d bin -cp ".;lib\*" src\*.java

echo Done! 