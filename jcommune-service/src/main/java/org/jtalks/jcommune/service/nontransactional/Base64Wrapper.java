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
package org.jtalks.jcommune.service.nontransactional;

import org.apache.commons.codec.binary.Base64;

/**
 * Wraps the static B64 encoder/decoder in a bean
 * for the sake of testability.
 *
 * If you want to encode string consider using a
 * {@link org.apache.commons.codec.binary.StringUtils#getBytesUtf8(String)}
 * as a preconversion step
 *
 * @author Evgeniy Naumenko
 */
public class Base64Wrapper {

    /**
     * Perform base64 binary data to string conversion.
     * If the data passed is null, then null will be returned as a result.
     *
     * @param bytes for processing
     * @return converted binary data string
     */
    public String encodeB64Bytes(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /**
     * Perform base64 string to binary data conversion.
     * If the data passed is null, then null will be returned as a result.
     *
     * @param encodedBytes string representation for processing
     * @return converted binary data
     */
    public byte[] decodeB64Bytes(String encodedBytes) {
        byte[] result = null;
        if (encodedBytes != null) {
            result = Base64.decodeBase64(encodedBytes);
        }
        return result;
    }
}
