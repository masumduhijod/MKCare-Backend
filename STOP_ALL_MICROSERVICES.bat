@echo off
title Stop All HMIS Microservices
color 0C
echo ===================================================
echo   Stopping All Running HMIS Microservices...
echo ===================================================

taskkill /F /IM java.exe /T

echo ===================================================
echo   All Microservices Stopped Successfully!
echo ===================================================
pause
