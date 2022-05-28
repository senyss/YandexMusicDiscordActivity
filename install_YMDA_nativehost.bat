@echo off

SET CUR_DIR=%~dp0
REG ADD "HKCU\Software\Google\Chrome\NativeMessagingHosts\com.senyss.ymda" /ve /t REG_SZ /d "%CUR_DIR%manifest.json" /f
echo !!! Make sure that Extension ID you got in your browser is present in manifest.json !!!
PAUSE