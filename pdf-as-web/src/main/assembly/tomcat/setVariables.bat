@echo off

set SERVICE_NAME=tomcatpdfas
set SERVICE_DESCRIPTION=ports: shutdown=8005, http=8080
set SERVICE_DISPLAY_NAME=Apache Tomcat - PDF-AS-WEB
set TITLE=%SERVICE_DISPLAY_NAME%

rem Set Tomcat installation folder, otherwise automatic detection will be performed.
set TOMCAT_DIR=%CD%

rem Set Java installation folder, otherwise default JRE/JDK will be used.
rem set JAVA_HOME=

@echo off
IF "%JAVA_HOME%" == "" (
    echo Enter path to JAVA_HOME: 
    set /p JAVA_HOME=
) ELSE (
    echo %JAVA_HOME%
)

rem *** do not change settings beyond this point ***

if exist %TOMCAT_DIR%\webapps\nul goto START
goto FIND_TOMCAT_DIR

:FIND_TOMCAT_DIR
set TOMCAT_DIR=
if exist webapps\nul set TOMCAT_DIR=%CD%
if exist ..\webapps\nul set TOMCAT_DIR=%CD%\..
if "%TOMCAT_DIR%"=="" goto TOMCAT_DIR_NOT_FOUND
goto START

:TOMCAT_DIR_NOT_FOUND
echo.
echo Unable to find Tomcat installation folder.
goto END

:START
set CATALINA_HOME=%TOMCAT_DIR%
set CATALINA_BASE=%TOMCAT_DIR%
set PDFAS_WORK_DIR=%CATALINA_BASE%\conf\pdf-as\pdf-as-web.properties

rem PARAMETERS
set PDFAS_WORK_DIR_PARAM=-Dpdf-as-web.conf="%PDFAS_WORK_DIR%"

rem MEMORY SETTINGS

rem Thread stack size in KB
set JVM_XSS=1024

rem Initial memory pool size in MB
set JVM_XMS=128

rem Maximum memory pool size in MB
set JVM_XMX=2048

rem Maximum PermGenSize in M
set JVM_MAXPERMSIZE=256

rem COMMAND LINE OPTIONS
set JAVA_OPTS=-server -Xss%JVM_XSS%k -Xms%JVM_XMS%m -Xmx%JVM_XMX%m -XX:MaxPermSize=%JVM_MAXPERMSIZE%m
set CATALINA_OPTS=%PDFAS_WORK_DIR_PARAM%

rem SERVICE OPTIONS
set SERVICE_OPTS=%PDFAS_WORK_DIR_PARAM%;-XX:MaxPermSize=%JVM_MAXPERMSIZE%m

:END
