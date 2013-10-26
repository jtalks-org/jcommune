-- added column for last read post date for the topic (it will be used instead of the last read post index)
ALTER TABLE LAST_READ_POSTS ADD(LAST_READ_POST_DATE DATETIME not null);

-- calculating of the last read post date based on the last read post index
UPDATE LAST_READ_POSTS
SET LAST_READ_POST_DATE =
(  SELECT POST_DATE
   FROM POST P1
   WHERE P1.TOPIC_ID=LAST_READ_POSTS.TOPIC_ID AND
         -- since we don't have indexes in the POST table, we need to use [count of the posts created earlier in this TOPIC]
         -- as an index for the POST
         LAST_READ_POST_INDEX = (SELECT count(1)
                                 FROM POST P2
                                 WHERE P2.TOPIC_ID = P1.TOPIC_ID and P2.POST_DATE < P1.POST_DATE
                                )
);

-- temporary set "not null" for LAST_READ_POST_INDEX (it should be deleted later)
ALTER TABLE LAST_READ_POSTS CHANGE COLUMN `LAST_READ_POST_INDEX` `LAST_READ_POST_INDEX` INT(11) NULL;

-- change COUNT_POSTS_TOPICS_VIEW view to work with the date instead of the post indexes
DROP VIEW IF EXIST COUNT_POSTS_TOPICS_VIEW;
CREATE VIEW COUNT_POSTS_TOPICS_VIEW AS
SELECT
  tp.TOPIC_ID, tp.BRANCH_ID, MAX(POST_DATE) as LAST_POST_DATE
FROM
    TOPIC tp
    join POST p ON p.TOPIC_ID=tp.TOPIC_ID
group by tp.TOPIC_ID;