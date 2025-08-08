@echo off
setlocal enabledelayedexpansion

:: === CONFIGURATION ===
set JAR_PATH=build\libs\capyverse_cli-1.0.0.jar
set OUTPUT_NAME=capyverse
set GRAALVM_BIN=C:\Users\amank\dev_mode\graalvm_21\bin
set VS_DEV_CMD="C:\Program Files\Microsoft Visual Studio\2022\Professional\VC\Auxiliary\Build\vcvars64.bat"

:: === VALIDATION ===
if not exist "%JAR_PATH%" (
    echo [ERROR] JAR file not found at %JAR_PATH%
    exit /b 1
)

if not exist "%GRAALVM_BIN%\native-image.cmd" (
    echo [ERROR] native-image.cmd not found at %GRAALVM_BIN%
    exit /b 1
)

:: === SETUP ENVIRONMENT ===
echo [INFO] Setting up Visual Studio build environment...
call %VS_DEV_CMD%

echo [INFO] Using GraalVM native-image from: %GRAALVM_BIN%
set PATH=%GRAALVM_BIN%;%PATH%

:: === BUILD ===
echo [INFO] Starting native-image build...
call native-image.cmd -jar %JAR_PATH% -H:-CheckToolchain --no-fallback --verbose -H:WindowsIconPath=installer/app-icon.ico

if %ERRORLEVEL% neq 0 (
    echo [ERROR] Native image build failed.
    exit /b %ERRORLEVEL%
)

echo [SUCCESS] Native image built: %OUTPUT_NAME%.exe
endlocal
