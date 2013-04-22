package org.jtalks.jcommune.web.interceptors;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;

/** @author stanislav bashkirtsev */
public class JavaSapeInterceptorTest {
    @Test
    public void constructorInitsDummyLinks() throws Exception {
        assertPropertyReflectionEquals("dummyLinks",
                fileContent("/org/jtalks/jcommune/web/interceptors/DummySapeLinks.txt"), new JavaSapeInterceptor());

    }

    private String fileContent(String resourceName) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream(resourceName));

    }
}
