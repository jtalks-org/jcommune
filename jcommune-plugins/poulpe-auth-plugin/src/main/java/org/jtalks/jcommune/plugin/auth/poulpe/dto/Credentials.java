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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for credentials complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="credentials">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="passwordHash" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guram Savinov
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "credentials", namespace = "http://www.jtalks.org/namespaces/1.0", propOrder = {
        "username",
        "passwordHash"})
public class Credentials {

    @XmlElement(namespace = "http://www.jtalks.org/namespaces/1.0", required = true)
    protected String username;
    @XmlElement(namespace = "http://www.jtalks.org/namespaces/1.0")
    protected String passwordHash;

    /**
     * Creates an {@code Credentials} instance.
     */
    public Credentials() {
    }

    /**
     * Creates an {@code Credentials} instance with specified username.
     *
     * @param username the username
     */
    public Credentials(String username) {
        this.username = username;
    }

    /**
     * Gets the value of the username property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the passwordHash property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the value of the passwordHash property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPasswordHash(String value) {
        this.passwordHash = value;
    }

}
