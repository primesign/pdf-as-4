@echo off
call setVariables.bat
echo.
cd /d %CATALINA_HOME%
bin\catalina.bat start
