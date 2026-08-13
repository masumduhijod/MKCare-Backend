@echo off
title Report Service (Port 8089)
color 0B
echo Starting Report Service with -Xmx256m Memory Limit...
cd /d "%~dp0"
java -Xmx256m -jar target\report-service-1.0.jar
pause
