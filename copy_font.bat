@echo off
REM Create fonts directory if it doesn't exist
if not exist "lib\fonts" mkdir lib\fonts

echo Looking for Inter_18pt-Black.ttf in current directory...

REM Check if the font file exists in the current directory
if exist "Inter_18pt-Black.ttf" (
    echo Found Inter_18pt-Black.ttf! Copying to lib\fonts directory...
    copy "Inter_18pt-Black.ttf" "lib\fonts\Inter_18pt-Black.ttf"
    echo Font copied successfully.
) else (
    echo Inter_18pt-Black.ttf not found in current directory.
    echo Please place the Inter_18pt-Black.ttf file in the project root directory
    echo and run this script again, or manually copy it to lib\fonts\Inter_18pt-Black.ttf
)

REM Also check for any .ttf files in current directory
echo.
echo Other font files found in current directory:
for %%f in (*.ttf) do (
    echo - %%f
    echo   Would you like to copy this file to lib\fonts\Inter_18pt-Black.ttf? (Y/N)
    choice /c YN /m "Copy %%f to lib\fonts\Inter_18pt-Black.ttf"
    if errorlevel 2 (
        echo Skipped %%f
    ) else (
        echo Copying %%f to lib\fonts\Inter_18pt-Black.ttf...
        copy "%%f" "lib\fonts\Inter_18pt-Black.ttf"
        echo Copied successfully!
        goto :end
    )
)

:end
echo.
echo IMPORTANT: After copying the font file, please restart your application
echo to make sure the new font is loaded. 