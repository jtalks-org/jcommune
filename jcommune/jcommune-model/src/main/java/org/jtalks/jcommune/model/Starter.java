package org.jtalks.jcommune.model;

import org.jtalks.jcommune.model.entity.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Temdegon
 */
public class Starter {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("/org/jtalks/jcommune/model/entity/applicationContext-dao.xml");

        
    }
}
