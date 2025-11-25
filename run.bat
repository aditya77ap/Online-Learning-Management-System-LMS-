 @echo off
echo Compiling Project...
if not exist "bin" mkdir bin
javac -d bin -sourcepath src -cp "lib/*" src/com/lms/main/Main.java src/com/lms/util/*.java src/com/lms/model/*.java src/com/lms/dao/*.java src/com/lms/ui/*.java

if %errorlevel% neq 0 (
    echo Compilation Failed!
    pause
    exit /b %errorlevel%
)

echo Running LMS Application...
java -cp "bin;lib/*" com.lms.main.Main
pause
