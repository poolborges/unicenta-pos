Project: uniCenta oPOS v4.5Beta
Topic:	README installer
Author:	Jack Gerrard
Date: 	1 January 2018


************  Important Notice ******************
This version is 4.5

uniCenta oPOS v4.5 includes bug-fixes.

Please read the unicentaopos_4.5_readme in the Release Notes folder.

Java JRE 8 runtime is required.
The uniCenta oPOS installer now attempts to validate that Java JRE runtime is 
installed In all cases Java JRE MUST be installed and running properly for 
uniCenta oPOS to run.

On 64bit platforms; You may receive an error that javaw.exe cannot be found
when trying to run uniCenta oPOS. This is usually caused by having selected
the Java JRE if located in the :\Program Files(x86)\Java folder hence it
cannot be found in the Windows PATH settings.

Best/Quickest way to overcome is to either:
Select the :\Windows\System32 JRE during uniCenta oPOS installation OR
Copy javaw.exe into the :\Windows\System32 folder

For further information on installing Java refer to www.java.com

MySQL 5.5 or later is required.
Use either the suggested MAMP server or full installation of the MYSQL server
from Oracle http://mysql.com
