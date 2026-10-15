@echo off
setlocal
cd /d "%~dp0"
if exist build rmdir /s /q build
mkdir build
for /r src %%f in (*.java) do echo %%f>>build\sources.list
javac -encoding UTF-8 -d build @build\sources.list
if errorlevel 1 exit /b 1
java -cp build PresentationTestRunner
exit /b %errorlevel%
