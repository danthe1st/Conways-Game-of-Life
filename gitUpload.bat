@echo off
set REP_PATH=https://github.com/danthe1st/Conways-Game-of-Life.git

:: git remote add origin %REP_PATH%
git add .

echo enter Commit name:
set /P var=
if "x%var%"=="x" (
	::empty
	set var=%TIME%
)

git commit -m "%var%"
git push -u origin --all -f
:: git remote remove origin
pause>nul