DELIMITER $$
DROP PROCEDURE IF EXISTS changeCollation$$
-- Stored procedure to convert all tables character set and collations
CREATE PROCEDURE changeCollation(IN character_set VARCHAR(255), IN collation_type VARCHAR(255))
  BEGIN
    DECLARE is_finished INTEGER DEFAULT 0;
    DECLARE tableName varchar(255) DEFAULT "";
    DECLARE t_cursor CURSOR FOR SELECT DISTINCT TABLE_NAME
                                    FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = DATABASE()
                                                                          AND TABLE_TYPE = "BASE TABLE";
    DECLARE CONTINUE HANDLER
    FOR NOT FOUND SET is_finished = 1;
    OPEN t_cursor;

    get_table: LOOP
      FETCH t_cursor INTO tableName;
      IF is_finished = 1 THEN
        LEAVE get_table;
      END IF;
-- Flyway cannot convert schema_version table by self because it uses 'SELECT FOR UPDATE' query inside the lib.
      IF (tableName != '' AND tableName != 'jcommune_schema_version') THEN

        SET @s = CONCAT('ALTER TABLE ', tableName,
                        ' CONVERT TO CHARACTER SET ', character_set, ' COLLATE ', collation_type);
        PREPARE stmt FROM @s;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SET tableName = '';

      END IF;
    END LOOP get_table;
    CLOSE t_cursor;
  END $$
DELIMITER ;

ALTER DATABASE CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci';
SET FOREIGN_KEY_CHECKS = 0;
CALL changeCollation('utf8mb4','utf8mb4_unicode_ci');
SET FOREIGN_KEY_CHECKS = 1;