@echo off
@title Dump
set CLASSPATH=.;dist\*
ECHO.
ECHO --------------------------------------------------------------
ECHO                 1 - 加载道具信息
ECHO                 2 - 更新道具信息
ECHO                 3 - 退出
ECHO --------------------------------------------------------------
ECHO.
SET /P Ares=请选择:
IF %Ares%==1 GOTO ALL
IF %Ares%==2 GOTO UPDATA
IF %Ares%==3 GOTO QUIT

:ALL
ECHO 开始加载道具信息
java -Xms256m -Xmx512m -Dwzpath=wz\ tools.wztosql.DumpItems
pause
exit

:UPDATA
ECHO 开始更新道具信息
java -Xms256m -Xmx512m -Dwzpath=wz\ tools.wztosql.DumpItems -update
pause
exit

:QUIT
exit