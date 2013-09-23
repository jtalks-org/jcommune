package org.jtalks.jcommune.web.tags;

import org.slf4j.Logger;
import org.hibernate.ObjectNotFoundException;
import org.jtalks.jcommune.model.entity.JCUser;
import org.slf4j.LoggerFactory;

public class UsersUtil{

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersUtil.class);

    public static boolean isExist(JCUser user){
        if(user!=null){
            try{
                return (user.getUsername() != null);
            }catch (ObjectNotFoundException ex){
                LOGGER.warn("User does not exist." ,ex);
                return false;
            }
        }
        return false;
    }

}
