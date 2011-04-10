package org.jtalks.jcommune.model;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.hibernate.dialect.MySQL5InnoDBDialect;
/**
 *
 * @author Temdegon
 */
public class Starter {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("/org/jtalks/jcommune/model/entity/applicationContext-dao.xml");
    }
}
