@echo off
title Dump_String Console
set CLASSPATH=.;dist\*
java -Dwzpath=wz\ tools.WZStringDumper Strings
pause
