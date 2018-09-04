@echo off
set REP_PATH=https://github.com/danthe1st/Conways-Game-of-Life.git
::cd GIT

git remote add origin %REP_PATH%
git add .

git pull origin
git remote remove origin
pause>nul