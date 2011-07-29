use jtalks;

insert into user values(1,1,'chris', 'Fuko','christoph','chff@mail.ru',123456,'admin');
insert into article_collection values(1,1,'Collection title','Collection description',null);
insert into article values(1,1,'Some Article Content','Some article title',now(),1,1,1);
update ARTICLE_COLLECTION set LAST_ARTICLE_ID=1 where ARTICLE_COLLECTION_ID=1;
insert into comment values(1,1,now(),'some coment content',1);
