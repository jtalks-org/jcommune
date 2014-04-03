UPDATE `contact_type` SET `VALIDATION_PATTERN`='^(id[\d]+)|([\w]+[_.]{0,}[\w]+[\d]{0,})$', `MASK`='id123456 or latin letters, numbers, "." or "_"' WHERE  `TYPE_ID`=9;
