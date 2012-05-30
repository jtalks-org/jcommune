UPDATE USERS users
JOIN (
	SELECT 
		ID, MD5(PASSWORD) AS PASSWORD_HASH
	FROM USERS
) users_hashed_password ON users.ID = users_hashed_password.ID
SET users.PASSWORD = users_hashed_password.PASSWORD_HASH;