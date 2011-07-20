use p_antarcticle;

insert into user values(1,1,'chris', 'Fuko','christoph','chff@mail.ru',123456,'admin');
insert into article_collection values(1,1,'Collection title','Collection description',null);
insert into article values(1,1,'This is a fake article! Here should be a article content! This is a fake article! Here should be a article content! This is a fake article! Here should be a article content! This is a fake article! Here should be a article content! This is a fake article! Here should be a article content! ','Some article title',now(),1,1,1);
update ARTICLE_COLLECTION set LAST_ARTICLE_ID=1 where ARTICLE_COLLECTION_ID=1;
insert into comment values(1,1,now(),'The article is not usefull. It did not help me at all!!!',1,1);
insert into comment values(2,2,now(),'This is a very godd article. It helped me very much! Thanks a lot!',1,1);
