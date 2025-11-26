@echo off
echo Resetting LMS Database...
echo.

REM Try common MySQL installation paths
set MYSQL_PATH=

if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
    set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
) else if exist "C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe" (
    set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe
) else if exist "C:\xampp\mysql\bin\mysql.exe" (
    set MYSQL_PATH=C:\xampp\mysql\bin\mysql.exe
) else (
    echo MySQL not found in common locations.
    echo Please update the database manually using MySQL Workbench or command line.
    echo.
    echo Run these commands:
    echo   DROP DATABASE IF EXISTS lms_db;
    echo   CREATE DATABASE lms_db;
    echo   USE lms_db;
    echo   SOURCE 'c:/Users/adity/OneDrive/Documents/Project LMS/database.sql';
    echo.
    pause
    exit /b 1
)

echo Found MySQL at: %MYSQL_PATH%
echo.

"%MYSQL_PATH%" -u root -ppassword -e "DROP DATABASE IF EXISTS lms_db; CREATE DATABASE lms_db;"
"%MYSQL_PATH%" -u root -ppassword lms_db < "database.sql"

echo.
echo Database reset complete!
echo You can now login with:
echo   Admin: admin@lms.com / admin123
echo   Instructor: instructor@lms.com / instructor123
echo   Student: student@lms.com / student123
echo.
pause
