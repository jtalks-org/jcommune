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
package org.jtalks.jcommune.plugin.auth.poulpe.dto;

import javax.xml.bind.annotation.*;

/**
 * <p>Java class for user complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="user">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="username" type="{http://www.jtalks.org/namespaces/1.0}string" />
 *         &lt;element name="passwordHash" type="{http://www.w3.org/2001/XMLSchema}string" />
 *         &lt;element name="email" type="{http://www.jtalks.org/namespaces/1.0}string" />
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastName" type="{http://www.jtalks.org/namespaces/1.0}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Mikhail Zaitsev
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "user", namespace = "http://www.jtalks.org/namespaces/1.0", propOrder = {
        "username",
        "passwordHash",
        "email",
        "firstName",
        "lastName"})
@XmlRootElement(name = "user", namespace = "http://www.jtalks.org/namespaces/1.0")
public class User {

    @XmlElement(namespace = "http://www.jtalks.org/namespaces/1.0", required = true)
    private String username;

    @XmlElement(namespace = "http://www.jtalks.org/namespaces/1.0", required = true)
    private String passwordHash;

    @XmlElement(namespace = "http://www.jtalks.org/namespaces/1.0", required = true)
    private String email;

    @XmlElement(namespace = "http://www.jtalks.org/namespaces/1.0")
    private String firstName;

    @XmlElement(namespace = "http://www.jtalks.org/namespaces/1.0")
    private String lastName;

    /**
     * Constructs object of this class
     */
    public User() {
    }

    /**
     * Returns the username
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password hash
     *
     * @return the password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the password hash
     *
     * @param passwordHash the password hash
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Returns the email
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the first name
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name
     *
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name
     *
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
