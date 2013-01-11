package org.jtalks.jcommune.service.jmx;

import com.google.common.collect.Lists;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/** @author stanislav bashkirtsev */
public class Log4jConfiguratorTest {
    private Log4jConfigurator sut;

    @BeforeMethod
    public void initSutAndDocs() throws Exception {
        sut = spy(new Log4jConfigurator());
    }

    @Test
    public void getLoggersShouldReturnAllAvailableNames() {
        List<Logger> allLoggers = Lists.newArrayList(logger("a", "DEBUG"), logger("b", "INFO"));
        doReturn(allLoggers).when(sut).getAllLoggers();
        assertEquals(sut.getLoggers(), Lists.newArrayList("a = DEBUG", "b = INFO"));
    }

    @Test
    public void getLoggersShouldReturnEmptyListIfNoLoggersConfigured() {
        doReturn(Lists.newArrayList()).when(sut).getAllLoggers();
        assertTrue(sut.getLoggers().isEmpty());
    }

    @Test
    public void testGetLogLevel() throws Exception {
        doReturn(logger("a", "INFO")).when(sut).getLogger("a");
        assertEquals(sut.getLogLevel("a"), "INFO");
    }

    @Test
    public void getLogLevelReturnsUnavailableIfNoSuchLogger(){
        doReturn(null).when(sut).getLogger("a");
        assertEquals(sut.getLogLevel("a"), "unavailable");
    }

    @Test
    public void testSetLogLevel() throws Exception {
        doReturn(logger("a", "INFO")).when(sut).getLogger("a");
        sut.setLogLevel("a", "DEBUG");
        assertEquals(sut.getLogLevel("a"), "DEBUG");
    }

    private Logger logger(String name, String level){
        Logger logger = Logger.getLogger(name);
        logger = spy(logger);
        logger.setLevel(Level.toLevel(level));
        return logger;
    }
}
