@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0backend"

where javac >nul 2>nul
if errorlevel 1 (
    echo O JDK nao foi encontrado.
    echo Instale o JDK 17 e marque a opcao para adicionar ao PATH.
    pause
    exit /b 1
)

if not exist "lib\mysql-connector-j-9.6.0.jar" (
    echo O driver do MySQL nao foi encontrado na pasta backend\lib.
    pause
    exit /b 1
)

if not exist "config.properties" (
    copy "config.exemplo.properties" "config.properties" >nul
    echo O arquivo config.properties foi criado.
    echo Informe nele a senha do MySQL, salve e abra este arquivo novamente.
    start notepad "config.properties"
    pause
    exit /b 0
)

if not exist "bin" mkdir "bin"

set "FONTES="
for /r "src" %%f in (*.java) do set "FONTES=!FONTES! "%%f""

echo Compilando o backend...
javac -encoding UTF-8 -cp "lib\mysql-connector-j-9.6.0.jar" -d "bin" !FONTES!
if errorlevel 1 (
    echo Nao foi possivel compilar o backend.
    pause
    exit /b 1
)

echo Iniciando a API...
java -cp "bin;lib\mysql-connector-j-9.6.0.jar" Main

if errorlevel 1 (
    echo Nao foi possivel iniciar o backend.
)

pause
