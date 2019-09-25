@echo off
REM    uniCenta oPOS Touch Friendly Point of Sales designed for Touch Screen
REM    Copyright (c) 2009-2017 uniCenta & previous Openbravo POS works
REM    http://sourceforge.net/projects/unicentaopos
REM
REM    This file is part of uniCenta oPOS
REM
REM    uniCenta oPOS is free software: you can redistribute it and/or modify
REM    it under the terms of the GNU General Public License as published by
REM    the Free Software Foundation, either version 3 of the License, or
REM    (at your option) any later version.
REM
REM    uniCenta oPOS is distributed in the hope that it will be useful,
REM    but WITHOUT ANY WARRANTY; without even the implied warranty of
REM    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
REM    GNU General Public License for more details.
REM
REM    You should have received a copy of the GNU General Public License
REM    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>
REM
set DIRNAME=%~dp0
set CP="%DIRNAME%unicentaopos.jar"
set CP=%CP%;"%DIRNAME%locales/"

start /B javaw -cp %CP% com.openbravo.pos.config.JFrmConfig