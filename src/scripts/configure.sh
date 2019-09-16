#!/bin/sh
#    uniCenta oPOS Touch Friendly Point of Sale designed for Touch Screen
#    Copyright (c) 2009-2017 uniCenta & previous Openbravo POS works
#    http://sourceforge.net/projects/unicentaopos
#
#    This file is part of uniCenta oPOS.
#
#    uniCenta oPOS is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    uniCenta oPOS is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

DIRNAME=`dirname $0`
CP=$DIRNAME/unicentaopos.jar
CP=$CP:$DIRNAME/locales/

java -cp $CP -splash:unicenta_splash_dark.png com.openbravo.pos.config.JFrmConfig
