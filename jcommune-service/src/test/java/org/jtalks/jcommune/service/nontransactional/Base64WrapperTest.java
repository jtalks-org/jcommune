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

import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Evgeniy Naumenko
 */
public class Base64WrapperTest {

    private Base64Wrapper wrapper = new Base64Wrapper();

    private byte[] rawData = new byte[]{1, 2, 3, 4, 5};
    private String encodedData = "AQIDBAU=";


    @Test
    public void testBase64Encoder() {
        String result = wrapper.encodeB64Bytes(rawData);

        assertEquals(result, encodedData);
    }

    @Test
    public void testBase64Decoder()  {
        byte[] result = wrapper.decodeB64Bytes(encodedData);

        assertEquals(result, rawData);
    }

    @Test
    public void testBase64EncoderNullData() {
        String result = wrapper.encodeB64Bytes(null);

        assertEquals(result, null);
    }

    @Test
    public void testBase64DecoderNullData() {
        byte[] result = wrapper.decodeB64Bytes(null);

        assertEquals(result, null);
    }
}
