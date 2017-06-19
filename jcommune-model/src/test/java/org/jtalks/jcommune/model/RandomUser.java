package org.jtalks.jcommune.model;

import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.qala.datagen.RandomShortApi.*;
import static org.jtalks.common.model.entity.Group.GROUP_NAME_MAX_LENGTH;

public class RandomUser {
    private JCUser user;
    private List<Group> groups = new ArrayList<>();

    public static RandomUser create() {
        RandomUser result = new RandomUser();
        String email = alphanumeric(1, 40) + "@" + english(2, 5) + "." + english(2, 3);
        String username = unicode(1, User.USERNAME_MAX_LENGTH);
        result.user = new JCUser(username, email, unicode(50));
        return result;
    }

    // This is needed commonly for different kinds of searches, so decided not create a special method
    public RandomUser withUsernameOrEmailPrefix(String val) {
        if(bool()) withUsername(val);
        if(!val.equals(user.getUsername()) || bool()) withEmailPrefix(val);
        return this;
    }
    public RandomUser withUsername(String username) {
        user.setUsername(username);
        return this;
    }
    public RandomUser withEmailPrefix(String emailPrefix) {
        user.setEmail(emailPrefix + "@blah.com");
        return this;
    }
    public RandomUser maybeInGroups() {
        for(int i = 0; i < integer(0, 10); i++) groups.add(new Group(unicode(1, GROUP_NAME_MAX_LENGTH)));
        return this;
    }
    public RandomUser inGroups() {
        for(int i = 0; i < integer(1, 10); i++) groups.add(new Group(unicode(1, GROUP_NAME_MAX_LENGTH)));
        return this;
    }
    public JCUser persist() {
        List<User> users = Collections.<User>singletonList(user);
        for(Group next: groups) PersistedObjectsFactory.persist(next);
        for(Group next: groups) {//user is still transient so cannot save it right with the group
            next.setUsers(users);
            next.addToUsers();
        }
        PersistedObjectsFactory.persist(user);
        PersistedObjectsFactory.flushAndClear();
        return user;
    }

    public RandomUser withEmail(String email) {
        user.setEmail(email);
        return this;
    }
}
