--    KrOS POS - Open Source Point Of Sale
--    Copyright (c) 2009-2017 uniCenta
--    
--    This program is free software: you can redistribute it and/or modify
--    it under the terms of the GNU General Public License as published by
--    the Free Software Foundation, either version 3 of the License, or
--    (at your option) any later version.
--
--    This program is distributed in the hope that it will be useful,
--    but WITHOUT ANY WARRANTY; without even the implied warranty of
--    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--    GNU General Public License for more details.
--
--    You should have received a copy of the GNU General Public License
--    along with this program.  If not, see <http://www.gnu.org/licenses/>.

-- Database upgrade script for MySQL
-- v4.4.1 - v4.4.2 16SEPT2017

--
-- CLEAR THE DECKS
--
DELETE FROM sharedtickets;

-- UPDATE App' version
-- Rev: JG 16SEPT2017
DELETE FROM roles WHERE id = '0';
INSERT INTO roles(id, name, permissions) VALUES('0', 'Administrator role', $FILE{/com/openbravo/pos/templates/Role.Administrator.xml} );
DELETE FROM resources WHERE name = 'Menu.Root';
INSERT INTO resources(id, name, restype, content) VALUES('0', 'Menu.Root', 0, $FILE{/com/openbravo/pos/templates/Menu.Root.txt});
-- END Rev

DELETE FROM resources WHERE name = 'Cash.Close';
INSERT INTO resources(id, name, restype, content) VALUES('79', 'Cash.Close', 0, $FILE{/com/openbravo/pos/templates/Cash.Close.xml});

DELETE FROM resources WHERE name = 'Ticket.Buttons';
INSERT INTO resources(id, name, restype, content) VALUES('80', 'Ticket.Buttons', 0, $FILE{/com/openbravo/pos/templates/Ticket.Buttons.xml});

UPDATE applications SET NAME = $APP_NAME{}, VERSION = $APP_VERSION{} WHERE ID = $APP_ID{};
