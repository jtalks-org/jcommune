package org.jtalks.jcommune.model.entity;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SimplePageTest {

    private SamplePage page;
    @BeforeMethod
    public void setUp() throws Exception {
        page = new SamplePage("Test Name", "Test Content");

    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(page.getName(), "Test Name");
    }

    @Test
    public void testGetContent() throws Exception {
        assertEquals(page.getName(), "Test Content");
    }

}
