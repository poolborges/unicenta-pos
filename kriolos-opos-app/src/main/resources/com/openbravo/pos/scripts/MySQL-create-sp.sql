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

-- ADD STORED PROCEDURES

-- Example: 
-- call AddTblNotExist(Database(), 'atest');

CREATE PROCEDURE `AddTblNotExist`(
	IN dbName tinytext,
	IN tblName tinytext)
begin
	IF NOT EXISTS (
		SELECT * FROM information_schema.TABLES
		WHERE table_name=tblName
		and table_schema=dbName
		)
	THEN
		set @ddl=CONCAT('CREATE TABLE ',dbName,'.',tblName);
		prepare stmt from @ddl;
		execute stmt;
	END IF;
end


-- Example: 
-- call AddColNotExist(Database(), 'atest', 'b', 'int unsigned not null default 1');

CREATE PROCEDURE `AddColNotExist`(
	IN dbName tinytext,
	IN tblName tinytext,
	IN fldName tinytext,
	IN fldDef text)
begin
	IF NOT EXISTS (
		SELECT * FROM information_schema.COLUMNS
		WHERE column_name=fldName
		and table_name=tblName
		and table_schema=dbName
		)
	THEN
		set @ddl=CONCAT('ALTER TABLE ',dbName,'.',tblName,
			' ADD COLUMN ',fldName,' ',fldDef);
		prepare stmt from @ddl;
		execute stmt;
	END IF;
end