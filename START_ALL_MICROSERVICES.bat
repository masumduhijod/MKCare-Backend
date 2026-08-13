@echo off
title HMIS Microservices Starter
color 0A
echo ===================================================
echo   Starting HMIS Microservices (Memory Optimized)
echo ===================================================

cd /d "%~dp0"

echo [1/10] Starting Eureka Server (Port 8761)...
start "Eureka Server" java -Xmx256m -jar EurekaServerApplication\target\eureka-1.0.jar
timeout /t 10 /nobreak > nul

echo [2/10] Starting API Gateway (Port 8080)...
start "API Gateway" java -Xmx256m -jar ApiGatewayApplication\target\getway-1.0.jar
timeout /t 5 /nobreak > nul

echo [3/10] Starting User Service (Port 8081)...
start "User Service" java -Xmx256m -jar UserServiceApplication\target\user-1.0.jar

echo [4/10] Starting Doctor Service (Port 8086)...
start "Doctor Service" java -Xmx256m -jar DoctorServiceApplication\target\doctor-1.0.jar

echo [5/10] Starting Patient Service (Port 8082)...
start "Patient Service" java -Xmx256m -jar PatientServiceApplication\target\patient-1.0.jar

echo [6/10] Starting Appointment Service (Port 8083)...
start "Appointment Service" java -Xmx256m -jar AppointmentServiceApplication\target\appointment-1.0.jar

echo [7/10] Starting Billing Service (Port 8084)...
start "Billing Service" java -Xmx256m -jar BillingService\target\billing-1.0.jar

echo [8/10] Starting OPD Service (Port 8085)...
start "OPD Service" java -Xmx256m -jar OpdServiceApplication\target\opd-1.0.jar

echo [9/10] Starting CVR Service (Port 8088)...
start "CVR Service" java -Xmx256m -jar cvr-service\target\cvr-1.0.jar

echo [10/10] Starting Report Service (Port 8089)...
start "Report Service" java -Xmx256m -jar Report-Service\target\report-service-1.0.jar

echo ===================================================
echo   All 10 Microservices Launched Successfully!
echo   Eureka Dashboard: http://localhost:8761
echo ===================================================
pause
