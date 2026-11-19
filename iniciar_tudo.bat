@echo off
title PertoJa - Inicializador Completo
color 0B
echo ========================================================
echo   PertoJa - Inicializador Completo (API + App)
echo ========================================================
echo.

set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "ANDROID_SDK=C:\Users\nahia\AppData\Local\Android\Sdk"
set "PATH=%JAVA_HOME%\bin;%ANDROID_SDK%\platform-tools;%ANDROID_SDK%\emulator;%PATH%"

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERRO] Java nao encontrado em: %JAVA_HOME%
    pause
    exit /b 1
)

echo [1/3] Verificando se a porta 8080 ja esta em uso...
powershell -NoProfile -Command "$c = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue; if ($c) { $c | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue } }"

echo [2/3] Iniciando a API REST Spring Boot (pertoja-api) na porta 8080...
start "PertoJa API - Spring Boot" /D "%~dp0pertoja-api" cmd /k "set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr&& call gradlew.bat bootRun --no-daemon"

echo Aguardando o servidor Spring Boot responder em http://localhost:8080 ...
powershell -NoProfile -Command "$ok = $false; for ($i=0; $i -lt 30; $i++) { try { $r = Invoke-WebRequest -Uri 'http://localhost:8080/api/status' -UseBasicParsing -TimeoutSec 2; if ($r.StatusCode -eq 200) { $ok = $true; break } } catch {}; Start-Sleep -Seconds 1 }; if ($ok) { Write-Host '[OK] API Spring Boot ONLINE!' -ForegroundColor Green } else { Write-Host '[AVISO] A API ainda esta iniciando na outra janela.' -ForegroundColor Yellow }"

echo.
echo [3/3] Abrindo o Simulador Web no navegador...
start "" "http://localhost:8080"

echo.
echo Verificando se ha celular Android conectado via USB...
if exist "%ANDROID_SDK%\platform-tools\adb.exe" (
    adb start-server >nul 2>&1
    adb devices | findstr /R "device$" >nul
    if not errorlevel 1 (
        echo Celular detectado via USB! Configurando tunel adb reverse e abrindo app...
        adb reverse tcp:8080 tcp:8080
        if exist "%~dp0PertoJa\app\build\outputs\apk\debug\app-debug.apk" (
            adb install -r "%~dp0PertoJa\app\build\outputs\apk\debug\app-debug.apk"
        )
        adb shell am start -n com.faculdade.pertoja/.activity.LoginActivity
    ) else (
        echo Nenhum celular conectado via USB neste momento.
    )
) else (
    echo ADB nao encontrado em %ANDROID_SDK%\platform-tools.
)

echo.
echo ========================================================
echo   Tudo pronto!
echo   - Mantenha a janela "PertoJa API - Spring Boot" aberta.
echo   - Navegador: http://localhost:8080
echo ========================================================
pause
