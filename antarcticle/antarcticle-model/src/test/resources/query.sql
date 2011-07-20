use p_antarcticle;

INSERT INTO `USER` VALUES(1, 1, 'chris', 'Fuko', 'christoph', 'chff@mail.ru', 123456, 'admin');
INSERT INTO ARTICLE_COLLECTION VALUES(1, 1, 'Collection title', 'Collection description', null);
INSERT INTO ARTICLE VALUES(1, 1, 'This is a fake article! Here should be a article content! This is a fake article! Here should be a article content! This is a fake article! Here should be a article content! This is a fake article! Here should be a article content! This is a fake article! Here should be a article content!', 'Some article title', NOW(), 1, 1, 1);

UPDATE ARTICLE_COLLECTION SET LAST_ARTICLE_ID = 1 WHERE ARTICLE_COLLECTION_ID = 1;

INSERT INTO COMMENT VALUES(1, 1, NOW(), 'The article is not usefull. It did not help me at all!!!', 1, 1);
INSERT INTO COMMENT VALUES(2, 2, NOW(), 'This is a very godd article. It helped me very much! Thanks a lot!', 1, 1);
