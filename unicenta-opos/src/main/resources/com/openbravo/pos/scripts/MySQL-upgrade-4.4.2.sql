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
-- v4.4.2 - v4.5 23SEPT2017

--
-- CLEAR THE DECKS
--
DELETE FROM sharedtickets;

-- UPDATE App' version
UPDATE applications SET NAME = $APP_NAME{}, VERSION = $APP_VERSION{} WHERE ID = $APP_ID{};

-- Customer Events
INSERT INTO resources(ID, name, restype, CONTENT) VALUES('80', 'Customer.Created', 0, $FILE{/com/openbravo/pos/templates/customer.created.xml});
INSERT INTO resources(ID, name, restype, CONTENT) VALUES('81', 'Customer.Updated', 0, $FILE{/com/openbravo/pos/templates/customer.updated.xml});
INSERT INTO resources(ID, name, restype, CONTENT) VALUES('82', 'Customer.Deleted', 0, $FILE{/com/openbravo/pos/templates/customer.deleted.xml});

-- Application Events
INSERT INTO resources(ID, name, restype, CONTENT) VALUES('83', 'Application.Started', 0, $FILE{/com/openbravo/pos/templates/application.started.xml});
