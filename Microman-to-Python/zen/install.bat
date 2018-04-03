@echo off
SETLOCAL
SET DEST="%UserProfile%\Documents\Carl Zeiss\ZEN\Documents\Macros\microman"
mkdir %DEST%
cp ..\ext\microman\x64\Release\microman.dll %DEST%
cp macro\header.xml %DEST%\..\microman.czmac
cat macro\microman.py >> %DEST%\..\microman.czmac
cat macro\footer.xml >> %DEST%\..\microman.czmac
xcopy /r /y ..\py\*.py %DEST%
cp macro\test.jpg >> %DEST%
ENDLOCAL
