package org.jtalks.jcommune.plugin.kaptcha;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class KaptchaPluginTest {

    @Test
    public void testGetHtml() throws Exception {

        KaptchaPlugin kaptchaPlugin = new KaptchaPlugin();
        String actual = kaptchaPlugin.getHtml(null);
        String expected = "<div class='control-group'>" +
                "            <div class='controls captcha-images'>" +
                "                <img id='captcha-img' alt='Captcha' src='http://localhost:8080/plugin/{captchaPluginId}/refreshCaptcha' />" +
                "                <img id='captcha-refresh' alt='Refresh captcha'  src='http://localhost:8080/resources/images/captcha-refresh.png' />" +
                "            </div>" +
                "            <div class='controls'>" +
                "                <input type='text' id='captcha' placeholder='Captcha' class='input-xlarge' />" +
                "            </div>" +
                "        </div> ";
        assertEquals(actual, expected);
    }
}
