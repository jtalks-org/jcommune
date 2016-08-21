-- Remove duplicates from LAST_READ_POSTS table
DELETE FROM LAST_READ_POSTS
WHERE ID IN (
  SELECT ID FROM (
                   SELECT *
                   FROM LAST_READ_POSTS
                   GROUP BY TOPIC_ID, USER_ID
                   HAVING COUNT(*) > 1
                 ) as tmp
);

ALTER TABLE LAST_READ_POSTS ADD UNIQUE INDEX `UNIQUE_TOPIC_ID_USER_ID` (TOPIC_ID, USER_ID);