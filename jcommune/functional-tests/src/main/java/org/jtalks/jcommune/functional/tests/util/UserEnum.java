package org.jtalks.jcommune.functional.tests.util;


public enum UserEnum {

    MAIN_USER("testuser", "userpass"), ALTERNATIVE_USER("testuser2", "userpass2"),
    INCORRECT_USER("incorrectuser","userpass");

    private String username;
    private String password;

   UserEnum(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
