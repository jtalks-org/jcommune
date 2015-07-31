ALTER TABLE CONTACT_TYPE 
    ADD MASK VARCHAR(50) NOT NULL DEFAULT '',
    ADD DISPLAY_PATTERN VARCHAR(255) NOT NULL DEFAULT '%s',
    ADD VALIDATION_PATTERN VARCHAR(255) NOT NULL DEFAULT '.+';

UPDATE CONTACT_TYPE SET MASK='000111222', VALIDATION_PATTERN='^([0-9]){5,9}$' WHERE TYPE_ID = 1;
UPDATE CONTACT_TYPE SET MASK='skypename', DISPLAY_PATTERN='<a href="skype://%s">%s</a>', VALIDATION_PATTERN='^[-\\.\\w]{6,30}$' WHERE TYPE_ID = 2;
UPDATE CONTACT_TYPE SET MASK='mail@server.com', DISPLAY_PATTERN='<a href="mailto:%s">%s</a>', VALIDATION_PATTERN='^([a-z0-9_\\.-]+)@([\\da-z\\._-]+)\\.([a-z]{2,6})$' WHERE TYPE_ID = 3;
UPDATE CONTACT_TYPE SET MASK='+38.0672345678', VALIDATION_PATTERN='^\\+[0-9]{1,3}\\.[0-9]{4,14}(?:x.+)?$' WHERE TYPE_ID = 4;
UPDATE CONTACT_TYPE SET MASK='vanya@gmail.com', VALIDATION_PATTERN='^(?:([^@/<>\'\"]+)@)?([^@/<>\'\"]+)(?:/([^<>\'\"]*))?$' WHERE TYPE_ID = 5;
UPDATE CONTACT_TYPE SET MASK='facebook.name', DISPLAY_PATTERN='<a href="http://facebook.com/%s">%s</a>', VALIDATION_PATTERN='^[A-Za-z\\d.]{5,}$' WHERE TYPE_ID = 6;
UPDATE CONTACT_TYPE SET MASK='twitter_name', DISPLAY_PATTERN='<a href="http://twitter.com/#!/%s">%s</a>', VALIDATION_PATTERN='^[\\w]+$' WHERE TYPE_ID = 7;
UPDATE CONTACT_TYPE SET MASK='mail@gmail.com', VALIDATION_PATTERN='^([a-z0-9_\\.-]+)@gmail.com$' WHERE TYPE_ID = 8;
UPDATE CONTACT_TYPE SET MASK='id123456', DISPLAY_PATTERN='<a href="http://vk.com/%s">%s</a>', VALIDATION_PATTERN='^id[\\d]+$' WHERE TYPE_ID = 9;
UPDATE CONTACT_TYPE SET MASK='12423632', DISPLAY_PATTERN='<a href="http://www.odnoklassniki.ru/profile/%s">http://www.odnoklassniki.ru/profile/%s</a>', VALIDATION_PATTERN='^[\\d]+$' WHERE TYPE_ID = 10;
UPDATE CONTACT_TYPE SET MASK='user-name.livejournal.com', DISPLAY_PATTERN='<a href="http://%s">%s</a>', VALIDATION_PATTERN='^[A-Za-z0-9-]+\\.livejournal\\.com|\\.жж\\.рф' WHERE TYPE_ID = 11;
UPDATE CONTACT_TYPE SET MASK='user-name.tumblr.com', DISPLAY_PATTERN='<a href="http://%s">%s</a>', VALIDATION_PATTERN='^[A-Za-z0-9-]+\\.tumblr\\.com' WHERE TYPE_ID = 12;
UPDATE CONTACT_TYPE SET MASK='user-name.blogspot.com', DISPLAY_PATTERN='<a href="http://%s">%s</a>', VALIDATION_PATTERN='^[A-Za-z0-9-]+\\.blogspot\\.com' WHERE TYPE_ID = 13;
UPDATE CONTACT_TYPE SET MASK='aim@server.com', VALIDATION_PATTERN='^([a-z0-9_\\.-]+)@([\\da-z\\._-]+)\\.([a-z]{2,6})$' WHERE TYPE_ID = 14;
UPDATE CONTACT_TYPE SET MASK='yim@server.com', VALIDATION_PATTERN='^([a-z0-9_\\.-]+)@([\\da-z\\._-]+)\\.([a-z]{2,6})$' WHERE TYPE_ID = 15;
UPDATE CONTACT_TYPE SET MASK='msn@server.com', VALIDATION_PATTERN='^([a-z0-9_\\.-]+)@([\\da-z\\._-]+)\\.([a-z]{2,6})$' WHERE TYPE_ID = 16;
UPDATE CONTACT_TYPE SET MASK='http://site-name.domain.com', DISPLAY_PATTERN='<a href="%s">%s</a>', VALIDATION_PATTERN='^(https?:\\/\\/)([\\da-z\\.-]+)\\.([a-z]{2,6})([\\/\\w \\.-]*)*\\/?$' WHERE TYPE_ID = 17;
