@echo off
@title Dump
set CLASSPATH=.;dist\*
ECHO.
ECHO --------------------------------------------------------------
ECHO                 1 - 加载怪物技能
ECHO                 2 - 更新怪物技能
ECHO                 3 - 退出
ECHO --------------------------------------------------------------
ECHO.
SET /P Ares=请选择:
IF %Ares%==1 GOTO ALL
IF %Ares%==2 GOTO UPDATA
IF %Ares%==3 GOTO QUIT

:ALL
ECHO 开始加载怪物技能
java -Dwzpath=wz\ tools.wztosql.DumpMobSkills
pause
exit

:UPDATA
ECHO 开始更新怪物技能
java -Dwzpath=wz\ tools.wztosql.DumpMobSkills -update
pause
exit

:QUIT
exit