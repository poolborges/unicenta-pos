@echo off
set DIRNAME=%~dp0
set CP="%DIRNAME%unicentaopos.jar"
start /B javaw -cp %CP% com.unicenta.orderpop.OrderPop