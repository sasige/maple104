@echo off
set CLASSPATH=.;dist\*;
java -Dwzpath=wz\ tools.wztosql.MonsterDropCreator false
pause