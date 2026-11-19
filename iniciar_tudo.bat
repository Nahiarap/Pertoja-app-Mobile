@echo off
chcp 65001 >nul
echo ========================================================
echo   ⚡ PertoJa - Inicializador Completo (API + App)
echo ========================================================
echo.

set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "ANDROID_SDK=C:\Users\nahia\AppData\Local\Android\Sdk"
set "PATH=%JAVA_HOME%\bin;%ANDROID_SDK%\platform-tools;%ANDROID_SDK%\emulator;%PATH%"

echo [1/2] Iniciando a API REST Spring Boot (pertoja-api) na porta 8080...
start "PertoJa API - Spring Boot" cmd /k "cd /d "%~dp0pertoja-api" && set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr" && .\gradlew.bat bootRun --no-daemon"

echo Aguardando o servidor ligar (8 segundos)...
timeout /t 8 /nobreak >nul

echo [2/2] Abrindo o Simulador Interativo do App + Painel CRUD no Navegador...
start http://localhost:8080

echo Verificando se ha celular Android conectado via USB...
adb devices | findstr /R "device$" >nul
if not errorlevel 1 (
    echo Celular detectado! Instalando e abrindo o app PertoJa...
    adb reverse tcp:8080 tcp:8080
    adb install -r "%~dp0PertoJa\app\build\outputs\apk\debug\app-debug.apk"
    adb shell am start -n com.faculdade.pertoja/.activity.LoginActivity
)

echo.
echo ✅ Tudo pronto! Acesse http://localhost:8080 no navegador!
pause
