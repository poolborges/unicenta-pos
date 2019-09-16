
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