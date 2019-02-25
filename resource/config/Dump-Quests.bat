@echo off
@title Dump
set CLASSPATH=.;dist\*
ECHO.
ECHO --------------------------------------------------------------
ECHO                 1 - 加载任务信息
ECHO                 2 - 更新任务信息
ECHO                 3 - 退出
ECHO --------------------------------------------------------------
ECHO.
SET /P Ares=请选择:
IF %Ares%==1 GOTO ALL
IF %Ares%==2 GOTO UPDATA
IF %Ares%==3 GOTO QUIT

:ALL
ECHO 开始加载任务信息
java -Xms256m -Xmx512m -Dwzpath=wz\ tools.wztosql.DumpQuests
pause
exit

:UPDATA
ECHO 开始更新任务信息
java -Xms256m -Xmx512m -Dwzpath=wz\ tools.wztosql.DumpQuests -update
pause
exit

:QUIT
exit