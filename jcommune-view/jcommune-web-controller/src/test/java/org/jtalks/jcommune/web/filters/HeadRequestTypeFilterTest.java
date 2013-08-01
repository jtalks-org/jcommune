package org.jtalks.jcommune.web.filters;

import org.jtalks.jcommune.web.filters.wrapper.NoBodyResponseWrapper;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class HeadRequestTypeFilterTest {

    private MockHttpServletRequest request = new MockHttpServletRequest();
    private MockHttpServletResponse response = new MockHttpServletResponse();

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequestWrapper forceRequestWrapper;

    @Mock
    private NoBodyResponseWrapper noBodyResponseWrapper;

    private HeadRequestTypeFilter headRequestTypeFilter;

    @BeforeTest
    private void setUp() {
        MockitoAnnotations.initMocks(this);
        this.headRequestTypeFilter = new HeadRequestTypeFilter();
        this.headRequestTypeFilter.setForceRequestWrapper(this.forceRequestWrapper);
        this.headRequestTypeFilter.setNoBodyResponseWrapper(this.noBodyResponseWrapper);
    }

    @AfterTest
    private void tearDown() {
        this.headRequestTypeFilter = null;
        this.noBodyResponseWrapper = null;
        this.forceRequestWrapper   = null;
        this.filterChain = null;
    }

    @Test
    public void testCatchHeadRequest() throws IOException, ServletException {

        Mockito.mock(NoBodyResponseWrapper.class);

        this.request.setMethod("HEAD");
        this.headRequestTypeFilter.doFilter(this.request, this.response, this.filterChain);

        Mockito.verify(noBodyResponseWrapper, times(1)).setContentLength();
        assertEquals(0, this.response.getContentLength());
        assertEquals(200, this.response.getStatus());
    }

    @Test(dataProvider = "requestType")
    public void testCatchAnotherDifferentRequest(String requestType) throws IOException, ServletException {

        Mockito.mock(NoBodyResponseWrapper.class);

        this.request.setMethod(requestType);
        this.headRequestTypeFilter.doFilter(this.request, this.response, this.filterChain);

        Mockito.verify(noBodyResponseWrapper, never()).setContentLength();
    }

    @DataProvider
    public String[][] requestType() {
        return new String[][]{
                {"GET"},
                {"POST"},
        };
    }
}
