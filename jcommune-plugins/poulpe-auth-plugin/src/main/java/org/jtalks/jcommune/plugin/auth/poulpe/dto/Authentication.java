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
 * <p>Java class for authentication complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="authentication">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="credentials" type="{http://www.jtalks.org/namespaces/1.0}credentials"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="statusInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profile" type="{http://www.jtalks.org/namespaces/1.0}profile" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guram Savinov
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "authentication", namespace = "http://www.jtalks.org/namespaces/1.0", propOrder = {
        "credentials",
        "status",
        "statusInfo",
        "profile"})
@XmlRootElement(name = "authentication", namespace = "http://www.jtalks.org/namespaces/1.0")
public class Authentication {

    @XmlElement(namespace = "http://www.jtalks.org/namespaces/1.0", required = true)
    protected Credentials credentials;
    @XmlElement(namespace = "http://www.jtalks.org/namespaces/1.0")
    protected String status;
    @XmlElement(namespace = "http://www.jtalks.org/namespaces/1.0")
    protected String statusInfo;
    @XmlElement(namespace = "http://www.jtalks.org/namespaces/1.0")
    protected Profile profile;

    /**
     * Creates an {@code Authentication} instance.
     */
    public Authentication() {
    }

    /**
     * Creates an {@code Authentication} instance with specified username.
     * Username will be set to the new {@code Credentials} instance.
     *
     * @param username the username
     */
    public Authentication(String username) {
        setCredintals(new Credentials(username));
    }

    /**
     * Gets the value of the credentials property.
     *
     * @return possible object is
     *         {@link Credentials }
     */
    public Credentials getCredintals() {
        return credentials;
    }

    /**
     * Sets the value of the credentials property.
     *
     * @param value allowed object is
     *              {@link Credentials }
     */
    public void setCredintals(Credentials value) {
        this.credentials = value;
    }

    /**
     * Gets the value of the status property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the statusInfo property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getStatusInfo() {
        return statusInfo;
    }

    /**
     * Sets the value of the statusInfo property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStatusInfo(String value) {
        this.statusInfo = value;
    }

    /**
     * Gets the value of the profile property.
     *
     * @return possible object is
     *         {@link Profile }
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     *
     * @param value allowed object is
     *              {@link Profile }
     */
    public void setProfile(Profile value) {
        this.profile = value;
    }

}
