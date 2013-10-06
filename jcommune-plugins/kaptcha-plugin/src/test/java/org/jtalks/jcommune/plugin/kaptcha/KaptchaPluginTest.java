package org.jtalks.jcommune.plugin.kaptcha;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class KaptchaPluginTest {

    @Test
    public void testGetHtml() throws Exception {

        KaptchaPlugin kaptchaPlugin = new KaptchaPlugin();
        String actual = kaptchaPlugin.getHtml(null);
        String expected = "<div class='control-group'>\n" +
                "  <div class='controls captcha-images'>\n" +
                "    <img id='captcha-img' alt='Captcha' src='http://localhost:8080/plugin/1/refreshCaptcha'/>\n" +
                "    <img id='captcha-refresh' alt='Refresh captcha' src='http://localhost:8080/resources/images/captcha-refresh.png'/>\n" +
                "  </div>\n" +
                "  <div class='controls'><input type='text' id='captcha' placeholder='Captcha text' class='input-xlarge'/></div>\n" +
                "</div>";
        assertEquals(actual, expected);
    }
}
