@echo off
chcp 65001 >nul
java -jar "%~dp0CalculadoraImpuestosImportacion.jar"
if errorlevel 1 (
  echo.
  echo No se pudo iniciar la aplicación. Verifique que Java 11 o superior esté instalado.
  pause
)
