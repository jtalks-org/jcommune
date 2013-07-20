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
 * <p>Java class for message complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="message">
 *    &lt;simpleContent>
 *       &lt;extension base="string">
 *         &lt;attribute name="code" type="string"/>
 *       &lt;extension>
 *    &lt;simpleContent>
 * &lt;complexType>
 *
 * </pre>
 *
 * @author Mikhail Zaitsev
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "error", namespace = "http://www.jtalks.org/namespaces/1.0")
public class Error {

    @XmlAttribute
    private String code;

    @XmlValue
    private String message;

    /**
     * Constructs a object of this class
     */
    public Error() {
    }

    /**
     * Returns the code of message
     *
     * @return the code of message
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code of message
     *
     * @param code the code of message
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Returns the message of error
     *
     * @return the message of error
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message of error
     *
     * @param message the message of error
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
