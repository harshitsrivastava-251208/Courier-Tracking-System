@echo off
echo Starting SwiftCourier Management System...
cd src
javac -cp ".;../lib/mysql-connector-j-9.1.0.jar" *.java
java -cp ".;../lib/mysql-connector-j-9.1.0.jar" LoginPage
pause
