@echo off
setlocal enabledelayedexpansion

echo.
echo ================================
echo Git Hooks Setup and Auto-commit
echo ================================
echo.

:: Check if hooks are installed
if exist ".git\hooks\pre-commit" if exist ".git\hooks\pre-push" (
    echo ✅ Git hooks are already installed.
    goto :run_spotless
) else (
    echo ⚠️  Git hooks are not installed. Installing now...
    goto :install_hooks
)

:install_hooks
echo Installing Git hooks...

:: Check if hook files exist
if not exist "hooks\pre-commit" (
    echo ❌ Error: hooks\pre-commit file not found!
    goto :pause_exit
)

if not exist "hooks\pre-push" (
    echo ❌ Error: hooks\pre-push file not found!
    goto :pause_exit
)

:: Create .git\hooks directory if it doesn't exist
if not exist ".git\hooks" mkdir ".git\hooks"

:: Copy hooks
copy "hooks\pre-commit" ".git\hooks\pre-commit" >nul
copy "hooks\pre-push" ".git\hooks\pre-push" >nul

echo ✅ Git hooks installed successfully!
echo.

:run_spotless
echo Running Spotless formatting...

:: Check if gradlew exists
if not exist "gradlew.bat" (
    echo ❌ Error: gradlew.bat not found in current directory!
    goto :pause_exit
)

:: Run Spotless apply
call gradlew.bat spotlessApply
if errorlevel 1 (
    echo ❌ Spotless formatting failed!
    goto :pause_exit
)

echo ✅ Spotless formatting completed successfully!
echo.

:: Check if there are changes
git status --porcelain > temp_status.txt
for /f %%i in ("temp_status.txt") do set size=%%~zi
del temp_status.txt

if %size% EQU 0 (
    echo ✅ No changes detected after Spotless formatting. Repository is clean!
    goto :success_exit
)

echo 📋 Changes detected after Spotless formatting:
git status --short
echo.

:: Add all changes
echo Adding all changes to git...
git add .
echo.

:get_commit_message
echo.
echo 📝 Enter your commit message:
set /p "commit_message="
if "!commit_message!"=="" (
    echo ❌ Commit message cannot be empty!
    goto :get_commit_message
)

:: Commit changes
echo.
echo Committing changes...
git commit -m "!commit_message!"
if errorlevel 1 (
    echo ❌ Failed to commit changes.
    goto :pause_exit
)

echo ✅ Changes committed successfully!
echo.

:: Get current branch
for /f "tokens=*" %%i in ('git branch --show-current') do set current_branch=%%i

:: Push changes
echo Pushing changes to remote repository...
echo Pushing to branch: !current_branch!
git push origin "!current_branch!"
if errorlevel 1 (
    echo ❌ Failed to push changes. Please check your remote configuration.
    goto :pause_exit
)

echo ✅ Changes pushed successfully!
goto :success_exit

:success_exit
echo.
echo 🎉 Script completed successfully!
goto :pause_exit

:pause_exit
echo.
pause
exit /b