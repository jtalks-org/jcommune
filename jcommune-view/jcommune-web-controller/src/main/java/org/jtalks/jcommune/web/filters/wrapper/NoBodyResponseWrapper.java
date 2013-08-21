package org.jtalks.jcommune.web.filters.wrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class NoBodyResponseWrapper extends HttpServletResponseWrapper {

    private final NoBodyOutputStream noBodyOutputStream = new NoBodyOutputStream();
    private PrintWriter writer;

    public NoBodyResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return noBodyOutputStream;
    }

    public PrintWriter getWriter() throws UnsupportedEncodingException {

        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(noBodyOutputStream, getCharacterEncoding()));
        }

        return writer;
    }

    public void setContentLength() {
        super.setContentLength(noBodyOutputStream.getContentLength());
    }

    private class NoBodyOutputStream extends ServletOutputStream {

        private int contentLength = 0;

        public int getContentLength() {
            return contentLength;
        }

        public void write(int b) throws IOException {
            contentLength++;
        }

        public void write(byte buf[], int offset, int len) throws IOException {
            contentLength += len;
        }
    }
}