@echo off
title Build All HMIS Microservices
color 0E
echo ===================================================
echo   Cleaning and Building All Microservices...
echo ===================================================

cd /d "%~dp0"
call mvn clean package -DskipTests

echo ===================================================
echo   BUILD COMPLETED SUCCESSFULLY!
echo ===================================================
pause
