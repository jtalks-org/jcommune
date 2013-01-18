/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.model.utils;

import org.mockejb.jndi.MockContextFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

import static org.mockito.Mockito.spy;
import static org.testng.Assert.assertEquals;


/** @author stanislav bashkirtsev */
public class JndiAwarePropertyPlaceholderConfigurerTest {
    private JndiAwarePropertyPlaceholderConfigurer sut;

    @BeforeMethod
    public void setUp() throws Exception {
        sut = new JndiAwarePropertyPlaceholderConfigurer();
    }

    @Test
    public void shouldLookForPropertyInJndi() throws Exception {
        givenTomcatContextWithProps("placeholder", "jndi");
        assertEquals(sut.resolvePlaceholder("placeholder", null, 0), "jndi");
    }

    /**
     * Puts the property into System vars and Properties (that are read from file) and checks that if property found in
     * JNDI, that it's not looked up in other places thus making JNDI of highest priority.
     *
     * @throws Exception we don't care in tests
     */
    @Test
    public void shouldNotLookInOtherPlacesIfFoundInJndi() throws Exception {
        //because we need to change System var which a baaad thing, we'll need to make sure that this variable is not
        //used by other tests and thus we name it this way.
        String propName = "JndiAwarePropertyPlaceholderConfigurerTest-TestPlaceholder1";
        Properties properties = new Properties();//properties
        properties.put(propName, "properties");//properties
        System.setProperty(propName, "system");//system
        givenTomcatContextWithProps(propName, "jndi");//jndi

        assertEquals(sut.resolvePlaceholder(propName, properties, 0), "jndi");
    }

    /**
     * Makes sure that changes didn't break usual placeholder configurer and it works as previously.
     *
     * @throws Exception we don't care in tests
     */
    @Test
    public void looksInUsualPropertiesIfNotFoundInJndi() throws Exception {
        String propName = "JndiAwarePropertyPlaceholderConfigurerTest-TestPlaceholder2";
        Properties properties = new Properties();//properties
        properties.put(propName, "properties");//properties

        assertEquals(sut.resolvePlaceholder(propName, properties, 0), "properties");
    }

    private void givenTomcatContextWithProps(String placeholder, String value) throws NamingException {
        MockContextFactory.setAsInitial();
        Context tomcatContext = new MockContextFactory().getInitialContext(null);
        tomcatContext.bind(placeholder, value);
        new InitialContext().bind("java:/comp/env", tomcatContext);
    }

}
