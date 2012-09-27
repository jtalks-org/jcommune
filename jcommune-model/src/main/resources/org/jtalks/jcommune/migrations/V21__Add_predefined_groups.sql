INSERT INTO GROUPS (`UUID`, `NAME`, `DESCRIPTION`)
  SELECT (SELECT UUID() FROM dual),'Administrators', 'Administrators group.' FROM dual
    WHERE NOT EXISTS (SELECT gr.GROUP_ID FROM GROUPS gr WHERE gr.NAME='Administrators');

INSERT INTO GROUPS (`UUID`, `NAME`, `DESCRIPTION`)
  SELECT (SELECT UUID() FROM dual),'Registered Users', 'The group for all registered users.' FROM dual
    WHERE NOT EXISTS (SELECT gr.GROUP_ID FROM GROUPS gr WHERE gr.NAME='Registered Users');

INSERT INTO GROUPS (`UUID`, `NAME`, `DESCRIPTION`)
  SELECT (SELECT UUID() FROM dual),'Banned Users', 'The group for banned users.' FROM dual
    WHERE NOT EXISTS (SELECT gr.GROUP_ID FROM GROUPS gr WHERE gr.NAME='Banned Users');
