#!/bin/sh
#    uniCenta oPOS Touch Friendly Point of Sale designed for Touch Screen
#    Copyright (C) 2009-2018 uniCenta
#    https://unicenta.com
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
CP=$CP:$DIRNAME/reports/

# Select the library folder
case "`uname -s`" in
    Linux)
    case "`uname -m`" in
    i686) LIBRARYPATH=/lib/Linux/i686-unknown-linux-gnu;;
    ia64) LIBRARYPATH=/lib/Linux/ia64-unknown-linux-gnu;;
    x86_64|amd64) LIBRARYPATH=/lib/Linux/x86_64-unknown-linux-gnu;;
    esac;;
    SunOS)
    case "`uname -m`" in
    sparc32) LIBRARYPATH=/Solaris/sparc-solaris/sparc32-sun-solaris2.8;;
    sparc64) LIBRARYPATH=/Solaris/sparc-solaris/sparc64-sun-solaris2.8;;
    esac;;
Darwin) LIBRARYPATH=/lib/Mac_OS_X;;
CYGWIN*|MINGW32*) LIBRARYPATH=/lib/Windows/i368-mingw32;;
esac

# start uniCenta oPOS
java -cp $CP -Xms512m -Xmx1024m -splash:unicenta_splash_dark.png -Djava.library.path=$DIRNAME$LIBRARYPATH -Ddirname.path=$DIRNAME/ com.openbravo.pos.forms.StartPOS