INSERT IGNORE INTO PROPERTIES(UUID, NAME, CMP_ID)
VALUES ((SELECT UUID() FROM dual), 'jcommune.email_domains_black_list', 2)