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
package org.jtalks.jcommune.web.interceptors;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;

/** @author stanislav bashkirtsev */
public class SapeInterceptorTest {
    @Test
    public void constructorInitsDummyLinks() throws Exception {
        assertPropertyReflectionEquals("dummyLinks",
                fileContent("/org/jtalks/jcommune/web/interceptors/DummySapeLinks.txt"), new SapeInterceptor());

    }

    private String fileContent(String resourceName) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream(resourceName));

    }
}
