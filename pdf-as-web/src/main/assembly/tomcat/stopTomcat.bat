@echo off
call setVariables.bat
echo.
cd %CATALINA_HOME%
bin\catalina.bat stop
