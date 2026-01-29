@ECHO OFF
SETLOCAL

REM Teachera - Maven Wrapper (Windows)
REM This wrapper downloads the Maven Wrapper JAR and a Maven distribution if needed.
REM Requires Java to be installed and available on PATH (java.exe).

SET "BASEDIR=%~dp0"
SET "WRAPPER_DIR=%BASEDIR%.mvn\wrapper"
SET "WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar"
SET "WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

IF NOT EXIST "%WRAPPER_DIR%" (
  MKDIR "%WRAPPER_DIR%"
)

IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Downloading Maven Wrapper JAR...
  REM Try PowerShell first
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ProgressPreference='SilentlyContinue';" ^
    "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12;" ^
    "try { iwr -UseBasicParsing -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%' } catch { exit 1 }"

  REM Fallback to curl if PowerShell fails (Windows 10+ usually has curl.exe)
  IF ERRORLEVEL 1 (
    ECHO PowerShell download failed. Trying curl...
    curl.exe -fL "%WRAPPER_URL%" -o "%WRAPPER_JAR%"
  )

  REM Fallback to certutil if curl fails
  IF ERRORLEVEL 1 (
    ECHO curl download failed. Trying certutil...
    certutil -urlcache -split -f "%WRAPPER_URL%" "%WRAPPER_JAR%" >NUL
  )

  IF NOT EXIST "%WRAPPER_JAR%" (
    ECHO Failed to download Maven Wrapper JAR from %WRAPPER_URL%
    ECHO Please download it manually and save it to:
    ECHO   %WRAPPER_JAR%
    EXIT /B 1
  )
)

REM Run Maven Wrapper
java -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
EXIT /B %ERRORLEVEL%

