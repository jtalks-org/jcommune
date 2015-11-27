# Since we store drafts of posts in separate table
# we don't need column STATE in POST table anymore
alter table POST drop column STATE;