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
import java.util.List;

/**
 * <p>Java class for errors complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="errors">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="credentials" type="{http://www.jtalks.org/namespaces/1.0}error"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Mikhail Zaitsev
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "errors", namespace = "http://www.jtalks.org/namespaces/1.0")
@XmlRootElement(name = "errors", namespace = "http://www.jtalks.org/namespaces/1.0")
public class Errors {

    @XmlElement(name = "error", namespace = "http://www.jtalks.org/namespaces/1.0", required = true)
    private List<Error> errorList;

    /**
     * Constructs the object of this class
     */
    public Errors() {
    }

    /**
     * Returns the list of errors
     *
     * @return the list of errors
     */
    public List<Error> getErrorList() {
        return errorList;
    }

    /**
     * Sets the list of errors
     *
     * @param errorList the list of errors
     */
    public void setErrorList(List<Error> errorList) {
        this.errorList = errorList;
    }
}
