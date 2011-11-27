@echo off

set ALL_DIR=%cd%
set ALL_MEM=128m
set ALL_MAX_MEM=1024m
set CLASSPATH="%ALL_DIR%"\System\Jar;"%ALL_DIR%"\System\Jar\*;"%ALL_DIR%"\System
set STARTUP_ARGS=-Xmx%ALL_MAX_MEM% -Xms%ALL_MEM% -Djava.library.path="%ALL_DIR%\System\Lib" -Djna.library.path="%ALL_DIR%\System\Lib"
@Rem set MAIN_CLASS=com.all.login.Client
set MAIN_CLASS=com.all.launcher.Main

echo java %STARTUP_ARGS% -cp %CLASSPATH% %MAIN_CLASS%

java %STARTUP_ARGS% -cp %CLASSPATH% %MAIN_CLASS%
