/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Temdegon
 */
public class newTestNGTest {
    
    @BeforeClass
    public void setUp() {
        // code that will be invoked before this test starts
    }
    
    @Test
    public void aTest() {
        System.out.println("Test");
    }
    
    @AfterClass
    public void cleanUp() {
        // code that will be invoked after this test ends
    }
}
