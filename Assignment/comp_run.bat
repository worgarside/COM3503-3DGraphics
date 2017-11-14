@echo off

IF %1.==. GOTO No1

javac %~n1.java

java %~n1

GOTO End1

:No1
  ECHO No parameter given
GOTO End1

:End1