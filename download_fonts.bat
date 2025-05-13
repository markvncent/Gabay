@echo off
REM Create fonts directory if it doesn't exist
if not exist "lib\fonts" mkdir lib\fonts

echo Downloading Inter fonts from Google Fonts API...

REM Download Inter-Regular font as TTF
powershell -Command "& {$webClient = New-Object System.Net.WebClient; $webClient.DownloadFile('https://fonts.googleapis.com/css2?family=Inter:wght@400&display=swap', 'inter-regular.css')}"

REM Download Inter-Black font as TTF
powershell -Command "& {$webClient = New-Object System.Net.WebClient; $webClient.DownloadFile('https://fonts.googleapis.com/css2?family=Inter:wght@900&display=swap', 'inter-black.css')}"

echo Downloaded CSS files, extracting font URLs...

REM Create a direct download version for reliable font downloads
echo /* Direct font downloads */ > lib\fonts\README.txt
echo. >> lib\fonts\README.txt
echo These fonts were downloaded from Google Fonts for the GabayApp application. >> lib\fonts\README.txt
echo Inter is an open source font designed by Rasmus Andersson. >> lib\fonts\README.txt
echo. >> lib\fonts\README.txt

REM Create a simple HTML file to load and save the fonts correctly
echo ^<!DOCTYPE html^> > save-fonts.html
echo ^<html^> >> save-fonts.html
echo ^<head^> >> save-fonts.html
echo     ^<meta charset="UTF-8"^> >> save-fonts.html
echo     ^<title^>Font Downloader^</title^> >> save-fonts.html
echo     ^<style^> >> save-fonts.html
echo         @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;900&display=swap'); >> save-fonts.html
echo         body { font-family: 'Inter', sans-serif; } >> save-fonts.html
echo         .regular { font-weight: 400; } >> save-fonts.html
echo         .black { font-weight: 900; } >> save-fonts.html
echo     ^</style^> >> save-fonts.html
echo ^</head^> >> save-fonts.html
echo ^<body^> >> save-fonts.html
echo     ^<h1 class="regular"^>Regular Font Test^</h1^> >> save-fonts.html
echo     ^<h1 class="black"^>Black Font Test^</h1^> >> save-fonts.html
echo     ^<p^>Please load this page in a browser. The fonts will be cached by your browser.^</p^> >> save-fonts.html
echo     ^<p^>Then copy these files from your browser's cache to lib\fonts:^</p^> >> save-fonts.html
echo     ^<ul^> >> save-fonts.html
echo         ^<li^>Copy a weight 400 Inter font file to: lib\fonts\Inter-Regular.ttf^</li^> >> save-fonts.html
echo         ^<li^>Copy a weight 900 Inter font file to: lib\fonts\Inter_18pt-Black.ttf^</li^> >> save-fonts.html
echo     ^</ul^> >> save-fonts.html
echo ^</body^> >> save-fonts.html
echo ^</html^> >> save-fonts.html

REM Remove temporary CSS files
del inter-regular.css
del inter-black.css

echo Direct download not possible, manual steps required.
echo Created save-fonts.html file to help with font download.
echo.
echo INSTRUCTIONS:
echo 1. Open the save-fonts.html file in your browser
echo 2. The fonts will be loaded and cached by your browser
echo 3. Find the woff2 font files in your browser's cache:
echo    - Chrome: chrome://net-internals/#httpCache
echo    - Firefox: about:cache
echo    - Edge: edge://net-internals/#httpCache
echo 4. Copy the Inter Regular and Black font files to:
echo    - lib\fonts\Inter-Regular.ttf
echo    - lib\fonts\Inter_18pt-Black.ttf
echo.
echo Alternatively, download Inter fonts directly from:
echo https://fonts.google.com/specimen/Inter 