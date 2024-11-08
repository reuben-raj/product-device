package com.e3.api.product_device.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;

import jakarta.servlet.http.HttpServletResponse;

@ActiveProfiles("unit")
public class ResponseWrapperTest {

    private ResponseWrapper responseWrapper;
    private HttpServletResponse mockResponse;

    @BeforeEach
    public void setUp() {
        mockResponse = Mockito.mock(HttpServletResponse.class);
        responseWrapper = new ResponseWrapper(mockResponse);
    }

    @Test
    void testFlushBuffer() throws IOException {
        PrintWriter writer = responseWrapper.getWriter();
        writer.write("flush test");

        responseWrapper.flushBuffer();

        assertEquals("flush test", new String(responseWrapper.getContentAsByteArray()));
    }

    @Test
    void testGetContentAsByteArray() throws IOException {
        responseWrapper.getWriter().write("content test");
        responseWrapper.getWriter().flush();

        byte[] content = responseWrapper.getContentAsByteArray();

        assertEquals("content test", new String(content));
    }

    @Test
    void testGetWriter() throws IOException {
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        ResponseWrapper responseWrapper = new ResponseWrapper(mockResponse);

        PrintWriter writer = responseWrapper.getWriter();
        writer.write("Test content");
        writer.flush();

        assertEquals("Test content", new String(responseWrapper.getContentAsByteArray()));

    }
}
